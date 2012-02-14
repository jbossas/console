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

import java.util.List;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
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
    @Binding(detypedName="start")
    @FormItem(defaultValue="LAZY",
            label="Start Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"EAGER", "LAZY"})
    public String getStart();
    @Override
    public void setStart(String start);

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

    @Override
    @Binding(detypedName="jndi-name")
    @FormItem(defaultValue="NONE",
            label="Indexing",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    public String getJndiName();
    @Override
    public void setJndiName(String jndiName);

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

    // transaction attributes
    @Override
    @Binding(detypedName="transaction/TRANSACTION/mode")
    @FormItem(defaultValue="NONE",
            label="Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "NONE_XA", "NONE_DURABLE_XA", "FULL_XA"},
            tabName="subsys_infinispan_transaction")
    public String getMode();
    @Override
    public void setMode(String mode);

    @Override
    @Binding(detypedName="transaction/TRANSACTION/stop-timeout")
    @FormItem(defaultValue="30000",
            label="Stop Timeout (ms)",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_transaction")
    public Long getStopTimeout();
    @Override
    public void setStopTimeout(Long stopTimeout);

    @Override
    @Binding(detypedName="transaction/TRANSACTION/locking")
    @FormItem(defaultValue="OPTIMISTIC",
            label="Locking",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"OPTIMISTIC","PESSIMISTIC"},
            tabName="subsys_infinispan_transaction")
    public String getLocking();
    @Override
    public void setLocking(String locking);

    // eviction attributes
    @Override
    @Binding(detypedName="eviction/EVICTION/strategy")
    @FormItem(defaultValue="NONE",
            label="Eviction Strategy",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "UNORDERED", "FIFO", "LRU", "LIRS"},
            tabName="subsys_infinispan_eviction")
    public String getEvictionStrategy();
    @Override
    public void setEvictionStrategy(String evictionStrategy);

    @Override
    @Binding(detypedName="eviction/EVICTION/max-entries")
    @FormItem(defaultValue="10000",
            label="Max Entries",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_eviction")
    public Integer getMaxEntries();
    @Override
    public void setMaxEntries(Integer maxEntries);


    // expiration attributes
    @Override
    @Binding(detypedName="expiration/EXPIRATION/max-idle")
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
    @Binding(detypedName="expiration/EXPIRATION/lifespan")
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
    @Binding(detypedName="expiration/EXPIRATION/interval")
    @FormItem(defaultValue="5000",
            label="Interval",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getInterval();
    @Override
    public void setInterval(Long interval);

    // Store tab
    @Override
    @Binding(detypedName="store/STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isShared();
    @Override
    public void setShared(Boolean isShared);

    @Override
    @Binding(detypedName="store/STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPreload();
    @Override
    public void setPreload(Boolean isPreload);

    @Override
    @Binding(detypedName="store/STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPassivation();
    @Override
    public void setPassivation(Boolean isPassivation);

    @Override
    @Binding(detypedName="store/STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isFetchState();
    @Override
    public void setFetchState(Boolean isFetchState);

    @Override
    @Binding(detypedName="store/STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPurge();
    @Override
    public void setPurge(Boolean isPurge);

    @Override
    @Binding(detypedName="store/STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isSingleton();
    @Override
    public void setSingleton(Boolean isSingleton);

   // ------ PROPERTIES TAB --------------
    @Override
    @Binding(detypedName="store/STORE/properties",
            listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
    @FormItem(defaultValue="",
             label="Store Properties",
             required=false,
             formItemTypeForEdit="PROPERTY_EDITOR",
             formItemTypeForAdd="PROPERTY_EDITOR",
             tabName="CUSTOM")
    List<PropertyRecord> getProperties();
    @Override
    void setProperties(List<PropertyRecord> properties);

    // attributes inherited from InvalidationCache
    @Override
    @Binding(detypedName="queue-size")
    @FormItem(defaultValue="1000",
            label="Queue Size",
            required=true,
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
            required=true,
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
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_cluster")
    public Long getRemoteTimeout();
    @Override
    public void setRemoteTimeout(Long remoteTimeout);

    // attributes not inherited from InvalidationCache or LocalCache
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
