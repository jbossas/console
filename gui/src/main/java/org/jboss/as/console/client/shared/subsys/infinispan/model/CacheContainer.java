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

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

import java.util.List;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=infinispan/cache-container={0}")
public interface CacheContainer extends NamedEntity {
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX",
              order=1)
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName= "default-cache")
    @FormItem(defaultValue="",
            localLabel="subsys_infinispan_default_cache",
            required=true,
            formItemTypeForEdit="TEXT",
            formItemTypeForAdd="TEXT_BOX",
            order=2)
    String getDefaultCache();
    void setDefaultCache(String defaultCache);

    @Binding(detypedName = "jndi-name")
    @FormItem(localLabel="subsys_infinispan_jndiName",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    String getJndiName();
    void setJndiName(String jndiName);

    @Binding(detypedName= "start")
    @FormItem(defaultValue="EAGER",
            label="Start",
            required=false,
            formItemTypeForEdit="COMBO_BOX",
              formItemTypeForAdd="COMBO_BOX",
            acceptedValues={"EAGER", "LAZY"})
    String getStart();
    void setStart(String start);

    @Binding(detypedName="eviction-executor")
    @FormItem(localLabel="subsys_infinispan_evictionExecutor",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    String getEvictionExecutor();
    void setEvictionExecutor(String evictionExecutor);

    @Binding(detypedName="replication-queue-executor")
    @FormItem(localLabel="subsys_infinispan_replicationQueueExecutor",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    String getReplicationQueueExecutor();
    void setReplicationQueueExecutor(String replicationQueueExecutor);

    @Binding(detypedName="listener-executor")
    @FormItem(localLabel="subsys_infinispan_listenerExecutor",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX")
    String getListenerExecutor();
    void setListenerExecutor(String listenerExecutor);

    // Not part of detyped model.  This is a flag to tell us if the transport
    // needs to be added to or removed from the model.
    @Binding(detypedName="has-transport", skip=true)
    @FormItem(defaultValue="false",
            label="Is transport defined?",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX",
            order=1,
            tabName="subsys_infinispan_transport")
    public boolean isHasTransport();
    public void setHasTransport(boolean hasTransport);

    // Transport tab
    @Binding(detypedName="transport/TRANSPORT/stack")
    @FormItem(label="Stack",
            required=true,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            order=2,
            tabName="subsys_infinispan_transport")
    String getStack();
    void setStack(String stack);

    @Binding(detypedName="transport/TRANSPORT/executor")
    @FormItem(label="Executor",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_transport")
    String getExecutor();
    void setExecutor(String executor);

    @Binding(detypedName="transport/TRANSPORT/site")
    @FormItem(label="Site",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_transport")
    String getSite();
    void setSite(String site);

    @Binding(detypedName="transport/TRANSPORT/lock-timeout")
    @FormItem(defaultValue = "60000",
            label="Lock Timeout (ms)",
            required=true,
            formItemTypeForEdit="NUMBER_BOX",
            formItemTypeForAdd="NUMBER_BOX",
            tabName="subsys_infinispan_transport")
    Long getLockTimeout();
    void setLockTimeout(Long lockTimeout);

    @Binding(detypedName="transport/TRANSPORT/rack")
    @FormItem(label="Rack",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_transport")
    String getRack();
    void setRack(String rack);

    @Binding(detypedName="transport/TRANSPORT/machine")
    @FormItem(label="Machine",
            required=false,
            formItemTypeForEdit="TEXT_BOX",
            formItemTypeForAdd="TEXT_BOX",
            tabName="subsys_infinispan_transport")
    String getMachine();
    void setMachine(String machine);

    @Binding(detypedName="aliases",
             listType="java.lang.String")
    @FormItem(defaultValue="",
             label="Aliases",
             required=false,
             formItemTypeForEdit="UNLIMITED_STRING_LIST_EDITOR",
             formItemTypeForAdd="UNLIMITED_STRING_LIST_EDITOR",
             tabName="CUSTOM")
    public List<String> getAliases();
    public void setAliases(List<String> aliases);
}
