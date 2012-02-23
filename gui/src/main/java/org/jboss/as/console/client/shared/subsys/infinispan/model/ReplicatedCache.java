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
              formItemTypeForAdd="TEXT_BOX",
              tabName="subsys_infinispan_attrs",
              order=1)
    public String getName();
    @Override
    public void setName(String name);

    @Override
    @Binding(detypedName="cache-container")
    @FormItem(defaultValue="",
              label="Cache Container",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="COMBO_BOX",
              tabName="subsys_infinispan_attrs",
              order=2)
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
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_attrs",
            order=3)
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
            acceptedValues={"EAGER", "LAZY"},
            tabName="subsys_infinispan_attrs")
    public String getStart();
    @Override
    public void setStart(String start);

    @Override
    @Binding(detypedName="batching")
    @FormItem(defaultValue="false",
            label="Batching",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_attrs")
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
            acceptedValues={"NONE", "LOCAL", "ALL"},
            tabName="subsys_infinispan_attrs")
    public String getIndexing();
    @Override
    public void setIndexing(String indexing);

    @Override
    @Binding(detypedName="jndi-name")
    @FormItem(label="JNDI Name",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_attrs")
    public String getJndiName();
    @Override
    public void setJndiName(String jndiName);

    // Not part of detyped model.  This is a flag to tell us if locking
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="locking/has-locking")
    @FormItem(defaultValue="false",
            label="Is locking defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_locking")
    public boolean isHasLocking();
    @Override
    public void setHasLocking(boolean hasLocking);

    // Locking attributes
    @Override
    @Binding(detypedName="locking/LOCKING/isolation")
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
    @Binding(detypedName="locking/LOCKING/striping")
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
    @Binding(detypedName="locking/LOCKING/acquire-timeout")
    @FormItem(defaultValue="15000",
            label="Acquire Timeout (ms)",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Long getAcquireTimeout();
    @Override
    public void setAcquireTimeout(Long aquireTimeout);

    @Override
    @Binding(detypedName="locking/LOCKING/concurrency-level")
    @FormItem(defaultValue="1000",
            label="Concurrency Level",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_locking")
    public Integer getConcurrencyLevel();
    @Override
    public void setConcurrencyLevel(Integer concurrencyLevel);

    // Not part of detyped model.  This is a flag to tell us if transaction
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="transaction/has-transaction")
    @FormItem(defaultValue="false",
            label="Is transaction defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_transaction")
    public boolean isHasTransaction();
    @Override
    public void setHasTransaction(boolean hasTransaction);

    // transaction attributes
    @Override
    @Binding(detypedName="transaction/TRANSACTION/mode")
    @FormItem(defaultValue="NONE",
            label="Mode",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"NONE", "NON_XA", "NON_DURABLE_XA", "FULL_XA"},
            tabName="subsys_infinispan_transaction")
    public String getTransactionMode();
    @Override
    public void setTransactionMode(String transactionMode);

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

    // Not part of detyped model.  This is a flag to tell us if eviction
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="eviction/has-eviction")
    @FormItem(defaultValue="false",
            label="Is eviction defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_eviction")
    public boolean isHasEviction();
    @Override
    public void setHasEviction(boolean hasEviction);

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

    // Not part of detyped model.  This is a flag to tell us if expiration
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="expiration/has-expiration")
    @FormItem(defaultValue="false",
            label="Is expiration defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_expiration")
    public boolean isHasExpiration();
    @Override
    public void setHasExpiration(boolean hasExpiration);

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

    // Not part of detyped model.  This is a flag to tell us if store
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="store/has-store")
    @FormItem(defaultValue="false",
            label="Is store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_store")
    public boolean isHasStore();
    @Override
    public void setHasStore(boolean hasStore);

    // Store attributes
    @Override
    @Binding(detypedName="store/STORE/class")
    @FormItem(defaultValue="NONE",
            label="Store Impl Class",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            order=2,
            tabName="subsys_infinispan_store")
    public String getStoreClass();
    @Override
    public void setStoreClass(String storeClass);

    @Override
    @Binding(detypedName="store/STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStoreShared();
    @Override
    public void setStoreShared(Boolean isShared);

    @Override
    @Binding(detypedName="store/STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePreload();
    @Override
    public void setStorePreload(Boolean isPreload);

    @Override
    @Binding(detypedName="store/STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePassivation();
    @Override
    public void setStorePassivation(Boolean isPassivation);

    @Override
    @Binding(detypedName="store/STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStoreFetchState();
    @Override
    public void setStoreFetchState(Boolean isFetchState);

    @Override
    @Binding(detypedName="store/STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStorePurge();
    @Override
    public void setStorePurge(Boolean isPurge);

    @Override
    @Binding(detypedName="store/STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_store")
    public Boolean isStoreSingleton();
    @Override
    public void setStoreSingleton(Boolean isSingleton);

    @Override
    @Binding(detypedName="store/STORE/properties",
            listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
    @FormItem(defaultValue="",
             label="Store Properties",
             required=false,
             formItemTypeForEdit="PROPERTY_EDITOR",
             formItemTypeForAdd="PROPERTY_EDITOR",
             tabName="subsys_infinispan_store")
    List<PropertyRecord> getStoreProperties();
    @Override
    void setStoreProperties(List<PropertyRecord> properties);

    // Not part of detyped model.  This is a flag to tell us if file-store
    // singleton needs to be added to or removed from the model.
    @Override
    @Binding(detypedName="file-store/has-file-store")
    @FormItem(defaultValue="false",
             label="Is file store defined?",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             order=1,
             tabName="subsys_infinispan_file_store")
     public boolean isHasFileStore();
     @Override
     public void setHasFileStore(boolean hasFileStore);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/shared")
     @FormItem(defaultValue="false",
             label="Shared",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStoreShared();
     @Override
     public void setFileStoreShared(Boolean isShared);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/preload")
     @FormItem(defaultValue="false",
             label="Preload",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStorePreload();
     @Override
     public void setFileStorePreload(Boolean isPreload);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/passivation")
     @FormItem(defaultValue="true",
             label="Passivation",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStorePassivation();
     @Override
     public void setFileStorePassivation(Boolean isPassivation);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/fetch-state")
     @FormItem(defaultValue="true",
             label="Fetch State",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStoreFetchState();
     @Override
     public void setFileStoreFetchState(Boolean isFetchState);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/purge")
     @FormItem(defaultValue="true",
             label="Purge",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStorePurge();
     @Override
     public void setFileStorePurge(Boolean isPurge);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/singleton")
     @FormItem(defaultValue="false",
             label="Singletion",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             tabName="subsys_infinispan_file_store")
     public Boolean isFileStoreSingleton();
     @Override
     public void setFileStoreSingleton(Boolean isSingleton);

     @Override
     @Binding(detypedName="file-store/FILE_STORE/properties",
             listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
     @FormItem(defaultValue="",
              label="Store Properties",
              required=false,
              formItemTypeForEdit="PROPERTY_EDITOR",
              formItemTypeForAdd="PROPERTY_EDITOR",
              tabName="subsys_infinispan_file_store")
     List<PropertyRecord> getFileStoreProperties();
     @Override
     void setFileStoreProperties(List<PropertyRecord> properties);

   // Not part of detyped model.  This is a flag to tell us if jdbc-store
   // singleton needs to be added to or removed from the model.
   @Override
   @Binding(detypedName="jdbc-store/has-jdbc-store")
   @FormItem(defaultValue="false",
            label="Is JDBC store defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_jdbc_store")
    public boolean isHasJdbcStore();
    @Override
    public void setHasJdbcStore(boolean hasJdbcStore);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/datasource")
    @FormItem(defaultValue="",
            label="Datasource",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            order=2,
            tabName="subsys_infinispan_jdbc_store")
    public String getJdbcStoreDatasource();
    @Override
    public void setJdbcStoreDatasource(String jdbcStoreDatasource);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/shared")
    @FormItem(defaultValue="false",
            label="Shared",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreShared();
    @Override
    public void setJdbcStoreShared(Boolean isShared);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/preload")
    @FormItem(defaultValue="false",
            label="Preload",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePreload();
    @Override
    public void setJdbcStorePreload(Boolean isPreload);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/passivation")
    @FormItem(defaultValue="true",
            label="Passivation",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePassivation();
    @Override
    public void setJdbcStorePassivation(Boolean isPassivation);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/fetch-state")
    @FormItem(defaultValue="true",
            label="Fetch State",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreFetchState();
    @Override
    public void setJdbcStoreFetchState(Boolean isFetchState);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/purge")
    @FormItem(defaultValue="true",
            label="Purge",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStorePurge();
    @Override
    public void setJdbcStorePurge(Boolean isPurge);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/singleton")
    @FormItem(defaultValue="false",
            label="Singletion",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            tabName="subsys_infinispan_jdbc_store")
    public Boolean isJdbcStoreSingleton();
    @Override
    public void setJdbcStoreSingleton(Boolean isSingleton);

    @Override
    @Binding(detypedName="jdbc-store/JDBC_STORE/properties",
            listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
    @FormItem(defaultValue="",
             label="Store Properties",
             required=false,
             formItemTypeForEdit="PROPERTY_EDITOR",
             formItemTypeForAdd="PROPERTY_EDITOR",
             tabName="subsys_infinispan_jdbc_store")
    List<PropertyRecord> getJdbcStoreProperties();
    @Override
    void setJdbcStoreProperties(List<PropertyRecord> properties);

     // Not part of detyped model.  This is a flag to tell us if remote-store
     // singleton needs to be added to or removed from the model.
     @Override
     @Binding(detypedName="remote-store/has-remote-store")
     @FormItem(defaultValue="false",
              label="Is remote store defined?",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              order=1,
              tabName="subsys_infinispan_remote_store")
      public boolean isHasRemoteStore();
      @Override
      public void setHasRemoteStore(boolean hasFileStore);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/shared")
      @FormItem(defaultValue="false",
              label="Shared",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStoreShared();
      @Override
      public void setRemoteStoreShared(Boolean isShared);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/preload")
      @FormItem(defaultValue="false",
              label="Preload",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStorePreload();
      @Override
      public void setRemoteStorePreload(Boolean isPreload);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/passivation")
      @FormItem(defaultValue="true",
              label="Passivation",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStorePassivation();
      @Override
      public void setRemoteStorePassivation(Boolean isPassivation);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/fetch-state")
      @FormItem(defaultValue="true",
              label="Fetch State",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStoreFetchState();
      @Override
      public void setRemoteStoreFetchState(Boolean isFetchState);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/purge")
      @FormItem(defaultValue="true",
              label="Purge",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStorePurge();
      @Override
      public void setRemoteStorePurge(Boolean isPurge);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/singleton")
      @FormItem(defaultValue="false",
              label="Singletion",
              required=false,
              formItemTypeForEdit="CHECK_BOX",
              formItemTypeForAdd="CHECK_BOX",
              tabName="subsys_infinispan_remote_store")
      public Boolean isRemoteStoreSingleton();
      @Override
      public void setRemoteStoreSingleton(Boolean isSingleton);

      @Override
      @Binding(detypedName="remote-store/REMOTE_STORE/properties",
              listType="org.jboss.as.console.client.shared.properties.PropertyRecord")
      @FormItem(defaultValue="",
               label="Store Properties",
               required=false,
               formItemTypeForEdit="PROPERTY_EDITOR",
               formItemTypeForAdd="PROPERTY_EDITOR",
               tabName="subsys_infinispan_remote_store")
      List<PropertyRecord> getRemoteStoreProperties();
      @Override
      void setRemoteStoreProperties(List<PropertyRecord> properties);

    // attributes inherited from InvalidationCache
    @Override
    @Binding(detypedName="mode")
    @FormItem(defaultValue="SYNC",
            label="Clustered Cache Mode",
            required=true,
            formItemTypeForEdit="COMBO_BOX",
            formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"SYNC", "ASYNC"},
            tabName="subsys_infinispan_attrs")
    public String getClusteredCacheMode();
    @Override
    public void setClusteredCacheMode(String clusteredCacheMode);

    @Override
    @Binding(detypedName="queue-size")
    @FormItem(defaultValue="1000",
            label="Queue Size",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_attrs")
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
            tabName="subsys_infinispan_attrs")
    public Long getQueueFlushInterval();
    @Override
    public void setQueueFlushInterval(Long queueFlushInterval);

    @Override
    @Binding(detypedName="remote-timeout")
    @FormItem(defaultValue="17500",
            label="Remote Timeout (ms)",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_attrs")
    public Long getRemoteTimeout();
    @Override
    public void setRemoteTimeout(Long remoteTimeout);

    // attributes not inherited from InvalidationCache or LocalCache
    // Not part of detyped model.  This is a flag to tell us if remote-store
    // singleton needs to be added to or removed from the model.
    @Binding(detypedName="state-transfer/has-state-transfer")
    @FormItem(defaultValue="false",
             label="Is state tranfer defined?",
             required=false,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             order=1,
             tabName="subsys_infinispan_stateTransfer")
    public boolean isHasStateTransfer();
    public void setHasStateTransfer(boolean hasStateTranfer);

    @Binding(detypedName="state-transfer/STATE_TRANSFER/enabled")
    @FormItem(defaultValue="true",
             label="Enabled",
             required=true,
             formItemTypeForEdit="CHECK_BOX",
             formItemTypeForAdd="CHECK_BOX",
             order=2,
             tabName="subsys_infinispan_stateTransfer")
    public Boolean isStateTransferEnabled();
    public void setStateTransferEnabled(Boolean isStateTransferEnabled);

    @Binding(detypedName="state-transfer/STATE_TRANSFER/timeout")
    @FormItem(defaultValue="60000",
            label="Timeout (ms)",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_stateTransfer")
    public Long getStateTransferTimeout();
    public void setStateTransferTimeout(Long stateTransferTimeout);

    @Binding(detypedName="state-transfer/STATE_TRANSFER/chunk-size")
    @FormItem(defaultValue="10000",
            label="Chunk Size",
            required=false,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_stateTransfer")
    public Integer getStateTransferChunkSize();
    public void setStateTransferChunkSize(Integer stateTransferChunkSize);
}
