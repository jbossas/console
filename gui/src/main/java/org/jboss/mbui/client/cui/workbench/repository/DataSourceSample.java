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
package org.jboss.mbui.client.cui.workbench.repository;

import org.jboss.mbui.client.aui.aim.Container;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.aui.aim.Select;
import org.jboss.mbui.client.aui.aim.as7.Form;
import org.jboss.mbui.client.aui.mapping.Mapping;
import org.jboss.mbui.client.aui.mapping.as7.ResourceAttribute;
import org.jboss.mbui.client.aui.mapping.as7.ResourceMapping;

import static org.jboss.mbui.client.aui.aim.TemporalOperator.Choice;
import static org.jboss.mbui.client.aui.aim.TemporalOperator.OrderIndependance;

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
        Container overview = new Container("datasourceOverview", "Datasources", OrderIndependance);

        Select table = new Select("datasourceTable", "Datasources");
        overview.add(table);

        Container forms = new Container("datasourceAttributes", "Datasource", Choice);
        overview.add(forms);

        Form basicAttributes = new Form("basicAttributes", "Attributes");
        forms.add(basicAttributes);

        Form connectionAttributes = new Form("connectionAttributes", "Connection");
        forms.add(connectionAttributes);

        // mappings (required)
        Mapping tableMapping = new ResourceMapping("datasourceTable",
                "/profile=${profile}/subsystem=datasources/data-source=*")
                .addAttribute(new ResourceAttribute("${resource.name}", "Name"))
                .addAttributes("jndi-name", "enabled");

        Mapping basicAttributesMapping = new ResourceMapping("basicAttributes",
                "/profile=${profile}/subsystem=datasources/data-source=${datasource}")
                .addAttribute(new ResourceAttribute("${resource.name}", "Name"))
                .addAttributes("jndi-name", "enabled", "driver-name", "share-prepared-statements",
                        "prepared-statements-cache-size");

        Mapping connectionAttributesMapping = new ResourceMapping("connectionAttributes",
                "/profile=${profile}/subsystem=datasources/data-source=${datasource}")
                .addAttributes("connection-url", "new-connection-sql", "jta", "use-ccm");

        table.getEntityContext().addMapping(tableMapping);
        basicAttributes.getEntityContext().addMapping(basicAttributesMapping);
        connectionAttributes.getEntityContext().addMapping(connectionAttributesMapping);

        return overview;
    }
}
