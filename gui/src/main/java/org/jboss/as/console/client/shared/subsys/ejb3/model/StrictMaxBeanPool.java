/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.ejb3.model;

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=ejb3/strict-max-bean-instance-pool={0}")
public interface StrictMaxBeanPool extends NamedEntity {
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX")
    String getName();
    @Override
    void setName(String name);

    @Binding(detypedName="max-pool-size")
    @FormItem(defaultValue="20",
              label="Max Pool Size",
              formItemTypeForEdit="NUMBER_BOX",
              formItemTypeForAdd="NUMBER_BOX")
    int getMaxPoolSize();
    void setMaxPoolSize(int maxSize);

    @FormItem(defaultValue="5",
            label="Timeout",
            formItemTypeForAdd="NUMBER_UNIT_BOX",
            formItemTypeForEdit="NUMBER_UNIT_BOX")
    long getTimeout();
    void setTimeout(long timeout);

    @Binding(detypedName="timeout-unit")
    @FormItem(formItemTypeForEdit="UNITS")
    String getTimeoutUnit();
    void setTimeoutUnit(String unit);
}
