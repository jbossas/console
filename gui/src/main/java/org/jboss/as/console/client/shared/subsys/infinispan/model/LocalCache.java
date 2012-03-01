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
              formItemTypeForAdd="TEXT_BOX",
              order=1)
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName="cache-container")
    @FormItem(defaultValue="",
              label="Cache Container",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="COMBO_BOX",
              order=2)
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
            formItemTypeForAdd="CHECK_BOX",
            order=3)
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
    @FormItem(label="JNDI Name",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    public String getJndiName();
    public void setJndiName(String jndiName);

    // Not part of detyped model.  This is a flag to tell us if locking
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="locking/has-locking", skip = true)
    @FormItem(defaultValue="false",
            label="Is locking defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_locking")
    public boolean isHasLocking();
    public void setHasLocking(boolean hasLocking);

    // Locking attributes
    @Binding(detypedName="locking/LOCKING/isolation")
    @FormItem(defaultValue="REPEATABLE_READ",
            label="Isolation",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "READ_UNCOMMITTED", "READ_COMMITTED", "REPEATABLE_READ", "SERIALIZABLE"},
            tabName="subsys_infinispan_locking")
    public String getIsolation();
    public void setIsolation(String isolation);

    @Binding(detypedName="locking/LOCKING/striping")
    @FormItem(defaultValue="false",
            label="Striping",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_locking")
    public Boolean isStriping();
    public void setStriping(Boolean striping);

    @Binding(detypedName="locking/LOCKING/acquire-timeout")
    @FormItem(defaultValue="15000",
            label="Acquire Timeout (ms)",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Long getAcquireTimeout();
    public void setAcquireTimeout(Long aquireTimeout);

    @Binding(detypedName="locking/LOCKING/concurrency-level")
    @FormItem(defaultValue="1000",
            label="Concurrency Level",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Integer getConcurrencyLevel();
    public void setConcurrencyLevel(Integer concurrencyLevel);

    // Not part of detyped model.  This is a flag to tell us if transaction
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="transaction/has-transaction", skip = true)
    @FormItem(defaultValue="false",
            label="Is transaction defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_transaction")
    public boolean isHasTransaction();
    public void setHasTransaction(boolean hasTransaction);

    // transaction attributes
    @Binding(detypedName="transaction/TRANSACTION/mode")
    @FormItem(defaultValue="NONE",
            label="Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "NON_XA", "NON_DURABLE_XA", "FULL_XA"},
            tabName="subsys_infinispan_transaction")
    public String getTransactionMode();
    public void setTransactionMode(String transactionMode);

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

    // Not part of detyped model.  This is a flag to tell us if eviction
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="eviction/has-eviction", skip=true)
    @FormItem(defaultValue="false",
            label="Is eviction defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_eviction")
    public boolean isHasEviction();
    public void setHasEviction(boolean hasEviction);

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


    // Not part of detyped model.  This is a flag to tell us if expiration
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="expiration/has-expiration", skip=true)
    @FormItem(defaultValue="false",
            label="Is expiration defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_expiration")
    public boolean isHasExpiration();
    public void setHasExpiration(boolean hasExpiration);

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

    // Not part of detyped model.  This is a flag to tell us if store
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="store/has-store", skip=true)
    @FormItem(defaultValue="false",
            label="Is store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_store")
    public boolean isHasStore();
    public void setHasStore(boolean hasStore);

    // Store attributes
    @Binding(detypedName="store/STORE/class")
    @FormItem(label="Store Impl Class",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            order=2,
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
    public Boolean isStoreShared();
    public void setStoreShared(Boolean isShared);

    @Binding(detypedName="store/STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePreload();
    public void setStorePreload(Boolean isPreload);

    @Binding(detypedName="store/STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePassivation();
    public void setStorePassivation(Boolean isPassivation);

    @Binding(detypedName="store/STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStoreFetchState();
    public void setStoreFetchState(Boolean isFetchState);

    @Binding(detypedName="store/STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePurge();
    public void setStorePurge(Boolean isPurge);

    @Binding(detypedName="store/STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStoreSingleton();
    public void setStoreSingleton(Boolean isSingleton);

   @Binding(detypedName="store/STORE/properties",
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Store Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="subsys_infinispan_store")
   List<PropertyRecord> getStoreProperties();
   void setStoreProperties(List<PropertyRecord> properties);

   // Not part of detyped model.  This is a flag to tell us if file-store
   // singleton needs to be added to or removed from the model.
   @Binding(detypedName="file-store/has-file-store", skip=true)
   @FormItem(defaultValue="false",
            label="Is file store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_file_store")
    public boolean isHasFileStore();
    public void setHasFileStore(boolean hasFileStore);

    @Binding(detypedName="file-store/FILE_STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStoreShared();
    public void setFileStoreShared(Boolean isShared);

    @Binding(detypedName="file-store/FILE_STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStorePreload();
    public void setFileStorePreload(Boolean isPreload);

    @Binding(detypedName="file-store/FILE_STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStorePassivation();
    public void setFileStorePassivation(Boolean isPassivation);

    @Binding(detypedName="file-store/FILE_STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStoreFetchState();
    public void setFileStoreFetchState(Boolean isFetchState);

    @Binding(detypedName="file-store/FILE_STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStorePurge();
    public void setFileStorePurge(Boolean isPurge);

    @Binding(detypedName="file-store/FILE_STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_file_store")
    public Boolean isFileStoreSingleton();
    public void setFileStoreSingleton(Boolean isSingleton);

   @Binding(detypedName="file-store/FILE_STORE/properties",
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Store Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="subsys_infinispan_file_store")
   List<PropertyRecord> getFileStoreProperties();
   void setFileStoreProperties(List<PropertyRecord> properties);

   // Not part of detyped model.  This is a flag to tell us if jdbc-store
   // singleton needs to be added to or removed from the model.
   @Binding(detypedName="jdbc-store/has-jdbc-store", skip=true)
   @FormItem(defaultValue="false",
            label="Is JDBC store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_jdbc_store")
    public boolean isHasJdbcStore();
    public void setHasJdbcStore(boolean hasJdbcStore);

    @Binding(detypedName="jdbc-store/JDBC_STORE/datasource")
    @FormItem(defaultValue="",
            label="Datasource",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            order=2,
            tabName="subsys_infinispan_jdbc_store")
    public String getJdbcStoreDatasource();
    public void setJdbcStoreDatasource(String jdbcStoreDatasource);

    @Binding(detypedName="jdbc-store/JDBC_STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreShared();
    public void setJdbcStoreShared(Boolean isShared);

    @Binding(detypedName="jdbc-store/JDBC_STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePreload();
    public void setJdbcStorePreload(Boolean isPreload);

    @Binding(detypedName="jdbc-store/JDBC_STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePassivation();
    public void setJdbcStorePassivation(Boolean isPassivation);

    @Binding(detypedName="jdbc-store/JDBC_STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreFetchState();
    public void setJdbcStoreFetchState(Boolean isFetchState);

    @Binding(detypedName="jdbc-store/JDBC_STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePurge();
    public void setJdbcStorePurge(Boolean isPurge);

    @Binding(detypedName="jdbc-store/JDBC_STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreSingleton();
    public void setJdbcStoreSingleton(Boolean isSingleton);

   @Binding(detypedName="jdbc-store/JDBC_STORE/properties",
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Store Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="subsys_infinispan_jdbc_store")
   List<PropertyRecord> getJdbcStoreProperties();
   void setJdbcStoreProperties(List<PropertyRecord> properties);

   // Not part of detyped model.  This is a flag to tell us if remote-store
   // singleton needs to be added to or removed from the model.
   @Binding(detypedName="remote-store/has-remote-store", skip=true)
   @FormItem(defaultValue="false",
            label="Is remote store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_remote_store")
    public boolean isHasRemoteStore();
    public void setHasRemoteStore(boolean hasFileStore);

    @Binding(detypedName="remote-store/REMOTE_STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStoreShared();
    public void setRemoteStoreShared(Boolean isShared);

    @Binding(detypedName="remote-store/REMOTE_STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStorePreload();
    public void setRemoteStorePreload(Boolean isPreload);

    @Binding(detypedName="remote-store/REMOTE_STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStorePassivation();
    public void setRemoteStorePassivation(Boolean isPassivation);

    @Binding(detypedName="remote-store/REMOTE_STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStoreFetchState();
    public void setRemoteStoreFetchState(Boolean isFetchState);

    @Binding(detypedName="remote-store/REMOTE_STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStorePurge();
    public void setRemoteStorePurge(Boolean isPurge);

    @Binding(detypedName="remote-store/REMOTE_STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_remote_store")
    public Boolean isRemoteStoreSingleton();
    public void setRemoteStoreSingleton(Boolean isSingleton);

   @Binding(detypedName="remote-store/REMOTE_STORE/properties",
           listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
   @FormItem(defaultValue="",
            label="Store Properties",
            required=false,
            formItemTypeForEdit="PROPERTY_EDITOR",
            formItemTypeForAdd="PROPERTY_EDITOR",
            tabName="subsys_infinispan_remote_store")
   List<PropertyRecord> getRemoteStoreProperties();
   void setRemoteStoreProperties(List<PropertyRecord> properties);
}
