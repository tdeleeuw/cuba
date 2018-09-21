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

package com.haulmont.cuba.web.widgets.client.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.haulmont.cuba.web.widgets.CubaTable;
import com.haulmont.cuba.web.widgets.client.tableshared.CubaTableShortcutActionHandler;
import com.haulmont.cuba.web.widgets.client.tableshared.TableCellClickListener;
import com.vaadin.client.*;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import com.vaadin.v7.client.ui.table.TableConnector;

import java.util.Arrays;
import java.util.HashSet;

import static com.haulmont.cuba.web.widgets.client.Tools.findCurrentOrParentTd;

@Connect(value = CubaTable.class, loadStyle = Connect.LoadStyle.EAGER)
public class CubaScrollTableConnector extends TableConnector {

    protected HandlerRegistration tooltipHandlerRegistration;

    public CubaScrollTableConnector() {
        registerRpc(CubaTableClientRpc.class, new CubaTableClientRpc() {
            @Override
            public void hidePresentationsPopup() {
                if (getWidget()._delegate.presentationsEditorPopup != null) {
                    getWidget()._delegate.presentationsEditorPopup.hide();
                }
            }

            @Override
            public void hideContextMenuPopup() {
                if (getWidget()._delegate.customContextMenuPopup != null) {
                    getWidget()._delegate.customContextMenuPopup.hide();
                }
            }

            @Override
            public void showCustomPopup() {
                getWidget().showCustomPopup();
            }

            @Override
            public void requestFocus(String itemKey, String columnKey) {
                getWidget().requestFocus(itemKey, columnKey);
            }
        });
    }

    @Override
    public CubaTableState getState() {
        return (CubaTableState) super.getState();
    }

