package com.haulmont.cuba.web.gui.components;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.TreeDataGrid;
import com.haulmont.cuba.web.widgets.CubaTreeGrid;

public class WebTreeDataGrid<E extends Entity> extends WebAbstractDataGrid<CubaTreeGrid<E>, E>
        implements TreeDataGrid<E> {

    @Override
    protected CubaTreeGrid<E> createComponent() {
        return new CubaTreeGrid<>();
    }
}
