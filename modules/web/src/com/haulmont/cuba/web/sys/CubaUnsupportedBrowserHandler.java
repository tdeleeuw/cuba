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

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Resources;
import com.haulmont.cuba.core.global.TemplateHelper;
import com.vaadin.server.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class CubaUnsupportedBrowserHandler extends UnsupportedBrowserHandler {

    public static final String UNSUPPORTED_PAGE_TEMPLATE_PATH = "/com/haulmont/cuba/web/sys/unsupported-page-template.html";

    protected Resources resources;
    protected Messages messages;

    public CubaUnsupportedBrowserHandler() {
        super();

        resources = AppBeans.get(Resources.NAME);
        messages = AppBeans.get(Messages.NAME);
    }

    @Override
    protected void writeBrowserTooOldPage(VaadinRequest request, VaadinResponse response) throws IOException {
        try (Writer page = response.getWriter()) {

            Locale locale = request.getLocale();

            ParamsMap paramsMap = ParamsMap.of();
            paramsMap.pair("captionMessage", messages.getMainMessage("unsupportedPage.captionMessage", locale));
            paramsMap.pair("descriptionMessage", messages.getMainMessage("unsupportedPage.descriptionMessage", locale));
            paramsMap.pair("browserListCaption", messages.getMainMessage("unsupportedPage.browserListCaption", locale));

            paramsMap.pair("chromeImagePath", messages.getMainMessage("unsupportedPage.chromeImagePath"));
            paramsMap.pair("firefoxImagePath", messages.getMainMessage("unsupportedPage.firefoxImagePath"));
            paramsMap.pair("safariImagePath", messages.getMainMessage("unsupportedPage.safariImagePath"));
            paramsMap.pair("operaImagePath", messages.getMainMessage("unsupportedPage.operaImagePath"));
            paramsMap.pair("edgeImagePath", messages.getMainMessage("unsupportedPage.edgeImagePath"));
            paramsMap.pair("explorerImagePath", messages.getMainMessage("unsupportedPage.explorerImagePath"));

            paramsMap.pair("chromeMessage", messages.getMainMessage("unsupportedPage.chromeMessage", locale));
            paramsMap.pair("firefoxMessage", messages.getMainMessage("unsupportedPage.firefoxMessage", locale));
            paramsMap.pair("safariMessage", messages.getMainMessage("unsupportedPage.safariMessage", locale));
            paramsMap.pair("operaMessage", messages.getMainMessage("unsupportedPage.operaMessage", locale));
            paramsMap.pair("edgeMessage", messages.getMainMessage("unsupportedPage.edgeMessage", locale));
            paramsMap.pair("explorerMessage", messages.getMainMessage("unsupportedPage.explorerMessage", locale));

            String stringTemplate = resources.getResourceAsString(UNSUPPORTED_PAGE_TEMPLATE_PATH);

            String pageContent = TemplateHelper.processTemplate(stringTemplate, paramsMap.create());

            page.write(pageContent);
        }
    }
}