    @Override
    public CubaScrollTableWidget getWidget() {
        return (CubaScrollTableWidget) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("textSelectionEnabled")) {
            getWidget()._delegate.textSelectionEnabled = getState().textSelectionEnabled;

            if (getWidget().getTotalRows() > 0) {
                getWidget().updateTextSelection();
            }
        }
        if (stateChangeEvent.hasPropertyChanged("contextMenuEnabled")) {
            getWidget()._delegate.contextMenuEnabled = getState().contextMenuEnabled;
        }
        if (stateChangeEvent.hasPropertyChanged("presentations")) {
            if (getState().presentations != null) {
                ComponentConnector presentations = (ComponentConnector) getState().presentations;
                getWidget().setPresentationsMenu(presentations.getWidget());
            } else {
                getWidget().setPresentationsMenu(null);
            }
        }
        if (stateChangeEvent.hasPropertyChanged("contextMenu")) {
            if (getState().contextMenu != null) {
                ComponentConnector contextMenu = (ComponentConnector) getState().contextMenu;
                getWidget()._delegate.customContextMenu = contextMenu.getWidget();
            } else {
                getWidget()._delegate.customContextMenu = null;
            }
        }
        if (stateChangeEvent.hasPropertyChanged("multiLineCells")) {
            getWidget()._delegate.multiLineCells = getState().multiLineCells;
        }
        if (stateChangeEvent.hasPropertyChanged("clickableColumnKeys")) {
            if (getState().clickableColumnKeys != null) {
                getWidget()._delegate.clickableColumns = new HashSet<>(Arrays.asList(getState().clickableColumnKeys));
            } else {
                getWidget()._delegate.clickableColumns = null;
            }
        }
        if (stateChangeEvent.hasPropertyChanged("customPopup")) {
            if (getState().customPopup != null) {
                ComponentConnector customPopup = (ComponentConnector) getState().customPopup;
                getWidget()._delegate.customPopupWidget = customPopup.getWidget();
            } else {
                getWidget()._delegate.customPopupWidget = null;
            }
        }
        if (stateChangeEvent.hasPropertyChanged("customPopupAutoClose")) {
            getWidget()._delegate.customPopupAutoClose = getState().customPopupAutoClose;
        }
        if (stateChangeEvent.hasPropertyChanged("tableSortResetLabel")) {
            getWidget()._delegate.tableSortResetLabel = getState().tableSortResetLabel;
        }
        if (stateChangeEvent.hasPropertyChanged("tableSortAscendingLabel")) {
            getWidget()._delegate.tableSortAscendingLabel = getState().tableSortAscendingLabel;
        }
        if (stateChangeEvent.hasPropertyChanged("tableSortDescendingLabel")) {
            getWidget()._delegate.tableSortDescendingLabel = getState().tableSortDescendingLabel;
        }
        if (stateChangeEvent.hasPropertyChanged("htmlCaptionColumns")) {
            if (getState().htmlCaptionColumns != null) {
                getWidget()._delegate.htmlCaptionColumns = new HashSet<>(Arrays.asList(getState().htmlCaptionColumns));
            } else {
                getWidget()._delegate.htmlCaptionColumns = null;
            }
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(Element element) {
        if (getState().columnDescriptions != null) {
            Element targetHeaderElement = findCurrentOrParentTd(element);
            if (targetHeaderElement != null
                    && targetHeaderElement.hasClassName("v-table-header-cell")) {
                // if column has description
                int childIndex = DOM.getChildIndex(targetHeaderElement.getParentElement(), targetHeaderElement);

                String columnKey = getWidget().tHead.getHeaderCell(childIndex).getColKey();

                if (columnKey != null) {
                    String columnDescription = getState().columnDescriptions.get(columnKey);
                    if (columnDescription != null && !columnDescription.isEmpty()) {
                        return new TooltipInfo(columnDescription);
                    }
                }
            }
        }

        if (getState().aggregationDescriptions != null) {
            Element targetAggregatedElement = findCurrentOrParentTd(element);
            if (targetAggregatedElement != null
                    && (targetAggregatedElement.hasClassName("v-table-aggregation-cell")
                        || targetAggregatedElement.getFirstChildElement().hasClassName("v-table-footer-container"))) {
                int childIndex = DOM.getChildIndex(targetAggregatedElement.getParentElement(), targetAggregatedElement);

                String columnKey = getWidget().tHead.getHeaderCell(childIndex).getColKey();
                if (columnKey != null) {
                    String columnTooltip = getState().aggregationDescriptions.get(columnKey);
                    if (columnTooltip != null && !columnTooltip.isEmpty()) {
                        return new TooltipInfo(columnTooltip);
                    }
                }
            }
        }

        if (element != getWidget().getElement()) {
            Object node = WidgetUtil.findWidget(element, CubaScrollTableWidget.CubaScrollTableBody.CubaScrollTableRow.class);

            if (node != null) {
                CubaScrollTableWidget.CubaScrollTableBody.CubaScrollTableRow row
                        = (CubaScrollTableWidget.CubaScrollTableBody.CubaScrollTableRow) node;
                return row.getTooltip(element);
            }
        }

        return super.getTooltipInfo(element);
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);

        if (uidl.hasVariable("collapsedcolumns")) {
            getWidget().addStyleName("collapsing-allowed");
        } else {
            getWidget().removeStyleName("collapsing-allowed");
        }

        // We may have actions attached to this table
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 0; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("shortcuts")) {
                    if (getWidget().getShortcutActionHandler() == null) {
                        getWidget().setShortcutActionHandler(new CubaTableShortcutActionHandler(uidl.getId(), client, this));
                    }
                    getWidget().getShortcutActionHandler().updateActionMap(childUidl);
                }
            }
        }
    }

    @Override
    protected void updateAdditionalRowData(UIDL uidl) {
        UIDL arow = uidl.getChildByTagName("arow");
        if (arow != null) {
            getWidget().updateAggregationRow(arow);
        }
    }

    @Override
    protected void init() {
        super.init();

        getWidget()._delegate.cellClickListener = new TableCellClickListener() {
            @Override
            public void onClick(String columnKey, int rowKey) {
                getRpcProxy(CubaTableServerRpc.class).onClick(columnKey, String.valueOf(rowKey));
            }
        };

        tooltipHandlerRegistration = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (event.getTypeInt() != Event.ONMOUSEMOVE
                        || !Element.is(event.getNativeEvent().getEventTarget())) {
                    return;
                }

                Element element = Element.as(event.getNativeEvent().getEventTarget());
                if ("div".equalsIgnoreCase(element.getTagName())) {
                    String className = element.getClassName();
                    if (className != null && (className.contains("v-table-caption-container")
                            || className.contains("v-table-footer-container"))) {
                        DomEvent.fireNativeEvent(event.getNativeEvent(), getWidget());
                    }
                }
            }
        });
    }

    @Override
    public void onUnregister() {
        if (tooltipHandlerRegistration != null) {
            tooltipHandlerRegistration.removeHandler();
            tooltipHandlerRegistration = null;
        }
        super.onUnregister();
    }
}