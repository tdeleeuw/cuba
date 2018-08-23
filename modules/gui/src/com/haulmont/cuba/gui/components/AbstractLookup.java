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
package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.gui.components.Window.Lookup;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Base class for lookup screen controllers.
 */
public class AbstractLookup extends AbstractWindow implements Lookup {

    private Predicate lookupValidator;
    private Consumer lookupHandler;

    public AbstractLookup() {
    }

    @Override
    public Component getLookupComponent() {
        return ((Lookup) frame).getLookupComponent();
    }

    @Override
    public void setLookupComponent(Component lookupComponent) {
        ((Lookup) frame).setLookupComponent(lookupComponent);
    }

    @Override
    public void initLookupLayout() {
        ((Lookup) frame).initLookupLayout();
    }

    @Override
    public Consumer<Collection> getSelectHandler() {
        return lookupHandler;
    }

    @Override
    public Predicate<ValidationContext> getSelectValidator() {
        return lookupValidator;
    }

    @Override
    public void setSelectValidator(Predicate lookupValidator) {
        this.lookupValidator = lookupValidator;
    }

    @Override
    public void setSelectHandler(Consumer lookupHandler) {
        this.lookupHandler = lookupHandler;
    }
}