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

package org.jboss.as.console.client.shared.subsys.modcluster.model;

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * @author Pavel Slegr
 * @date 2/14/12
 */
public interface Modcluster {
    
    @Binding(detypedName="advertise")
    public boolean isAdvertise();
    public void setAdvertise(boolean advertise);
    
    @Binding(detypedName="advertise-socket")
    public String getAdvertiseSocket();
    public void setAdvertiseSocket(String advertiseSocket);
    
    @Binding(detypedName="advertise-security-key")
    public String getAdvertiseKey();
    public void setAdvertiseKey(String advertiseKey);
    
    @Binding(detypedName="auto-enableContexts")
    public boolean isAutoEnableContexts();
    public void setAutoEnableContexts(boolean autoEnableContexts);

    @Binding(detypedName="excluded-contexts")
    public String getExcludedContexts();
    public void setExcludedContexts(String excludedContexts);

    // "description" => "List of proxies, Format (hostname:port) separed with comas."
    @Binding(detypedName="proxy-list")
    public String getProxyList();
    public void setProxyList(String proxyList);

    //"description" => "Base URL for MCMP requests."
    @Binding(detypedName="proxy-url")
    public String getProxyUrl();
    public void setProxyUrl(String proxyUrl);

    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="socket-timeout")
    Integer getSocketTimeout();
    void setSocketTimeout(Integer socketTimeout);


    // skip SSL configuration for now - place it in the separate class
    @Binding(skip=true)
    String getSsl();
    
    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="stop-context-timeout")
    Integer getStopContextTimeout();
    void setStopContextTimeout(Integer stopContextTimeout);
    
    // Proxy Discovery Configuraiton attributes
    
    public String getBalancer();
    public void setBalancer(String balancer);

    public String getDomain();
    public void setDomain(String domain);

    @Binding(detypedName="flush-packets")
    public boolean isFlushPackets();
    public void setFlushPackets(boolean flushPackets);

    //TODO this has to be time value with MILISECONDS unit
    @Binding(detypedName="flush-wait")
    Integer getFlushWait();
    void setFlushWait(Integer flushWait);

    @Binding(detypedName="max-attemps")
    Integer getMaxAttemps();
    void setMaxAttemps(Integer maxAttemps);

    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="node-timeout")
    Integer getNodeTimeout();
    void setNodeTimeout(Integer nodeTimeout);

    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="ping")
    Integer getPing();
    void setPing(Integer ping);
    
    @Binding(detypedName="sticky-session")
    public boolean isStickySession();
    public void setStickySession(boolean stickySession);
    
    @Binding(detypedName="sticky-session-force")
    public boolean isStickySessionForce();
    public void setStickySessionForce(boolean stickySessionForce);

    @Binding(detypedName="sticky-session-remove")
    public boolean isStickySessionRemove();
    public void setStickySessionRemove(boolean stickySessionRemove);

    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="ttl")
    Integer getTtl();
    void setTtl(Integer ttl);
    
    //TODO this has to be time value with SECONDS unit
    @Binding(detypedName="worker-timeout")
    Integer getWorkerTimeout();
    void setWorkerTimeout(Integer workerTimeout);
    
    
}
