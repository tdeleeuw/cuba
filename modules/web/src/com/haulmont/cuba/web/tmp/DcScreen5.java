/*
 * Copyright (c) 2008-2018 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.web.tmp;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.DialogAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataLoader;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@UiController("dcScreen5")
@UiDescriptor("dc-screen-5.xml")
public class DcScreen5 extends Screen {

    @Inject
    protected Metadata metadata;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Screens screens;

    @Inject
    protected Editors editors;

    @Inject
    protected Dialogs dialogs;

    @Inject
    private CollectionContainer<User> usersCont;

    @Inject
    private DataLoader usersLoader;

    @Subscribe
    protected void beforeShow(BeforeShowEvent event) {
        getScreenData().loadAll();
    }

    @Subscribe("createBtn")
    private void onCreateClick(Button.ClickEvent event) {
        editors.createEntity(usersCont, DcScreen6.class, this::initNewUser, null);
    }

    protected void initNewUser(User user) {
        Group group = dataManager.load(Group.class).id(UUID.fromString("0fa2b1a5-1d68-4d69-9fbd-dff348347f93")).one();
        user.setGroup(group);
    }

    @Subscribe("editBtn")
    private void onEditClick(Button.ClickEvent event) {
        editors.editEntity(usersCont, DcScreen6.class, null);
    }

    @Subscribe("removeBtn")
    private void onRemoveClick(Button.ClickEvent event) {
        User selectedUser = usersCont.getItemOrNull();
        if (selectedUser != null) {
            dialogs.createOptionDialog()
                    .setCaption("Confirmation")
                    .setMessage("Are you sure you want to delete the selected row?")
                    .setActions(
                            new DialogAction(DialogAction.Type.YES).withHandler(actionPerformedEvent -> {
                                usersCont.getMutableItems().remove(selectedUser);
                                getScreenData().getDataContext().remove(selectedUser);
                                getScreenData().getDataContext().commit();
                            }),
                            new DialogAction(DialogAction.Type.NO)
                    )
                    .show();
        }
    }

    @Provide(to = "usersLoader", subject = "loadDelegate", target = Target.DATA_LOADER)
    private List<User> loadUsers(LoadContext<User> loadContext) {
        List<User> users = dataManager.loadList(loadContext);
        System.out.println("Loaded users: " + users.size());
        return users;
    }
}
