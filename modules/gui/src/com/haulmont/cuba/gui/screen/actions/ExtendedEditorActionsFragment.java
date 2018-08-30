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

package com.haulmont.cuba.gui.screen.actions;

import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.Configuration;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.screen.ScreenControllerUtils;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import static com.haulmont.cuba.gui.screen.EditorScreen.*;

@UiDescriptor("extended-editor-actions.xml")
@UiController("extendedEditorActions")
public class ExtendedEditorActionsFragment extends ScreenFragment {

    protected boolean commitActionPerformed = false;

    public ExtendedEditorActionsFragment() {
        addInitListener(this::init);
    }

    protected void init(@SuppressWarnings("unused") InitEvent initEvent) {
        Configuration configuration = getBeanLocator().get(Configuration.NAME);

        Messages messages = getBeanLocator().get(Messages.NAME);

        Window window = getParentScreen().getWindow();

        String commitShortcut = configuration.getConfig(ClientConfig.class).getCommitShortcut();
        window.addAction(
                new BaseAction(WINDOW_COMMIT_AND_CLOSE)
                        .withCaption(messages.getMainMessage("actions.OkClose"))
                        .withPrimary(true)
                        .withShortcut(commitShortcut)
                        .withHandler(e -> getParentScreen().closeWithCommit()));

        Action commitAction = new BaseAction(WINDOW_COMMIT)
                .withCaption(messages.getMainMessage("actions.Save"))
                .withHandler(e ->
                        ScreenControllerUtils.commitChanges(getParentScreen())
                                .then(() -> commitActionPerformed = true)
                );

        window.addAction(commitAction);

        Action closeAction = new BaseAction(WINDOW_CLOSE)
                .withCaption(messages.getMainMessage("actions.Cancel"))
                .withHandler(e ->
                        getParentScreen().close(commitActionPerformed ?
                                WINDOW_COMMIT_AND_CLOSE_ACTION : WINDOW_DISCARD_AND_CLOSE_ACTION)
                );

        window.addAction(closeAction);
    }
}