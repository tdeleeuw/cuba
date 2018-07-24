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

import java.util.ArrayList;
import java.util.List;

public class ActionsHolderPostInitTask extends AbstractPostInitTask {
    public ActionsHolderPostInitTask(Component.ActionsHolder component, String actionName, Frame frame) {
        super(component, actionName, frame);
    }

    @Override
    protected Action checkHavingOwnAction(String id) {
        Component.ActionsHolder actionsHolder = (Component.ActionsHolder) component;
        return actionsHolder.getAction(id);
    }

    @Override
    protected void addAction(Component component, Action action) {
        Component.ActionsHolder actionsHolder = (Component.ActionsHolder) component;
        List<Action> declarativeActions = new ArrayList<>(actionsHolder.getActions());
        for (Action declarativeAction : declarativeActions) {
            if (declarativeAction.getId().equals(actionName)) {
                int index = declarativeActions.indexOf(declarativeAction);
                actionsHolder.removeAction(declarativeAction);
                actionsHolder.addAction(action, index);
                break;
            }
        }
    }
}
