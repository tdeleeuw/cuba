/*
 * Copyright (c) 2008-2016 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesUtils;
import com.haulmont.cuba.core.app.dynamicattributes.PropertyType;
import com.haulmont.cuba.core.entity.CategoryAttribute;
import com.haulmont.cuba.core.entity.LocaleHelper;
import com.haulmont.cuba.core.global.MetadataTools;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.table.CollectionContainerTableSource;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import com.haulmont.cuba.gui.dynamicattributes.DynamicAttributesGuiTools;
import com.haulmont.cuba.gui.model.*;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.gui.xml.DeclarativeColumnGenerator;
import com.haulmont.cuba.gui.xml.layout.ComponentLoader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractTableLoader<T extends Table> extends ActionsHolderLoader<T> {

    protected ComponentLoader buttonsPanelLoader;
    protected Element panelElement;

    @Override
    public void loadComponent() {
        assignXmlDescriptor(resultComponent, element);
        assignFrame(resultComponent);

        loadEnable(resultComponent, element);
        loadVisible(resultComponent, element);
        loadEditable(resultComponent, element);
        loadValidators(resultComponent, element);
        loadSettingsEnabled(resultComponent, element);

        loadAlign(resultComponent, element);
        loadStyleName(resultComponent, element);

        loadHeight(resultComponent, element);
        loadWidth(resultComponent, element);

        loadIcon(resultComponent, element);
        loadCaption(resultComponent, element);
        loadDescription(resultComponent, element);
        loadContextHelp(resultComponent, element);

        loadTabIndex(resultComponent, element);

        loadSortable(resultComponent, element);
        loadReorderingAllowed(resultComponent, element);
        loadColumnControlVisible(resultComponent, element);
        loadAggregatable(resultComponent, element);
        loadAggregationStyle(resultComponent, element);

        loadPresentations(resultComponent, element);

        loadActions(resultComponent, element);
        loadContextMenuEnabled(resultComponent, element);
        loadMultiLineCells(resultComponent, element);

        loadColumnHeaderVisible(resultComponent, element);
        loadShowSelection(resultComponent, element);
        loadTextSelectionEnabled(resultComponent, element);
        loadResponsive(resultComponent, element);

        loadHeaderContentMode(resultComponent, element);

        Element columnsElement = element.element("columns");
        Element rowsElement = element.element("rows");

        if (rowsElement == null) {
            throw new GuiDevelopmentException("Table doesn't have 'rows' element", context.getCurrentFrameId(),
                    "Table ID", element.attributeValue("id"));
        }

        String rowHeaderMode = rowsElement.attributeValue("rowHeaderMode");
        if (StringUtils.isBlank(rowHeaderMode)) {
            rowHeaderMode = rowsElement.attributeValue("headerMode");
            if (StringUtils.isNotBlank(rowHeaderMode)) {
                Logger log = LoggerFactory.getLogger(AbstractTableLoader.class);
                log.warn("Attribute headerMode is deprecated. Use rowHeaderMode.");
            }
        }

        if (!StringUtils.isEmpty(rowHeaderMode)) {
            resultComponent.setRowHeaderMode(Table.RowHeaderMode.valueOf(rowHeaderMode));
        }

        loadButtonsPanel(resultComponent);

        loadRowsCount(resultComponent, element); // must be before datasource setting

        MetaClass metaClass;
        CollectionContainer collectionContainer = null;
        DataLoader dataLoader = null;
        Datasource datasource = null;

        String containerId = rowsElement.attributeValue("container");
        if (containerId != null) {
            FrameOwner frameOwner = context.getFrame().getFrameOwner();
            ScreenData screenData = UiControllerUtils.getScreenData(frameOwner);
            InstanceContainer container = screenData.getContainer(containerId);
            if (container instanceof CollectionContainer) {
                collectionContainer = (CollectionContainer) container;
            } else {
                throw new GuiDevelopmentException("Not a CollectionContainer: " + containerId, context.getCurrentFrameId());
            }
            metaClass = collectionContainer.getEntityMetaClass();
            if (collectionContainer instanceof HasLoader) {
                dataLoader = ((HasLoader) collectionContainer).getLoader();
            }

        } else {
            String datasourceId = rowsElement.attributeValue("datasource");
            if (StringUtils.isBlank(datasourceId)) {
                throw new GuiDevelopmentException("Table 'rows' element doesn't have 'datasource' attribute",
                        context.getCurrentFrameId(), "Table ID", element.attributeValue("id"));
            }

            datasource = context.getDsContext().get(datasourceId);
            if (datasource == null) {
                throw new GuiDevelopmentException("Can't find datasource by name: " + datasourceId, context.getCurrentFrameId());
            }

            if (!(datasource instanceof CollectionDatasource)) {
                throw new GuiDevelopmentException("Not a CollectionDatasource: " + datasourceId, context.getCurrentFrameId());
            }

            metaClass = datasource.getMetaClass();
        }

        List<Table.Column> availableColumns;

        if (columnsElement != null) {
            availableColumns = loadColumns(resultComponent, columnsElement, metaClass);
        } else {
            availableColumns = new ArrayList<>();
        }

        for (Table.Column column : availableColumns) {
            resultComponent.addColumn(column);
            loadValidators(resultComponent, column);
            loadRequired(resultComponent, column);
        }

        if (collectionContainer != null) {
            if (dataLoader instanceof CollectionLoader) {
                addDynamicAttributes(resultComponent, metaClass, null, (CollectionLoader) dataLoader, availableColumns);
            }
            //noinspection unchecked
            resultComponent.setTableSource(createContainerTableSource(collectionContainer));
        } else {
            addDynamicAttributes(resultComponent, metaClass, datasource, null, availableColumns);
            resultComponent.setDatasource((CollectionDatasource) datasource);
        }

        for (Table.Column column : availableColumns) {
            if (column.getXmlDescriptor() != null) {
                String generatorMethod = column.getXmlDescriptor().attributeValue("generator");
                if (StringUtils.isNotEmpty(generatorMethod)) {
                    //noinspection unchecked
                    resultComponent.addGeneratedColumn(String.valueOf(column),
                            beanLocator.getPrototype(DeclarativeColumnGenerator.NAME, resultComponent, generatorMethod));
                }
            }
        }

        String multiselect = element.attributeValue("multiselect");
        if (StringUtils.isNotEmpty(multiselect)) {
            resultComponent.setMultiSelect(Boolean.parseBoolean(multiselect));
        }
    }

    protected void loadHeaderContentMode(Table component, Element element) {
        String hcm = element.attributeValue("headerContentMode");
        if (hcm != null && !hcm.isEmpty()) {
            Table.HeaderContentMode headerContentMode = Table.HeaderContentMode.valueOf(hcm);
            component.setHeaderContentMode(headerContentMode);
        }
    }

    @SuppressWarnings("unchecked")
    protected CollectionContainerTableSource createContainerTableSource(CollectionContainer container) {
        return new CollectionContainerTableSource(container);
    }

    protected MetadataTools getMetadataTools() {
        return beanLocator.get(MetadataTools.NAME);
    }

    protected DynamicAttributesGuiTools getDynamicAttributesGuiTools() {
        return beanLocator.get(DynamicAttributesGuiTools.NAME);
    }

    protected void loadTextSelectionEnabled(Table table, Element element) {
        String textSelectionEnabled = element.attributeValue("textSelectionEnabled");
        if (StringUtils.isNotEmpty(textSelectionEnabled)) {
            table.setTextSelectionEnabled(Boolean.parseBoolean(textSelectionEnabled));
        }
    }

    protected void addDynamicAttributes(Table component, MetaClass metaClass, Datasource ds, CollectionLoader collectionLoader,
                                        List<Table.Column> availableColumns) {
        if (getMetadataTools().isPersistent(metaClass)) {
            Set<CategoryAttribute> attributesToShow =
                    getDynamicAttributesGuiTools().getAttributesToShowOnTheScreen(metaClass, context.getFullFrameId(), component.getId());
            if (CollectionUtils.isNotEmpty(attributesToShow)) {
                if (collectionLoader != null) {
                    collectionLoader.setLoadDynamicAttributes(true);
                } else if (ds != null) {
                    ds.setLoadDynamicAttributes(true);
                }
                for (CategoryAttribute attribute : attributesToShow) {
                    final MetaPropertyPath metaPropertyPath = DynamicAttributesUtils.getMetaPropertyPath(metaClass, attribute);

                    Object columnWithSameId = IterableUtils.find(availableColumns,
                            o -> o.getId().equals(metaPropertyPath));

                    if (columnWithSameId != null) {
                        continue;
                    }

                    final Table.Column column = new Table.Column(metaPropertyPath);

                    column.setCaption(LocaleHelper.isLocalizedValueDefined(attribute.getLocaleNames()) ?
                            attribute.getLocaleName() :
                            StringUtils.capitalize(attribute.getName()));

                    if (attribute.getDataType().equals(PropertyType.STRING)) {
                        ClientConfig clientConfig = getConfiguration().getConfig(ClientConfig.class);
                        column.setMaxTextLength(clientConfig.getDynamicAttributesTableColumnMaxTextLength());
                    }

                    if (attribute.getDataType().equals(PropertyType.ENUMERATION)) {
                        column.setFormatter(value ->
                                LocaleHelper.getEnumLocalizedValue((String) value, attribute.getEnumerationLocales())
                        );
                    }
                    component.addColumn(column);
                }
            }

            if (ds != null) {
                getDynamicAttributesGuiTools().listenDynamicAttributesChanges(ds);
            }
        }
    }

    protected void loadMultiLineCells(Table table, Element element) {
        String multiLineCells = element.attributeValue("multiLineCells");
        if (StringUtils.isNotEmpty(multiLineCells)) {
            table.setMultiLineCells(Boolean.parseBoolean(multiLineCells));
        }
    }

    protected void loadContextMenuEnabled(Table table, Element element) {
        String contextMenuEnabled = element.attributeValue("contextMenuEnabled");
        if (StringUtils.isNotEmpty(contextMenuEnabled)) {
            table.setContextMenuEnabled(Boolean.parseBoolean(contextMenuEnabled));
        }
    }

    protected void loadRowsCount(Table table, Element element) {
        Element rowsCountElement = element.element("rowsCount");
        if (rowsCountElement != null) {
            RowsCount rowsCount = factory.create(RowsCount.class);
            rowsCount.setOwner(table);
            table.setRowsCount(rowsCount);
        }
    }

    protected List<Table.Column> loadColumns(Table component, Element columnsElement, MetaClass metaClass) {
        List<Table.Column> columns = new ArrayList<>();
        //noinspection unchecked
        for (Element columnElement : columnsElement.elements("column")) {
            String visible = columnElement.attributeValue("visible");
            if (StringUtils.isEmpty(visible) || Boolean.parseBoolean(visible)) {
                columns.add(loadColumn(columnElement, metaClass));
            }
        }
        return columns;
    }

    protected void loadAggregatable(Table component, Element element) {
        String aggregatable = element.attributeValue("aggregatable");
        if (StringUtils.isNotEmpty(aggregatable)) {
            component.setAggregatable(Boolean.parseBoolean(aggregatable));
            String showTotalAggregation = element.attributeValue("showTotalAggregation");
            if (StringUtils.isNotEmpty(showTotalAggregation)) {
                component.setShowTotalAggregation(Boolean.parseBoolean(showTotalAggregation));
            }
        }
    }

    protected void loadAggregationStyle(Table component, Element element) {
        String aggregationStyle = element.attributeValue("aggregationStyle");
        if (!StringUtils.isEmpty(aggregationStyle)) {
            component.setAggregationStyle(Table.AggregationStyle.valueOf(aggregationStyle));
        }
    }

    protected void createButtonsPanel(T table, Element element) {
        panelElement = element.element("buttonsPanel");
        if (panelElement != null) {
            ButtonsPanelLoader loader = (ButtonsPanelLoader) getLoader(panelElement, ButtonsPanel.NAME);
            loader.createComponent();
            ButtonsPanel panel = loader.getResultComponent();

            table.setButtonsPanel(panel);

            buttonsPanelLoader = loader;
        }
    }

    @Override
    public void setMessagesPack(String messagesPack) {
        super.setMessagesPack(messagesPack);

        if (buttonsPanelLoader != null) {
            buttonsPanelLoader.setMessagesPack(messagesPack);
        }
    }

    protected void loadButtonsPanel(T component) {
        if (buttonsPanelLoader != null) {
            //noinspection unchecked
            buttonsPanelLoader.loadComponent();
            ButtonsPanel panel = (ButtonsPanel) buttonsPanelLoader.getResultComponent();

            Window window = ComponentsHelper.getWindowImplementation(component);
            String alwaysVisible = panelElement.attributeValue("alwaysVisible");
            panel.setVisible(!(window instanceof Window.Lookup) || "true".equals(alwaysVisible));
        }
    }

    @SuppressWarnings("unchecked")
    protected void loadRequired(Table component, Table.Column column) {
        Element element = column.getXmlDescriptor();
        String required = element.attributeValue("required");
        if (StringUtils.isNotEmpty(required)) {
            String requiredMsg = element.attributeValue("requiredMessage");
            component.setRequired(column, Boolean.parseBoolean(required), loadResourceString(requiredMsg));
        }
    }

    protected void loadValidators(Table component, Table.Column column) {
        @SuppressWarnings("unchecked")
        List<Element> validatorElements = column.getXmlDescriptor().elements("validator");

        if (!validatorElements.isEmpty()) {
            for (Element validatorElement : validatorElements) {
                Field.Validator validator = loadValidator(validatorElement);
                if (validator != null) {
                    component.addValidator(column, validator);
                }
            }
        } else if (column.isEditable()) {
            if (!(column.getId() instanceof MetaPropertyPath)) {
                throw new GuiDevelopmentException(String.format("Column '%s' has editable=true, but there is no " +
                        "property of an entity with this id", column.getId()), context.getCurrentFrameId());
            }

            MetaPropertyPath propertyPath = (MetaPropertyPath) column.getId();
            Field.Validator validator = getDefaultValidator(propertyPath.getMetaProperty());
            if (validator != null) {
                component.addValidator(column, validator);
            }
        }
    }

    protected Table.Column loadColumn(Element element, MetaClass metaClass) {
        String id = element.attributeValue("id");

        MetaPropertyPath metaPropertyPath = getMetadataTools().resolveMetaPropertyPath(metaClass, id);

        Table.Column column = new Table.Column(metaPropertyPath != null ? metaPropertyPath : id);

        String editable = element.attributeValue("editable");
        if (StringUtils.isNotEmpty(editable)) {
            column.setEditable(Boolean.parseBoolean(editable));
        }

        String collapsed = element.attributeValue("collapsed");
        if (StringUtils.isNotEmpty(collapsed)) {
            column.setCollapsed(Boolean.parseBoolean(collapsed));
        }

        String groupAllowed = element.attributeValue("groupAllowed");
        if (StringUtils.isNotEmpty(groupAllowed)) {
            column.setGroupAllowed(Boolean.parseBoolean(groupAllowed));
        }

        String sortable = element.attributeValue("sortable");
        if (StringUtils.isNotEmpty(sortable)) {
            column.setSortable(Boolean.parseBoolean(sortable));
        }

        loadCaption(column, element);
        loadDescription(column, element);

        if (column.getCaption() == null) {
            String columnCaption;
            if (column.getId() instanceof MetaPropertyPath) {
                MetaPropertyPath mpp = (MetaPropertyPath) column.getId();
                MetaProperty metaProperty = mpp.getMetaProperty();
                String propertyName = metaProperty.getName();

                if (DynamicAttributesUtils.isDynamicAttribute(metaProperty)) {
                    CategoryAttribute categoryAttribute = DynamicAttributesUtils.getCategoryAttribute(metaProperty);

                    columnCaption = LocaleHelper.isLocalizedValueDefined(categoryAttribute.getLocaleNames()) ?
                            categoryAttribute.getLocaleName() :
                            StringUtils.capitalize(categoryAttribute.getName());
                } else {
                    MetaClass propertyMetaClass = getMetadataTools().getPropertyEnclosingMetaClass(mpp);
                    columnCaption = getMessageTools().getPropertyCaption(propertyMetaClass, propertyName);
                }
            } else {
                Class<?> declaringClass = metaClass.getJavaClass();
                String className = declaringClass.getName();
                int i = className.lastIndexOf('.');
                if (i > -1)
                    className = className.substring(i + 1);
                columnCaption = getMessages().getMessage(declaringClass, className + "." + id);
            }
            column.setCaption(columnCaption);
        }

        column.setXmlDescriptor(element);
        if (metaPropertyPath != null)
            column.setType(metaPropertyPath.getRangeJavaClass());

        String width = loadThemeString(element.attributeValue("width"));
        if (!StringUtils.isBlank(width)) {
            if (StringUtils.endsWith(width, "px")) {
                width = StringUtils.substring(width, 0, width.length() - 2);
            }
            try {
                column.setWidth(Integer.parseInt(width));
            } catch (NumberFormatException e) {
                throw new GuiDevelopmentException("Property 'width' must contain only numeric value",
                        context.getCurrentFrameId(), "width", element.attributeValue("width"));
            }
        }
        String align = element.attributeValue("align");
        if (StringUtils.isNotBlank(align)) {
            column.setAlignment(Table.ColumnAlignment.valueOf(align));
        }

        column.setFormatter(loadFormatter(element));

        loadAggregation(column, element);
        loadCalculatable(column, element);
        loadMaxTextLength(column, element);

        return column;
    }

    protected void loadAggregation(Table.Column column, Element columnElement) {
        Element aggregationElement = columnElement.element("aggregation");
        if (aggregationElement != null) {
            final AggregationInfo aggregation = new AggregationInfo();
            aggregation.setPropertyPath((MetaPropertyPath) column.getId());
            String aggregationType = aggregationElement.attributeValue("type");
            if (StringUtils.isNotEmpty(aggregationType)) {
                aggregation.setType(AggregationInfo.Type.valueOf(aggregationType));
            }

            String valueDescription = aggregationElement.attributeValue("valueDescription");
            if (StringUtils.isNotEmpty(valueDescription)) {
                column.setValueDescription(loadResourceString(valueDescription));
            }

            Function formatter = loadFormatter(aggregationElement);
            //noinspection unchecked
            aggregation.setFormatter(formatter == null ? column.getFormatter() : formatter);
            column.setAggregation(aggregation);

            String strategyClass = aggregationElement.attributeValue("strategyClass");
            if (StringUtils.isNotEmpty(strategyClass)) {
                Class<?> aggregationClass = getScripting().loadClass(strategyClass);
                if (aggregationClass == null) {
                    throw new GuiDevelopmentException(String.format("Class %s is not found", strategyClass), context.getFullFrameId());
                }

                try {
                    Constructor<?> constructor = aggregationClass.getDeclaredConstructor();
                    AggregationStrategy customStrategy = (AggregationStrategy) constructor.newInstance();
                    aggregation.setStrategy(customStrategy);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to instantiate strategy for aggregation", e);
                }
            }

            if (aggregationType == null && strategyClass == null) {
                throw new GuiDevelopmentException("Incorrect aggregation - type or strategyClass is required", context.getFullFrameId());
            }
        }
    }

    protected void loadCalculatable(Table.Column column, Element columnElement) {
        String calc = columnElement.attributeValue("calculatable");
        if (StringUtils.isNotEmpty(calc)) {
            column.setCalculatable(Boolean.parseBoolean(calc));
        }
    }

    protected void loadMaxTextLength(Table.Column column, Element columnElement) {
        String maxTextLength = columnElement.attributeValue("maxTextLength");
        if (!StringUtils.isBlank(maxTextLength)) {
            column.setMaxTextLength(Integer.parseInt(maxTextLength));
        }
    }

    protected void loadValidators(Table component, Element element) {
        @SuppressWarnings("unchecked")
        List<Element> validatorElements = element.elements("validator");

        for (Element validatorElement : validatorElements) {
            final Field.Validator validator = loadValidator(validatorElement);
            if (validator != null) {
                component.addValidator(validator);
            }
        }
    }

    protected void loadSortable(Table component, Element element) {
        String sortable = element.attributeValue("sortable");
        if (StringUtils.isNotEmpty(sortable)) {
            component.setSortable(Boolean.parseBoolean(sortable));
        }
    }

    protected void loadReorderingAllowed(Table component, Element element) {
        String reorderingAllowed = element.attributeValue("reorderingAllowed");
        if (StringUtils.isNotEmpty(reorderingAllowed)) {
            component.setColumnReorderingAllowed(Boolean.parseBoolean(reorderingAllowed));
        }
    }

    protected void loadColumnControlVisible(Table component, Element element) {
        String columnControlVisible = element.attributeValue("columnControlVisible");
        if (StringUtils.isNotEmpty(columnControlVisible)) {
            component.setColumnControlVisible(Boolean.parseBoolean(columnControlVisible));
        }
    }

    protected void loadColumnHeaderVisible(Table component, Element element) {
        String columnHeaderVisible = element.attributeValue("columnHeaderVisible");
        if (StringUtils.isNotEmpty(columnHeaderVisible)) {
            component.setColumnHeaderVisible(Boolean.parseBoolean(columnHeaderVisible));
        }
    }

    protected void loadShowSelection(Table component, Element element) {
        String showSelection = element.attributeValue("showSelection");
        if (StringUtils.isNotEmpty(showSelection)) {
            component.setShowSelection(Boolean.parseBoolean(showSelection));
        }
    }
}