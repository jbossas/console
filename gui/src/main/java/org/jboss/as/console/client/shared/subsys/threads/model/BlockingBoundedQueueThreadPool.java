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
package org.jboss.as.console.client.shared.subsys.threads.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for a Blocking Bounded Queue Thread Pool
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=threads/blocking-bounded-queue-thread-pool={0}")
public interface BlockingBoundedQueueThreadPool extends ThreadPool {

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

    @Binding(detypedName="core-threads")
    @FormItem(defaultValue="2",
             required=true,
             label="Core Threads",
             formItemTypeForAdd="NUMBER_BOX",
             formItemTypeForEdit="NUMBER_BOX")
    Integer getCoreThreads();
    void setCoreThreads(Integer coreThreads);

    @Binding(detypedName="keepalive-time/time")
    @FormItem(defaultValue="60",
             required=true,
             label="Keepalive Time",
             formItemTypeForAdd="NUMBER_BOX",
             formItemTypeForEdit="NUMBER_BOX")
     Long getKeepaliveTime();
     void setKeepaliveTime(Long timeout);

    @Binding(detypedName="keepalive-time/unit")
    @FormItem(defaultValue="SECONDS",
             label="Keepalive Time Unit",
             required=true,
             formItemTypeForEdit="TIME_UNITS",
             formItemTypeForAdd="TIME_UNITS")
    String getKeepaliveTimeUnit();
    void setKeepaliveTimeUnit(String unit);

    @Binding(detypedName="thread-factory")
    @FormItem(defaultValue="",
             label="Thread Factory",
             required=false,
             formItemTypeForEdit="COMBO_BOX",
             formItemTypeForAdd="COMBO_BOX")
    String getThreadFactory();
    void setThreadFactory(String threadFactory);

    @Binding(detypedName="queue-length")
    @FormItem(defaultValue="2",
             required=true,
             label="Queue Length",
             formItemTypeForAdd="NUMBER_BOX",
             formItemTypeForEdit="NUMBER_BOX")
    Integer getQueueLength();
    void setQueueLength(Integer queueLength);

    @Binding(detypedName="max-threads")
    @FormItem(defaultValue="2",
             required=true,
             label="Max Threads",
             formItemTypeForAdd="NUMBER_BOX",
             formItemTypeForEdit="NUMBER_BOX")
    @Override
    Integer getMaxThreads();
    void setMaxThreads(Integer maxThreads);

   @Binding(detypedName="allow-core-timeout")
   @FormItem(defaultValue="true",
            label="Allow Core Timeout",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
   boolean isAllowCoreTimeout();
   void setAllowCoreTimeout(boolean allowCoreTimeout);

}
