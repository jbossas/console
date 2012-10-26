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
package org.jboss.mbui.client.cui.samples;

import org.jboss.mbui.client.aui.aim.DataInputOutput;
import org.jboss.mbui.client.aui.aim.DataSelection;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.aui.mapping.Mapping;
import org.jboss.mbui.client.aui.mapping.ResourceMapping;


import static org.jboss.mbui.client.aui.aim.InteractionRole.*;

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
        // Abstract UI Modelling
        InteractionUnit overview = new InteractionUnit("datasourceOverview");
        overview.setRole(Overview);

        DataSelection table = new DataSelection("table", "Datasources");
        table.setRole(Single_Select);
        overview.addComponent(table);


        InteractionUnit forms = new InteractionUnit("datasourceAttributes", "Datasource");
        forms.setRole(Overview);
        overview.addComponent(forms);


        DataInputOutput basicAttributes = new DataInputOutput("basicAttributes", "Attributes");
        basicAttributes.setRole(Edit);
        forms.addComponent(basicAttributes);

        DataInputOutput connectionAttributes = new DataInputOutput("connectionAttributes", "Connection");
        connectionAttributes.setRole(Edit);
        forms.addComponent(connectionAttributes);


        // Reification Steps (required)
        Mapping tableMapping = new ResourceMapping("datasourceTable",
                "/profile=${profile}/subsystem=datasources/data-source=*")
                .addAttributes("${resource.name}", "jndi-name", "enabled");

        Mapping editMapping = new ResourceMapping("datasourceForm",
                "/profile=${profile}/subsystem=datasources/data-source=${datasource}")
                .addAttributes("${resource.name}", "jndi-name", "enabled", "driver-name",
                        "share-prepared-statements", "prepared-statements-cache-size");

        table.getEntityContext().addMapping(tableMapping);
        basicAttributes.getEntityContext().addMapping(editMapping);

        return overview;
    }
}
