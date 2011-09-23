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

import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 5/16/11
 */
public interface JDBCDriver {

    @Binding(detypedName = "driver-name")
    String getName();
    void setName(String name);

    @Binding(detypedName = "deployment-name")
    String getDeploymentName();
    void setDeploymentName(String name);

    @Binding(detypedName = "major-version")
    int getMajorVersion();
    void setMajorVersion(int major);

    @Binding(detypedName = "minor-version")
    int getMinorVersion();
    void setMinorVersion(int minor);

    @Binding(detypedName = "driver-class")
    String getDriverClass();
    void setDriverClass(String driverClass);

    @Binding(detypedName = "driver-xa-datasource-class-name")
    String getXaDataSourceClass();
    void setXaDataSourceClass(String dataSourceClass);

    @Binding(detypedName = "none", skip = true)
    String getGroup();
    void setGroup(String group);
}
