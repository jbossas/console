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

package org.jboss.as.console.client.shared.subsys.messaging.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
@Address("/subsystem=messaging/hornetq-server={0}/connection-factory={1}")
public interface ConnectionFactory {

    @Binding(skip = true)
    String getName();
    void setName(String name);

    @Binding(detypedName = "group-id")
    String getGroupId();
    void setGroupId(String id);

    @Binding(skip = true)
    String getJndiName();
    void setJndiName(String jndi);

    @Binding(skip=true)
    String getConnector();
    void setConnector(String connector);

    @Binding(detypedName = "call-timeout")
    Long getCallTimeout();
    void setCallTimeout(Long timeout);

    @Binding(detypedName = "compress-large-messages")
    boolean isCompressLarge();
    void setCompressLarge(boolean b);

    @Binding(detypedName = "connection-ttl")
    Long getConnectionTTL();
    void setConnectionTTL(Long ttl);

    @Binding(detypedName = "failover-on-initial-connection ")
    boolean isFailoverInitial();
    void setFailoverInitial(boolean b);


    @Binding(detypedName = "failover-on-server-shutdown")
    boolean isFailoverShutdown();
    void setFailoverShutdown(boolean b);

    @Binding(detypedName = "connection-load-balancing-policy-class-name")
    String getLoadbalancingClassName();
    void setLoadbalancingClassName(String name);

    @Binding(detypedName = "max-retry-interval")
    Long getMaxRetryInterval();
    void setMaxRetryInterval(Long interval);

    @Binding(detypedName = "min-large-message-size")
    Long getMinLargeMessageSize();
    void setMinLargeMessageSize(Long size);

    @Binding(detypedName = "reconnect-attempts")
    Long getReconnectAttempts();
    void setReconnectAttempts(Long numAttempts);

    @Binding(detypedName = "retry-interval")
    Long getRetryInterval();
    void setRetryInterval(Long interval);

    @Binding(detypedName = "thread-pool-max-size")
    Long getThreadPoolMax();
    void setThreadPoolMax(Long max);


    @Binding(detypedName = "transaction-batch-size")
    Long getTransactionBatchSize();
    void setTransactionBatchSize(Long size);

    @Binding(detypedName = "use-global-pools")
    boolean isUseGlobalPools();
    void setUseGlobalPools(boolean b);

    /*@Binding(detypedName = "factory-type")
    Integer getFactoryType();
    void setFactoryType(Integer type);

    @Binding(skip=true)
    String getFactoryTypeName();
    void setFactoryTypeName(String name);*/


}
