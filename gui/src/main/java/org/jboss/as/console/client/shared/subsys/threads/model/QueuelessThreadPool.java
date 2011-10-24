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

import java.util.List;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
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
   
   @Binding(detypedName="blocking")
   @FormItem(defaultValue="true",
            label="Blocking",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
   boolean isBlocking();
   void setBlocking(boolean blocking);
   
   @Binding(detypedName="keepalive-time/time")
   @FormItem(defaultValue="60",
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
   
   // ---- SIZING TAB --------------------
   @Override
   @Binding(detypedName="max-threads/count")
   @FormItem(defaultValue="2",
            required=true,
            label="Max Threads Count",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX",
            tabName="subsys_threads_sizing")
   Integer getMaxThreadsCount();
   void setMaxThreadsCount(Integer maxThreadsCount);
    
   @Override
   @Binding(detypedName="max-threads/per-cpu")
   @FormItem(defaultValue="1",
            required=true,
            label="Max Threads Per CPU",
            formItemTypeForAdd="NUMBER_BOX",
            formItemTypeForEdit="NUMBER_BOX",
            tabName="subsys_threads_sizing")
   Integer getMaxThreadsPerCPU();
   void setMaxThreadsPerCPU(Integer maxThreadsPerCPU);
   
   // ------ PROPERTIES TAB --------------
   @Binding(detypedName="properties", 
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="common_label_properties")
   List<PropertyRecord> getProperties();
   void setProperties(List<PropertyRecord> properties);
}
