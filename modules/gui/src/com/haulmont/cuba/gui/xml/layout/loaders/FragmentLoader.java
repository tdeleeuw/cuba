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

import com.haulmont.cuba.core.global.DevelopmentException;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.FrameContext;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Fragment;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.components.sys.FrameImplementation;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.DsContext;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.logging.UIPerformanceLogger;
import com.haulmont.cuba.gui.logging.UIPerformanceLogger.LifeCycle;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiControllerUtils;
import com.haulmont.cuba.gui.screen.compatibility.LegacyFrame;
import com.haulmont.cuba.gui.sys.*;
import com.haulmont.cuba.gui.xml.data.DsContextLoader;
import com.haulmont.cuba.gui.xml.layout.ComponentRootLoader;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.haulmont.cuba.gui.screen.FrameOwner.NO_OPTIONS;

public class FragmentLoader extends ContainerLoader<Fragment> implements ComponentRootLoader<Fragment> {

    protected String frameId;

    protected void initCompanion(Element companionsElem, AbstractFrame frame) {
        String clientTypeId = AppConfig.getClientType().toString().toLowerCase();
        Element element = companionsElem.element(clientTypeId);
        if (element != null) {
            String className = element.attributeValue("class");
            if (!StringUtils.isBlank(className)) {
                Class aClass = getScripting().loadClassNN(className);
                Object companion;
                try {
                    companion = aClass.newInstance();
                    frame.setCompanion(companion);

                    CompanionDependencyInjector cdi = new CompanionDependencyInjector(frame, companion);
                    cdi.setBeanLocator(beanLocator);
                    cdi.inject();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to init companion for frame", e);
                }
            }
        }
    }

    protected void loadMessagesPack(Frame frame, Element element) {
        String msgPack = element.attributeValue("messagesPack");
        if (msgPack != null) {
            setMessagesPack(msgPack);
        } else {
            setMessagesPack(this.messagesPack);
        }
    }

    protected ScreenViewsLoader getScreenViewsLoader() {
        return beanLocator.get(ScreenViewsLoader.NAME);
    }

    @Override
    public void createComponent() {
        Fragment fragment = factory.createComponent(Fragment.NAME);
        fragment.setId(frameId);

        loadMessagesPack(fragment, element);

        Element layoutElement = element.element("layout");
        createContent(layoutElement);

        resultComponent = fragment;
    }

    public void setResultComponent(Fragment fragment) {
        this.resultComponent = fragment;
    }

    @Override
    public void createContent(Element layoutElement) {
        if (layoutElement == null) {
            throw new DevelopmentException("Missing required 'layout' element");
        }
        createSubComponents(resultComponent, layoutElement);
    }

