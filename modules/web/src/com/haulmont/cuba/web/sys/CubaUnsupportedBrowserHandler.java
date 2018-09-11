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

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import com.vaadin.server.*;

import java.io.IOException;
import java.io.Writer;

public class CubaUnsupportedBrowserHandler extends UnsupportedBrowserHandler {

    public static final String UNSUPPORTED_PAGE_PATH = "/com/haulmont/cuba/web/sys/unsupported-page.html";

    protected Resources resources;

    public CubaUnsupportedBrowserHandler() {
        super();

        resources = AppBeans.get(Resources.NAME);
    }

    @Override
    protected void writeBrowserTooOldPage(VaadinRequest request, VaadinResponse response) throws IOException {
        try (Writer page = response.getWriter()) {

            String pageContent = resources.getResourceAsString(UNSUPPORTED_PAGE_PATH);
            if (pageContent != null) {
                page.write(pageContent);
            }
        }
    }
}
