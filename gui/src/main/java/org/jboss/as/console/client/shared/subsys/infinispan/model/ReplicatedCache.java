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
package org.jboss.as.console.client.shared.subsys.infinispan.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=infinispan/cache-container={0}/replicated-cache={1}/")
public interface ReplicatedCache extends InvalidationCache {
    
    @Override
    @Binding(detypedName="replicated-cache")
    @FormItem(defaultValue="",
              label="Name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX") 
    public String getName();
    @Override
    public void setName(String name);
    
    @Override
    @Binding(detypedName="cache-container")
    @FormItem(defaultValue="",
              label="Cache Container",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="COMBO_BOX") 
    public String getCacheContainer();
    @Override
    public void setCacheContainer(String cacheContainerName);
    
    // This one isn't actually a cache attribute.
    // It is set by the console to display if it is the default cache
    // for its cache container.
    @Override
    @Binding(detypedName="default-for-cache-container")
    @FormItem(defaultValue="false",
            label="Default for cache container?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public Boolean isDefault();
    @Override
    public void setDefault(Boolean isDefault);
    
    @Override
    @Binding(detypedName="controller-mode")
    @FormItem(defaultValue="LAZY",
            label="Controller Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"EAGER", "LAZY"}) 
    public String getControllerMode();
    @Override
    public void setControllerMode(String controllerMode);
    
    @Override
    @Binding(detypedName="batching")
    @FormItem(defaultValue="false",
            label="Batching",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public Boolean isBatching();
    @Override
    public void setBatching(Boolean isBatching);
    
    @Override
    @Binding(detypedName="indexing")
    @FormItem(defaultValue="NONE",
            label="Indexing",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "LOCAL", "ALL"})
    public String getIndexing();
    @Override
    public void setIndexing(String indexing);
    
    // Locking attributes
    @Override
    @Binding(detypedName="locking/isolation")
    @FormItem(defaultValue="REPEATABLE_READ",
            label="Isolation",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "READ_UNCOMMITTED", "READ_COMMITTED", "REPEATABLE_READ", "SERIALIZABLE"},
            tabName="subsys_infinispan_locking")
    public String getIsolation();
    @Override
    public void setIsolation(String isolation);
    
    @Override
    @Binding(detypedName="locking/striping")
    @FormItem(defaultValue="false",
            label="Striping",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_locking")
    public Boolean isStriping();
    @Override
    public void setStriping(Boolean striping);
    
    @Override
    @Binding(detypedName="locking/acquire-timeout")
    @FormItem(defaultValue="15000",
            label="Acquire Timeout",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Long getAcquireTimeout();
    @Override
    public void setAcquireTimeout(Long aquireTimeout);
    
    @Override
    @Binding(detypedName="locking/concurrency-level")
    @FormItem(defaultValue="1000",
            label="Concurrency Level",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Integer getConcurrencyLevel();
    @Override
    public void setConcurrencyLevel(Integer concurrencyLevel);
    
    
    // eviction attributes
    @Override
    @Binding(detypedName="eviction/strategy")
    @FormItem(defaultValue="NONE",
            label="Eviction Strategy",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "UNORDERED", "FIFO", "LRU", "LIRS"},
            tabName="subsys_infinispan_eviction")
    public String getEvictionStrategy();
    @Override
    public void setEvictionStrategy(String evictionStrategy);
    
    @Override
    @Binding(detypedName="eviction/max-entries")
    @FormItem(defaultValue="10000",
            label="Max Entries",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_eviction")
    public Integer getMaxEntries();
    @Override
    public void setMaxEntries(Integer maxEntries);
    
    
    // expiration attributes
    @Override
    @Binding(detypedName="expiration/max-idle")
    @FormItem(defaultValue="-1",
            label="Max Idle",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getMaxIdle();
    @Override
    public void setMaxIdle(Long maxIdle);
    
    @Override
    @Binding(detypedName="expiration/lifespan")
    @FormItem(defaultValue="-1",
            label="Lifespan",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getLifespan();
    @Override
    public void setLifespan(Long lifespan);
    
    @Override
    @Binding(detypedName="expiration/interval")
    @FormItem(defaultValue="5000",
            label="Interval",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getInterval();
    @Override
    public void setInterval(Long interval);
    
    // clustered-cache attributes
    @Override
    @Binding(detypedName="mode")
    @FormItem(defaultValue="ASYNC",
            label="Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"ASYNC", "SYNC"},
            tabName="subsys_infinispan_cluster")
    public String getMode();
    @Override
    public void setMode(String mode);
    
    @Override
    @Binding(detypedName="queue-size")
    @FormItem(defaultValue="1000",
            label="Queue Size",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_cluster")
    public Integer getQueueSize();
    @Override
    public void setQueueSize(Integer queueSize);
    
    @Override
    @Binding(detypedName="queue-flush-interval")
    @FormItem(defaultValue="10",
            label="Queue Flush Interval",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_cluster")
    public Long getQueueFlushInterval();
    @Override
    public void setQueueFlushInterval(Long queueFlushInterval);
    
    @Override
    @Binding(detypedName="remote-timeout")
    @FormItem(defaultValue="17500",
            label="Remote Timeout",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_cluster")
    public Long getRemoteTimeout();
    @Override
    public void setRemoteTimeout(Long remoteTimeout);
    
    @Binding(detypedName="state-transfer/enabled")
    @FormItem(defaultValue="true",
            label="Enabled",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_stateTransfer")
    public Boolean isStateTransferEnabled();
    public void setStateTransferEnabled(Boolean isStateTransferEnabled);
    
    @Binding(detypedName="state-tranfer/timeout")
    @FormItem(defaultValue="60000",
            label="Timeout",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_stateTransfer")
    public Long getStateTransferTimeout();
    public void setStateTransferTimeout(Long stateTransferTimeout);
    
    @Binding(detypedName="state-tranfer/flush-timeout")
    @FormItem(defaultValue="60000",
            label="Flush Timeout",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_stateTransfer")
    public Long getStateTransferFlushTimeout();
    public void setStateTransferFlushTimeout(Long stateTransferFlushTimeout);
}
