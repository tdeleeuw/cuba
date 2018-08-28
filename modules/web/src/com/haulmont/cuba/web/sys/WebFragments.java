/*
 * Copyright (c) 2008-2018 Haulmont.
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
 */

package com.haulmont.cuba.web.sys;

import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.ScreenOptions;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.sys.ScreenDescriptorUtils;
import com.haulmont.cuba.gui.xml.layout.ScreenXmlLoader;
import com.haulmont.cuba.web.AppUI;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.haulmont.bali.util.Preconditions.checkNotNullArgument;

@Scope(UIScope.NAME)
@Component(Fragments.NAME)
public class WebFragments implements Fragments {

    @Inject
    protected ScreenXmlLoader screenXmlLoader;
    @Inject
    protected WindowConfig windowConfig;

    protected AppUI ui;

    public WebFragments(AppUI ui) {
        this.ui = ui;
    }

    @Override
    public <T extends ScreenFragment> T create(Class<T> requiredFragmentClass, ScreenOptions options) {
        checkNotNullArgument(requiredFragmentClass);
        checkNotNullArgument(options);

        WindowInfo windowInfo = getFragmentInfo(requiredFragmentClass);

        return createFragment(windowInfo, options);
    }

    protected <T extends ScreenFragment> WindowInfo getFragmentInfo(Class<T> fragmentClass) {
        UiController uiController = fragmentClass.getAnnotation(UiController.class);
        if (uiController == null) {
            throw new IllegalArgumentException("No @UiController annotation for class " + fragmentClass);
        }

        String screenId = ScreenDescriptorUtils.getInferredScreenId(uiController, fragmentClass);

        return windowConfig.getWindowInfo(screenId);
    }

    @Override
    public ScreenFragment create(WindowInfo windowInfo, ScreenOptions options) {
        checkNotNullArgument(windowInfo);
        checkNotNullArgument(options);

        return createFragment(windowInfo, options);
    }

    protected <T extends ScreenFragment> T createFragment(WindowInfo windowInfo, ScreenOptions options) {

        return null;
    }
}