    @Override
    public void loadComponent() {
        if (resultComponent.getFrameOwner() instanceof AbstractFrame) {
            getScreenViewsLoader().deployViews(element);
        }

        Element dsContextElement = element.element("dsContext");

        DsContext dsContext = null;
        DsContext parentDsContext = context.getParent().getDsContext();
        if (parentDsContext != null) {
            DsContextLoader contextLoader = new DsContextLoader(parentDsContext.getDataSupplier());

            dsContext = contextLoader.loadDatasources(dsContextElement,
                    parentDsContext, getContext().getAliasesMap());

            ((ComponentLoaderContext) context).setDsContext(dsContext);
        }

        assignXmlDescriptor(resultComponent, element);

        Element layoutElement = element.element("layout");
        if (layoutElement == null) {
            throw new GuiDevelopmentException("Required 'layout' element is not found", context.getFullFrameId());
        }

        loadIcon(resultComponent, layoutElement);
        loadCaption(resultComponent, layoutElement);
        loadDescription(resultComponent, layoutElement);

        loadVisible(resultComponent, layoutElement);
        loadEnable(resultComponent, layoutElement);
        loadActions(resultComponent, element);

        loadSpacing(resultComponent, layoutElement);
        loadMargin(resultComponent, layoutElement);
        loadWidth(resultComponent, layoutElement);
        loadHeight(resultComponent, layoutElement);
        loadStyleName(resultComponent, layoutElement);
        loadResponsive(resultComponent, layoutElement);

        FrameContext frameContext = new FrameContextImpl(resultComponent, context.getParams()); // todo with options
        ((FrameImplementation) resultComponent).setContext(frameContext);

        if (dsContext != null) {
            LegacyFrame.of(resultComponent).setDsContext(dsContext);

            for (Datasource ds : dsContext.getAll()) {
                if (ds instanceof DatasourceImplementation) {
                    ((DatasourceImplementation) ds).initialized();
                }
            }

            dsContext.setFrameContext(frameContext);
        }

        loadSubComponentsAndExpand(resultComponent, layoutElement);

        ComponentLoaderContext parentContext = (ComponentLoaderContext) getContext().getParent();
        Map<String, Object> params = parentContext.getParams();

        parentContext.addInjectTask(new FrameInjectPostInitTask(resultComponent, params));
        parentContext.addInitTask(new FrameLoaderInitTask(resultComponent, params));
        parentContext.addPostInitTask(new FrameLoaderPostInitTask(resultComponent));
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    protected class FrameInjectPostInitTask implements InjectTask {
        protected final Fragment fragment;
        protected final Map<String, Object> params;

        public FrameInjectPostInitTask(Fragment fragment, Map<String, Object> params) {
            this.fragment = fragment;
            this.params = params;
        }

        @Override
        public void execute(Context context, Frame window) {
            String loggingId = context.getFullFrameId();
            try {
                if (fragment.getFrameOwner() instanceof AbstractFrame) {
                    Element companionsElem = element.element("companions");
                    if (companionsElem != null) {
                        StopWatch companionStopWatch = new Slf4JStopWatch(loggingId + LifeCycle.COMPANION.getSuffix(),
                                LoggerFactory.getLogger(UIPerformanceLogger.class));

                        initCompanion(companionsElem, (AbstractFrame) fragment.getFrameOwner());

                        companionStopWatch.stop();
                    }
                }

                StopWatch injectStopWatch = LifeCycle.INJECTION.createStopWatch(loggingId);

                FrameOwner controller = fragment.getFrameOwner();
                UiControllerDependencyInjector dependencyInjector =
                        beanLocator.getPrototype(UiControllerDependencyInjector.NAME, controller, NO_OPTIONS);
                dependencyInjector.inject();

                injectStopWatch.stop();
            } catch (Throwable e) {
                throw new RuntimeException("Unable to init custom frame class", e);
            }
        }
    }

    protected class FrameLoaderInitTask implements InitTask {
        protected final Fragment fragment;
        protected final Map<String, Object> params;

        public FrameLoaderInitTask(Fragment fragment, Map<String, Object> params) {
            this.fragment = fragment;
            this.params = params;
        }

        @Override
        public void execute(Context context, Frame window) {
            String loggingId = ComponentsHelper.getFullFrameId(this.fragment);

            StopWatch stopWatch = LifeCycle.INIT.createStopWatch(loggingId);

            ScreenFragment frameOwner = fragment.getFrameOwner();

            UiControllerUtils.fireEvent(frameOwner,
                    ScreenFragment.InitEvent.class,
                    new ScreenFragment.InitEvent(frameOwner, NO_OPTIONS));

            stopWatch.stop();
        }
    }

    protected class FrameLoaderPostInitTask implements PostInitTask {
        protected final Frame frame;

        public FrameLoaderPostInitTask(Frame frame) {
            this.frame = frame;
        }

        @Override
        public void execute(Context context, Frame window) {
            String loggingId = ComponentsHelper.getFullFrameId(this.frame);

            StopWatch stopWatch = LifeCycle.UI_PERMISSIONS.createStopWatch(loggingId);

            // apply ui permissions
            WindowCreationHelper.applyUiPermissions(this.frame);

            stopWatch.stop();

            FragmentLoader.this.context.executePostInitTasks();
        }
    }
}