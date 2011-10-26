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

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
@Address("/subsystem=ejb3")
public interface EJB3Subsystem {
    @Binding(detypedName="default-slsb-instance-pool")
    @FormItem(localLabel="subsys_ejb3_statelessSessionBeanPool",
              required=true,
              formItemTypeForEdit="COMBO_BOX")
    String getDefaultSLSBPool();
    void setDefaultSLSBPool(String name);

    @Binding(detypedName="default-mdb-instance-pool")
    @FormItem(localLabel="subsys_ejb3_messageDrivenBeanPool",
              required=true,
              formItemTypeForEdit="COMBO_BOX")
    String getDefaultMDBPool();
    void setDefaultMDBPool(String name);

    @Binding(detypedName="default-resource-adapter-name")
    @FormItem(localLabel="subsys_ejb3_defaultResourceAdapter",
              required=true)
    String getDefaultRA();
    void setDefaultRA(String name);

    @Binding(detypedName="default-singleton-bean-access-timeout")
    @FormItem(localLabel="subsys_ejb3_singletonAccessTimeout",
              required=true,
              formItemTypeForEdit="NUMBER_BOX")
    long getDefaultSingletonAccessTimeout();
    void setDefaultSingletonAccessTimeout(long timeout);

    @Binding(detypedName="default-stateful-bean-access-timeout")
    @FormItem(localLabel="subsys_ejb3_statefulAccessTimeout",
              required=true,
              formItemTypeForEdit="NUMBER_BOX")
    long getDefaultStatefulAccessTimeout();
    void setDefaultStatefulAccessTimeout(long timeout);
}
