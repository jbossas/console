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

import org.jboss.mbui.model.structure.Dialog;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.Builder;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.as7.Form;
import org.jboss.mbui.model.structure.as7.ImplicitBehaviour;
import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;

import static org.jboss.mbui.model.structure.TemporalOperator.*;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class TransactionSample implements Sample
{

    private Dialog dialog;

    public TransactionSample() {
        this.dialog = build();
    }

    @Override
    public String getName()
    {
        return "Transaction";
    }

    @Override
    public Dialog getDialog() {
        return dialog;
    }

    public Dialog build()
    {
        String ns = "org.jboss.transactions";

        // entities
        Mapping global = new ResourceMapping(ns)
                .setAddress("/{selected.profile}/subsystem=transactions");

        Mapping basicAttributesMapping = new ResourceMapping(ns)
                .addAttributes(
                        "enable-statistics", "enable-tsm-status", "jts", "default-timeout",
                        "node-identifier", "use-hornetq-store");

        Mapping processMapping = new ResourceMapping(ns)
                .addAttributes("process-id-uuid", "process-id-socket-binding");

        Mapping recoveryMapping = new ResourceMapping(ns)
                .addAttributes("recovery-listener", "socket-binding");

        Container overview = new Container(ns, "transactionManager", "TransactionManager", Concurrency);
        Form basicAttributes = new Form(ns, "transactionManager#basicAttributes", "Attributes");
        Container details = new Container(ns, "configGroups", "Details", Choice);
        Form processAttributes = new Form(ns, "transactionManager#processAttributes", "Process ID");
        Form recoveryAttributes = new Form(ns, "transactionManager#recoveryAttributes", "Recovery");

        // structure & mapping
        InteractionUnit root = new Builder()
                .start(overview)
                .addMapping(global)
                    .add(basicAttributes).addMapping(basicAttributesMapping)
                    .start(details)
                        .add(processAttributes).addMapping(processMapping)
                        .add(recoveryAttributes).addMapping(recoveryMapping)
                    .end()
                .end()
                .build();

        // attach the implicit behaviour
        ImplicitBehaviour.attach(basicAttributes);

        Dialog dialog = new Dialog(QName.valueOf("org.jboss.as7:transaction-subsystem"), root);
        return dialog;
    }
}

