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
package org.jboss.as.console.client.shared.subsys.logging;

import java.util.ArrayList;
import java.util.List;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;

/**
 * This class manages the aggregated list of handlers.  The list of handlers is provided
 * to the user when he wants to add a handler to a logger or add a subhandler to an async handler.
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HandlerListManager {
   
    private HandlerConsumer[] consumers;
    
    private HandlerProducer[] producers;
    
    public void setHandlerConsumers(HandlerConsumer... consumers) {
        this.consumers = consumers;
    }
    
    public void setHandlerProducers(HandlerProducer... producers) {
        this.producers = producers;
    }
    
    /**
     * HandlerProducers must call handlerListUpdated whenever a handler is created or destroyed.  
     * This method in turn gathers all the handlers from all HandlerProducers and notifies all
     * the HandlerConsumers.
     */
    public void handlerListUpdated() {
        List<String> aggregatedHandlerList = new ArrayList<String>();
        
        for (HandlerProducer producer : producers) {
            List<NamedEntity> handlers = producer.getHandlers();
            for (NamedEntity handler : handlers) {
                aggregatedHandlerList.add(handler.getName());
            }
        }
        
        for (HandlerConsumer consumer : consumers) {
            consumer.handlersUpdated(aggregatedHandlerList);
        }
    }
}
