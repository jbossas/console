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
package org.jboss.as.console.client.tools.mbui.workbench.repository;

import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.aim.Select;
import org.jboss.as.console.client.mbui.aui.aim.as7.Form;
import org.jboss.as.console.client.mbui.aui.mapping.Mapping;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceAttribute;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;

import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.Choice;
import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.OrderIndependance;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class DataSourceSample implements Sample
{
    @Override
    public String getName()
    {
        return "Datasource";
    }

    @Override
    public InteractionUnit build()
    {
        // abstract UI modelling

        String namespace = "org.jboss.ds";

        ResourceMapping global = new ResourceMapping(namespace)
                .setAddress("/profile={0}/subsystem=datasources/data-source={1}");

        // global address scopefor namespace
        Container container = new Container(namespace, "datasources", "Datasources", Choice);
        container.getEntityContext().addMapping(global);

        Container dsOverview = new Container(namespace, "datasourceOverview", "Datasources", OrderIndependance);
        Container xaOverview = new Container(namespace, "xaOverview", "XA Datasources", OrderIndependance);

        container.add(dsOverview);
        container.add(xaOverview);

        Select table = new Select(namespace, "datasourceTable", "Datasources");
        dsOverview.add(table);

        Container forms = new Container(namespace, "datasourceAttributes", "Datasource", Choice);
        dsOverview.add(forms);

        Form basicAttributes = new Form(namespace, "basicAttributes", "Attributes");
        forms.add(basicAttributes);

        Form connectionAttributes = new Form(namespace, "connectionAttributes", "Connection");
        forms.add(connectionAttributes);

        // mappings (required)
        Mapping tableMapping = new ResourceMapping(namespace)
                .addAttribute(new ResourceAttribute("${resource.name}", "Name"))
                .addAttributes("jndi-name", "enabled");

        Mapping basicAttributesMapping = new ResourceMapping(namespace)
                .addAttribute(new ResourceAttribute("${resource.name}", "Name"))
                .addAttributes("jndi-name", "enabled", "driver-name", "share-prepared-statements",
                        "prepared-statements-cache-size");

        Mapping connectionAttributesMapping = new ResourceMapping(namespace)
                .addAttributes("connection-url", "new-connection-sql", "jta", "use-ccm");

        table.getEntityContext().addMapping(tableMapping);
        basicAttributes.getEntityContext().addMapping(basicAttributesMapping);
        connectionAttributes.getEntityContext().addMapping(connectionAttributesMapping);

        return container;
    }
}
