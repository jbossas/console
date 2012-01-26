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
package org.jboss.as.console.client.shared.subsys.ejb3.model;

import org.jboss.as.console.client.shared.subsys.threads.model.UnboundedQueueThreadPool;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for an Unbounded Queue Thread Pool used by EJB3
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=ejb3/thread-pool={0}")
public interface EJB3ThreadPool extends UnboundedQueueThreadPool {

   @Override
   @Binding(detypedName="name", key=true)
   @FormItem(defaultValue="",
             localLabel="common_label_name",
             required=true,
             formItemTypeForEdit="TEXT",
             formItemTypeForAdd="TEXT_BOX",
             order=0)
   public String getName();
   @Override
   public void setName(String name);

   @Binding(detypedName="thread-factory")
   @FormItem(defaultValue="",
            label="Thread Factory",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX")
   @Override
   String getThreadFactory();
   @Override
   void setThreadFactory(String threadFactory);

   @Binding(detypedName="keepalive-time/time")
   @FormItem(defaultValue="60",
            required=true,
            label="Keepalive Timeout",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX")
   @Override
   Integer getKeepaliveTimeout();
   @Override
   void setKeepaliveTimeout(Integer timeout);

   @Binding(detypedName="keepalive-time/unit")
   @FormItem(defaultValue="SECONDS",
            label="Keepalive Timeout Unit",
            required=true,
            formItemTypeForEdit="TIME_UNITS",
            formItemTypeForAdd="TIME_UNITS")
   @Override
   String getKeepaliveTimeoutUnit();
   @Override
   void setKeepaliveTimeoutUnit(String unit);

   @Binding(detypedName="max-threads")
   @FormItem(defaultValue="2",
            required=true,
            label="Max Threads",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX")
   @Override
   Integer getMaxThreads();
   @Override
   void setMaxThreads(Integer maxThreadsCount);

}
