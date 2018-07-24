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

package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Frame;

public class ActionOwnerPostInitTask extends AbstractPostInitTask {
    public ActionOwnerPostInitTask(Component component, String actionName, Frame frame) {
        super(component, actionName, frame);
    }

    @Override
    protected Action checkHavingOwnAction(String id) {
        return null;
    }

    @Override
    protected void addAction(Component component, Action action) {
        Component.ActionOwner actionOwner = (Component.ActionOwner) component;
        actionOwner.setAction(action);
    }
}
