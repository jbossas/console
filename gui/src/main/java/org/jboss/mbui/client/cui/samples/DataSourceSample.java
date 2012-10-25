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

import org.jboss.mbui.client.aui.aim.Compound;
import org.jboss.mbui.client.aui.aim.DataInputOutput;
import org.jboss.mbui.client.aui.aim.Header;
import org.jboss.mbui.client.aui.aim.InteractionUnit;
import org.jboss.mbui.client.aui.mapping.EntityContext;
import org.jboss.mbui.client.aui.mapping.ResourceMapping;

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
        Compound compound = new Compound("datasourceCompound");

        // header
        compound.add(new Header("header", "JDBC Datasources", "JDBC datasource configurations"));

        // form
        EntityContext datasourceContext = new EntityContext("datasource");
        datasourceContext.addMapping(
                new ResourceMapping("datasource", "/profile=${profile}/subsystem=datasources/data-source=${datasource}")
                        .addAttributes("${resource.name}", "jndi-name", "enabled", "driver-name",
                                "share-prepared-statements", "prepared-statements-cache-size"));
        DataInputOutput form = new DataInputOutput("datasource", "Datasource");
        form.setEntityContext(datasourceContext);
        compound.add(form);

        return compound;
    }
}
