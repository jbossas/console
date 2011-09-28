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
package org.jboss.as.console.client.shared.subsys.infinispan;

import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.subsys.infinispan.model.CacheContainer;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityToDmrBridge;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheContainerBridge extends AbstractEntityToDmrBridge<CacheContainer> {

    private BeanFactory beanFactory;
    
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public CacheContainer makeEntity(Property props) {
        CacheContainer cacheContainer = beanFactory.cacheContainer().as();
        cacheContainer.setName(props.getName());
        ModelNode values = props.getValue();
        
        cacheContainer.setDefaultCache(values.get(attributes.findAttribute("defaultCache").getDmrName()).asString());
        cacheContainer.setEvictionExecutor(values.get(attributes.findAttribute("evictionExecutor").getDmrName()).asString());
        cacheContainer.setJndiName(values.get(attributes.findAttribute("jndiName").getDmrName()).asString());
        cacheContainer.setReplicationQueueExecutor(values.get(attributes.findAttribute("replicationQueueExecutor").getDmrName()).asString());
        cacheContainer.setListenerExecutor(values.get(attributes.findAttribute("listenerExecutor").getDmrName()).asString());
        return cacheContainer;
    }

    @Override
    public CacheContainer newEntity() {
        return beanFactory.cacheContainer().as();
    }
    
}
