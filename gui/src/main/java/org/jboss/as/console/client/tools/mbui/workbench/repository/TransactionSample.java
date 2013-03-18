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

import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.Mapping;
import org.jboss.mbui.model.mapping.as7.DMRMapping;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.impl.Builder;

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;

import static org.jboss.mbui.model.structure.as7.StereoTypes.*;

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
        Mapping global = new DMRMapping()
                .setAddress("/{selected.profile}/subsystem=transactions");

        Mapping basicAttributesMapping = new DMRMapping()
                .addAttributes(
                        "enable-statistics", "enable-tsm-status", "jts", "default-timeout",
                        "node-identifier", "use-hornetq-store");

        Mapping processMapping = new DMRMapping()
                .addAttributes("process-id-uuid", "process-id-socket-binding");

        Mapping recoveryMapping = new DMRMapping()
                .addAttributes("recovery-listener", "socket-binding");

        Container overview = new Container(ns, "transactionManager", "TransactionManager");

        Container basicAttributes = new Container(ns, "transactionManager#basicAttributes", "Attributes",Form);

        Container details = new Container(ns, "configGroups", "Details", Choice);

        Container processAttributes = new Container(ns, "transactionManager#processAttributes", "Process ID",Form);

        Container recoveryAttributes = new Container(ns, "transactionManager#recoveryAttributes", "Recovery",Form);

        // structure & mapping
        InteractionUnit root = new Builder()
                .start(overview)
                .mappedBy(global)
                    .add(basicAttributes).mappedBy(basicAttributesMapping)
                    .start(details)
                        .add(processAttributes).mappedBy(processMapping)
                        .add(recoveryAttributes).mappedBy(recoveryMapping)
                    .end()
                .end()
                .build();

        Dialog dialog = new Dialog(QName.valueOf("org.jboss.as7:transaction-subsystem"), root);
        return dialog;
    }



}

