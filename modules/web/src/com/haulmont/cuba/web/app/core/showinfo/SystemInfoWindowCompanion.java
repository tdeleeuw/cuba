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

package com.haulmont.cuba.web.app.core.showinfo;

import com.haulmont.cuba.gui.app.core.showinfo.SystemInfoWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.toolkit.ui.CubaCopyButtonExtension;
import com.haulmont.cuba.web.toolkit.ui.CubaTable;
import com.vaadin.server.Page;
import com.vaadin.server.WebBrowser;
import com.vaadin.ui.Notification;

public class SystemInfoWindowCompanion implements SystemInfoWindow.Companion {


    @Override
    public void initInfoTable(Table infoTable) {
        CubaTable webTable = (CubaTable) WebComponentsHelper.unwrap(infoTable);
        webTable.setTextSelectionEnabled(true);
    }

    @Override
    public void addCopyAction(Button copyButton, String success, String fail, String cubaCopyLogContentClass) {
        if (browserSupportCopy()) {
            com.vaadin.ui.Button button = (com.vaadin.ui.Button) WebComponentsHelper.unwrap(copyButton);
            CubaCopyButtonExtension copyExtension = CubaCopyButtonExtension.copyWith(button, cubaCopyLogContentClass.concat(" textarea"));
            copyExtension.addCopyListener(event ->
                    Notification.show(event.isSuccess() ? success : fail,
                            Notification.Type.TRAY_NOTIFICATION));
        }
    }

    protected boolean browserSupportCopy() {
        WebBrowser webBrowser = Page.getCurrent().getWebBrowser();
        return !webBrowser.isSafari() && !webBrowser.isIOS() && !webBrowser.isWindowsPhone();
    }


}