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

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;
import static org.jboss.mbui.model.structure.TemporalOperator.Concurrency;

import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Select;
import org.jboss.mbui.model.structure.Trigger;
import org.jboss.mbui.model.structure.impl.Builder;

import static org.jboss.mbui.model.structure.as7.StereoTypes.*;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class DataSourceSample implements Sample
{
    private Dialog dialog;

    public DataSourceSample() {
        this.dialog = build();
    }

    @Override
    public String getName()
    {
        return "Datasource";
    }

    @Override
    public Dialog getDialog() {
        return this.dialog;
    }

    public Dialog build()
    {
        String namespace = "org.jboss.datasource";

        // maps to a collection of datasources
        DMRMapping datasourceCollection = new DMRMapping(namespace)
                .setAddress("/{selected.profile}/subsystem=datasources/data-source=*");

        // maps to a specific datasource
        DMRMapping singleDataSource = new DMRMapping(namespace)
                .setAddress("/{selected.profile}/subsystem=datasources/data-source={selected.entity}");

        Mapping tableMapping = new DMRMapping(namespace)
                .addAttributes("entity.key","jndi-name", "enabled");

        Mapping basicAttributesMapping = new DMRMapping(namespace)
                .addAttributes("entity.key", "jndi-name", "enabled", "datasource-class", "driver-name", "share-prepared-statements",
                        "prepared-statements-cache-size");

        Mapping connectionAttributesMapping = new DMRMapping(namespace)
                .addAttributes("connection-url", "new-connection-sql", "transaction-isolation", "jta", "use-ccm");

        // UI
        InteractionUnit root = new Builder()
                .start(new Container(namespace, "datasources", "Datasources", Choice, EditorPanel))
                .mappedBy(datasourceCollection)

                    .start(new Container(namespace, "regularDS", "Regular", Concurrency))


                        // TODO: support anonymous trigger id's? This would reduce the verbosity of these declarations.
                        // Might be derived from the surrounding scope of the interaction unit. I.e. dialog ID + UUID

                        .start(new Container(namespace, "tools", "Tools", Toolstrip))
                            .mappedBy(singleDataSource)
                            .add(new Trigger(
                                    QName.valueOf("org.jboss.datasource:add"),
                                    QName.valueOf("org.jboss.as:resource-operation#add"),
                                    "Add"))
                                    .mappedBy(datasourceCollection)

                            .add(new Trigger(
                                    QName.valueOf("org.jboss.datasource:remove"),
                                    QName.valueOf("org.jboss.as:resource-operation#remove"),
                                    "Remove"))
                            .add(new Trigger(
                                    QName.valueOf("org.jboss.datasource:enable"),
                                    QName.valueOf("org.jboss.as:resource-operation#enable"),
                                    "Enable"))

                            .add(new Trigger(
                                    QName.valueOf("org.jboss.datasource:disable"),
                                    QName.valueOf("org.jboss.as:resource-operation#disable"),
                                    "Disable"))
                        .end()


                        .add(new Select(namespace, "list", "List"))
                            .mappedBy(tableMapping)

                        .start(new Container(namespace, "details", "Details", Choice))
                            .mappedBy(singleDataSource)
                                .add(new Container(namespace, "datasource#basicAttributes", "Attributes", Form))
                                    .mappedBy(basicAttributesMapping)
                                .add(new Container(namespace, "datasource#connectionAttributes", "Connection", Form))
                                    .mappedBy(connectionAttributesMapping)
                        .end()
                    .end()

                    .start(new Container(namespace, "xsDS", "XA", Concurrency))
                    .end()

                .end()

                .build();

        Dialog dialog = new Dialog(QName.valueOf("org.jboss.as7:datasource-subsystem"), root);
        return dialog;
    }
}
