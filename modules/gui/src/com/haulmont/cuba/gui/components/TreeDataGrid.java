package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.data.DataGridSource;
import com.haulmont.cuba.gui.components.data.datagrid.HierarchicalDatasourceDataGridAdapter;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.HierarchicalDatasource;

public interface TreeDataGrid<E extends Entity> extends DataGrid<E> {

    String NAME = "treeDataGrid";

    @Override
    default HierarchicalDatasource getDatasource() {
        DataGridSource<E> dataGridSource = getDataGridSource();
        return dataGridSource != null ?
                (HierarchicalDatasource) ((HierarchicalDatasourceDataGridAdapter) dataGridSource).getDatasource()
                : null;
    }

    @Deprecated
    default void setDatasource(HierarchicalDatasource datasource) {
        setDatasource(datasource);
    }

    @SuppressWarnings("unchecked")
    @Override
    default void setDatasource(CollectionDatasource datasource) {
        if (datasource == null) {
            setDataGridSource(null);
        } else {
            if (!(datasource instanceof HierarchicalDatasource)) {
                throw new IllegalArgumentException("TreeDataGrid supports only HierarchicalDatasource");
            }

            setDataGridSource(new HierarchicalDatasourceDataGridAdapter((HierarchicalDatasource) datasource));
        }
    }
}
