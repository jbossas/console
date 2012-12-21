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

import org.jboss.as.console.client.mbui.aui.aim.Behaviour;
import org.jboss.as.console.client.mbui.aui.aim.Builder;
import org.jboss.as.console.client.mbui.aui.aim.Container;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.aim.Trigger;
import org.jboss.as.console.client.mbui.aui.aim.TriggerType;
import org.jboss.as.console.client.mbui.aui.aim.as7.Form;
import org.jboss.as.console.client.mbui.aui.aim.as7.ImplicitBehaviour;
import org.jboss.as.console.client.mbui.aui.mapping.Mapping;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;

import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.Choice;
import static org.jboss.as.console.client.mbui.aui.aim.TemporalOperator.OrderIndependance;
import static org.jboss.as.console.client.mbui.aui.aim.TriggerType.*;
import static org.jboss.as.console.client.mbui.aui.aim.TriggerType.System;

/**
 * @author Harald Pehl
 * @date 10/25/2012
 */
public class TransactionSample implements Sample
{
    @Override
    public String getName()
    {
        return "Transaction";
    }

    @Override
    public InteractionUnit build()
    {
        String ns = "org.jboss.transactions";

        // entities
        Mapping global = new ResourceMapping(ns)
                .setAddress("/profile={0}/subsystem=transactions");

        Mapping basicAttributesMapping = new ResourceMapping(ns)
                .addAttributes(
                        "enable-statistics", "enable-tsm-status", "jts", "default-timeout",
                        "node-identifier", "use-hornetq-store");

        Mapping processMapping = new ResourceMapping(ns)
                .addAttributes("process-id-uuid", "process-id-socket-binding");

        Mapping recoveryMapping = new ResourceMapping(ns)
                .addAttributes("recovery-listener", "socket-binding");

        Container overview = new Container(ns, "transactionManager", "TransactionManager", OrderIndependance);
        Form basicAttributes = new Form(ns, "root#basicAttributes", "Attributes");
        Container details = new Container(ns, "configGroups", "Details", Choice);
        Form processAttributes = new Form(ns, "root#processAttributes", "Process ID");
        Form recoveryAttributes = new Form(ns, "root#recoveryAttributes", "Recovery");

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

        return root;
    }
}

