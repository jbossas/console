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
 * Model for a Queueless Thread Pool
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=threads/queueless-thread-pool={0}")
public interface QueuelessThreadPool extends ThreadPool {

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
   String getThreadFactory();
   void setThreadFactory(String threadFactory);

   @Binding(detypedName="handoff-executor")
   @FormItem(defaultValue="",
            label="Handoff Executor",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
   String getHandoffExecutor();
   void setHandoffExecutor(String handoffExecutor);

   @Override
   @Binding(detypedName="max-threads")
   @FormItem(defaultValue="2",
            required=true,
            label="Max Threads",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX")
   Integer getMaxThreads();
   void setMaxThreads(Integer maxThreadsCount);

   // read-only metric
    @Binding(detypedName="current-thread-count")
    @FormItem(defaultValue="",
              label="Current Thread Count",
              required=false,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT")
    public String getCurrentThreadCount();
    public void setCurrentThreadCount(String currentThreadCount);

    // read-only metric
    @Binding(detypedName="rejected-count")
    @FormItem(defaultValue="",
              label="Rejected Tasks Count",
              required=false,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT")
    public String getRejectedCount();
    public void setRejectedCount(String rejectedCount);

    // read-only metric
    @Binding(detypedName="largest-thread-count")
    @FormItem(defaultValue="",
              label="Largest Thread Count",
              required=false,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT")
    public String getLargestThreadCount();
    public void setLargestThreadCount(String largestThreadCount);

   @Binding(detypedName="keepalive-time/time")
   @FormItem(defaultValue="60",
            required=false,
            label="Keepalive Timeout",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX")
    Integer getKeepaliveTimeout();
    void setKeepaliveTimeout(Integer timeout);

   @Binding(detypedName="keepalive-time/unit")
   @FormItem(defaultValue="SECONDS",
            label="Keepalive Timeout Unit",
            required=false,
            formItemTypeForEdit="TIME_UNITS",
            formItemTypeForAdd="TIME_UNITS")
   String getKeepaliveTimeoutUnit();
   void setKeepaliveTimeoutUnit(String unit);

}
