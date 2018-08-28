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
 *
 */

package com.haulmont.cuba.gui;

import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.ScreenOptions;

/**
 * JavaDoc
 */
public interface Fragments {

    String NAME = "cuba_Fragments";

    default <T extends ScreenFragment> T create(Class<T> fragmentClass) {
        return create(fragmentClass, FrameOwner.NO_OPTIONS);
    }

    default ScreenFragment create(WindowInfo windowInfo) {
        return create(windowInfo, FrameOwner.NO_OPTIONS);
    }

    <T extends ScreenFragment> T create(Class<T> fragmentClass, ScreenOptions options);

    ScreenFragment create(WindowInfo windowInfo, ScreenOptions options);
}