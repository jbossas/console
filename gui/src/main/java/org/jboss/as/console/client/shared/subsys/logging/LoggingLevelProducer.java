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

import java.util.List;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This class reads all the allowed log levels and notifies all the consumers of the result.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggingLevelProducer {
    
    private LoggingLevelProducer() {} // don't allow instance
    
    static interface LogLevelConsumer {
        void setLogLevels(String[] logLevels);
    }
    
    /**
     * Query the server to find the valid log levels and pass them on to the specified
     * LogLevelConsumers.
     */
    static void setLogLevels(DispatchAsync dispatcher, final LogLevelConsumer... consumers) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "logging");
        operation.get(ADDRESS).add("console-handler", "*");
        
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                List<ModelNode> resultList = response.get("result").asList();
                ModelNode resultNode = null;
                for (ModelNode node : resultList) {
                    if (node.hasDefined("result")) {
                        resultNode = node;
                        break;
                    }
                }
                
                List<ModelNode> levels = resultNode.get("result", "attributes", "level", "allowed").asList();
                String[] loggingLevels = new String[levels.size()];
                for (int i=0; i < loggingLevels.length; i++) {
                    loggingLevels[i] = levels.get(i).asString();
                }
                
                for (LogLevelConsumer consumer : consumers) {
                    consumer.setLogLevels(loggingLevels);
                }
            }
        });
    }
    
}
