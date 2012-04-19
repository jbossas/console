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

package org.jboss.as.console.client.shared.subsys.jca.model;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
@Address("/subsystem=datasources/xa-data-source={0}")
public interface XADataSource extends DataSource {

    @Binding(detypedName = "xa-datasource-class")
    String getDataSourceClass();
    void setDataSourceClass(String dadaSourceClass);

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> props);

    @Binding(skip=true) // does not exist on XA datasources
    boolean isJta();
    void setJta(boolean b);

    @Binding(detypedName = "pad-xid")
    boolean isPadXid();
    void setPadXid(boolean b);

    @Binding(detypedName = "wrap-xa-resource")
    boolean isWrapXaResource();
    void setWrapXaResource(boolean b);

    @Binding(detypedName = "same-rm-override")
    boolean isEnableRMOverride();
    void setEnableRMOverride(boolean b);

    @Binding(detypedName = "interleaving")
    boolean isEnableInterleave();
    void setEnableInterleave(boolean b);

}
