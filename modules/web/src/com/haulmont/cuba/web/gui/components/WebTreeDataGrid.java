package com.haulmont.cuba.web.gui.components;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.TreeDataGrid;
import com.haulmont.cuba.gui.components.data.DataGridSource;
import com.haulmont.cuba.gui.components.data.TreeDataGridSource;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;
import com.haulmont.cuba.web.gui.components.datagrid.DataGridDataProvider;
import com.haulmont.cuba.web.gui.components.datagrid.HierarchicalDataGridDataProvider;
import com.haulmont.cuba.web.widgets.CubaTreeGrid;

public class WebTreeDataGrid<E extends Entity> extends WebAbstractDataGrid<CubaTreeGrid<E>, E>
        implements TreeDataGrid<E> {

    @Override
    protected CubaTreeGrid<E> createComponent() {
        return new CubaTreeGrid<>();
    }

    @Override
    public void setDataGridSource(DataGridSource<E> dataGridSource) {
        if (dataGridSource != null
                && !(dataGridSource instanceof TreeDataGridSource)) {
            throw new IllegalArgumentException("TreeDataGrid supports only TreeDataGridSource data binding");
        }

        super.setDataGridSource(dataGridSource);
    }

    protected TreeDataGridSource<E> getTreeDataGridSource() {
        return (TreeDataGridSource<E>) getDataGridSource();
    }

    @Override
    protected DataGridDataProvider<E> createDataGridDataProvider(DataGridSource<E> dataGridSource) {
        return new HierarchicalDataGridDataProvider<>((TreeDataGridSource<E>) dataGridSource, this);
    }
}
