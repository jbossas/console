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
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=infinispan/cache-container={0}/local-cache={1}/")
public interface LocalCache extends NamedEntity {
    @Override
    @Binding(detypedName="local-cache")
    @FormItem(defaultValue="",
              label="Name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX")
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName="cache-container")
    @FormItem(defaultValue="",
              label="Cache Container",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="COMBO_BOX")
    public String getCacheContainer();
    public void setCacheContainer(String cacheContainerName);

    // This one isn't actually a cache attribute.
    // It is set by the console to display if it is the default cache
    // for its cache container.
    @Binding(detypedName="default-for-cache-container")
    @FormItem(defaultValue="false",
            label="Default for cache container?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public Boolean isDefault();
    public void setDefault(Boolean isDefault);

    @Binding(detypedName="start")
    @FormItem(defaultValue="LAZY",
            label="Start Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"EAGER", "LAZY"})
    public String getStart();
    public void setStart(String start);

    @Binding(detypedName="batching")
    @FormItem(defaultValue="false",
            label="Batching",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public Boolean isBatching();
    public void setBatching(Boolean isBatching);

    @Binding(detypedName="indexing")
    @FormItem(defaultValue="NONE",
            label="Indexing",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "LOCAL", "ALL"})
    public String getIndexing();
    public void setIndexing(String indexing);

    @Binding(detypedName="jndi-name")
    @FormItem(defaultValue="NONE",
            label="Indexing",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    public String getJndiName();
    public void setJndiName(String jndiName);

    // Locking attributes
    @Binding(detypedName="locking/isolation")
    @FormItem(defaultValue="REPEATABLE_READ",
            label="Isolation",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "READ_UNCOMMITTED", "READ_COMMITTED", "REPEATABLE_READ", "SERIALIZABLE"},
            tabName="subsys_infinispan_locking")
    public String getIsolation();
    public void setIsolation(String isolation);

    @Binding(detypedName="locking/striping")
    @FormItem(defaultValue="false",
            label="Striping",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_locking")
    public Boolean isStriping();
    public void setStriping(Boolean striping);

    @Binding(detypedName="locking/acquire-timeout")
    @FormItem(defaultValue="15000",
            label="Acquire Timeout",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Long getAcquireTimeout();
    public void setAcquireTimeout(Long aquireTimeout);

    @Binding(detypedName="locking/concurrency-level")
    @FormItem(defaultValue="1000",
            label="Concurrency Level",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Integer getConcurrencyLevel();
    public void setConcurrencyLevel(Integer concurrencyLevel);

    // transaction attributes
    @Binding(detypedName="transaction/TRANSACTION/mode")
    @FormItem(defaultValue="NONE",
            label="Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "NONE_XA", "NONE_DURABLE_XA", "FULL_XA"},
            tabName="subsys_infinispan_transaction")
    public String getMode();
    public void setMode(String mode);

    @Binding(detypedName="transaction/TRANSACTION/stop-timeout")
    @FormItem(defaultValue="30000",
            label="Stop Timeout (ms)",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_transaction")
    public Long getStopTimeout();
    public void setStopTimeout(Long stopTimeout);

    @Binding(detypedName="transaction/TRANSACTION/locking")
    @FormItem(defaultValue="OPTIMISTIC",
            label="Locking",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"OPTIMISTIC","PESSIMISTIC"},
            tabName="subsys_infinispan_transaction")
    public String getLocking();
    public void setLocking(String locking);

    // eviction attributes
    @Binding(detypedName="eviction/EVICTION/strategy")
    @FormItem(defaultValue="NONE",
            label="Eviction Strategy",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "UNORDERED", "FIFO", "LRU", "LIRS"},
            tabName="subsys_infinispan_eviction")
    public String getEvictionStrategy();
    public void setEvictionStrategy(String evictionStrategy);

    @Binding(detypedName="eviction/EVICTION/max-entries")
    @FormItem(defaultValue="10000",
            label="Max Entries",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_eviction")
    public Integer getMaxEntries();
    public void setMaxEntries(Integer maxEntries);


    // expiration attributes
    @Binding(detypedName="expiration/EXPIRATION/max-idle")
    @FormItem(defaultValue="-1",
            label="Max Idle",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getMaxIdle();
    public void setMaxIdle(Long maxIdle);

    @Binding(detypedName="expiration/EXPIRATION/lifespan")
    @FormItem(defaultValue="-1",
            label="Lifespan",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getLifespan();
    public void setLifespan(Long lifespan);

    @Binding(detypedName="expiration/EXPIRATION/interval")
    @FormItem(defaultValue="5000",
            label="Interval",
            required=true,
            formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE",
            formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE",
            tabName="subsys_infinispan_expiration")
    public Long getInterval();
    public void setInterval(Long interval);

    // Store tab
    @Binding(detypedName="store/STORE/class")
    @FormItem(defaultValue="NONE",
            label="Eviction Strategy",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_store")
    public String getStoreClass();
    public void setStoreClass(String storeClass);

    @Binding(detypedName="store/STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isShared();
    public void setShared(Boolean isShared);

    @Binding(detypedName="store/STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPreload();
    public void setPreload(Boolean isPreload);

    @Binding(detypedName="store/STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPassivation();
    public void setPassivation(Boolean isPassivation);

    @Binding(detypedName="store/STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isFetchState();
    public void setFetchState(Boolean isFetchState);

    @Binding(detypedName="store/STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isPurge();
    public void setPurge(Boolean isPurge);

    @Binding(detypedName="store/STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isSingleton();
    public void setSingleton(Boolean isSingleton);

   // ------ PROPERTIES TAB --------------
   @Binding(detypedName="store/STORE/properties",
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Store Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="CUSTOM")
   List<PropertyRecord> getProperties();
   void setProperties(List<PropertyRecord> properties);
}
