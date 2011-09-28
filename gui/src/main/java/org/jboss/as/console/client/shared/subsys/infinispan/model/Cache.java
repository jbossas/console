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

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface Cache extends NamedEntity {
    // top level attributes
        // just getName/setName for now
    
    // Locking attributes
    public void setIsolation(String isolation);
    public String getIsolation();
    
    public void setStriping(Boolean striping);
    public Boolean getStriping();
    
    public void setAcquireTimeout(Long aquireTimeout);
    public Long getAcquireTimeout();
    
    public void setConcurrencyLevel(Integer concurrencyLevel);
    public Integer getConcurrencyLevel();
    
    // eviction attributes
    public void setEvictionStrategy(String evictionStrategy);
    public String getEvictionStrategy();
    
    public void setMaxEntries(Integer maxEntries);
    public Integer getMaxEntries();
    
    // expiration attributes
    public void setMaxIdle(Long maxIdle);
    public Long getMaxIdle();
    
    public void setLifespan(Long lifespan);
    public Long getLifespan();
    
    public void setInterval(Long interval);
    public Long getInterval();
    
    
}
