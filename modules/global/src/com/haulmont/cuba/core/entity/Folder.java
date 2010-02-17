/*
 * Copyright (c) 2009 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Konstantin Krivopustov
 * Created: 10.12.2009 15:10:55
 *
 * $Id$
 */
package com.haulmont.cuba.core.entity;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity(name = "core$Folder")
@Table(name = "SYS_FOLDER")
@Inheritance(strategy= InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("F")
public class Folder extends StandardEntity {

    private static final long serialVersionUID = -2038652558181851215L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Folder parent;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
