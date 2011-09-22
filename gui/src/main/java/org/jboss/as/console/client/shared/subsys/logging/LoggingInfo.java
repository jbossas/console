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

import com.allen_sauer.gwt.log.client.Log;
import java.util.Comparator;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * This class gathers all the information about loggers and handlers.  When refreshView() is called
 * it gets an updated copy of the logging info from the server and makes that info available to
 * the view.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggingInfo {
    
    private LoggerConfig rootLogger;
    private List<LoggerConfig> loggers = Collections.EMPTY_LIST;
    private List<LoggingHandler> handlers = Collections.EMPTY_LIST;
    
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private LoggingPresenter.MyView view;
    
    private String loggerConfigEdited;
    private String handlerEdited;
    
    private Comparator loggerComparator = new Comparator<LoggerConfig>() {
        @Override
        public int compare(LoggerConfig logger1, LoggerConfig logger2) {
            return logger1.getName().toLowerCase().compareTo(logger2.getName().toLowerCase());
        }
    };
    
    private Comparator handlerComparator = new Comparator<LoggingHandler>() {
        @Override
        public int compare(LoggingHandler handler1, LoggingHandler handler2) {
            return handler1.getName().toLowerCase().compareTo(handler2.getName().toLowerCase());
        }
    };
    
    public LoggingInfo(DispatchAsync dispatcher, BeanFactory factory, LoggingPresenter.MyView view) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.view = view;
    }
    
    /**
     * Get the name of the LoggerConfig just edited/added.
     * @return The name, or <code>null</code> if a LoggerConfig was not just edited.
     */
    public String getLoggerConfigEdited() {
        return this.loggerConfigEdited;
    }
    
    /**
     * Get the name of the Handler just edited/added.
     * @return The name, or <code>null</code> if a Handler was not just edited.
     */
    public String getHandlerEdited() {
        return this.handlerEdited;
    }
    
    public LoggerConfig getRootLogger() {
        return rootLogger;
    }
    
    public List<LoggerConfig> getLoggers() {
        return loggers;
    }
    
    public List<LoggingHandler> getHandlers() {
        return handlers;
    }
    
    public String[] getHandlerNames() {
        String[] handlerNames = new String[handlers.size()];
        for (int i=0 ; i < handlers.size(); i++) {
            handlerNames[i] = handlers.get(i).getName();
        }
        return handlerNames;
    }
    
    public LoggingHandler findHandler(String name) {
        for (LoggingHandler handler : handlers) {
            if (handler.getName().equals(name)) return handler;
        }
        
        return null;
    }
    
    public LoggerConfig findLoggerConfig(String name) {
        for (LoggerConfig logger : loggers) {
            if (logger.getName().equals(name)) return logger;
        }
        
        return null;
    }
    
    private void setRootLogger() {
        ModelNode operation = LoggingOperation.make(READ_RESOURCE_OPERATION);
        
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                ModelNode resultNode = response.get("result");
                ModelNode node = resultNode.get("root-logger");
                LoggingInfo.this.rootLogger = makeLogger(node, "root-logger");
            }
        });
    }
    
    private LoggerConfig makeLogger(ModelNode node, String name) {
        LoggerConfig model = factory.loggerConfig().as();
        model.setName(name);
        model.setLevel(node.get("level").asString());

        List<String> handlers = new ArrayList();

        if (node.hasDefined("handlers")) {
            List<ModelNode> handlerModels = node.get("handlers").asList();
            for (ModelNode handlerNode : handlerModels) {
                handlers.add(handlerNode.asString());
            }
        }

        model.setHandlers(handlers);

        return model;
    }

    /**
     * Gather all the LoggerConfig and Handler info from the server and update the UI.
     */
    public void refreshView(String nameEditedOrAdded, boolean isHandlerOp) {
        
        if (isHandlerOp) {
            this.loggerConfigEdited = null;
            this.handlerEdited = nameEditedOrAdded;
        } else {
            this.loggerConfigEdited = nameEditedOrAdded;
            this.handlerEdited = null;
        }
        
        setRootLogger();
        
        final List<LoggingHandler> handlers = new ArrayList<LoggingHandler>();
        final List<LoggerConfig> loggers = new ArrayList<LoggerConfig>();

        ModelNode operation = LoggingOperation.make(READ_CHILDREN_TYPES_OPERATION);

        // REFACTOR ME!! 
        // This finds all the handler types (plus loggers) then does a read-children-resources request for each one.
        // Instead, we need to just do a single recursive request for everything.
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                List<ModelNode> payload = response.get("result").asList();
                for (final ModelNode node : payload) {
                    final String handlerType = node.asString();
                    ModelNode operation = LoggingOperation.make(READ_CHILDREN_RESOURCES_OPERATION);
                    operation.get(CHILD_TYPE).set(handlerType);

                    dispatcher.execute(new DMRAction(operation), new DmrCallback() {
                        @Override
                        public void onDmrSuccess(ModelNode response) {
                            if (!response.get("result").isDefined()) return;
                            
                            List<ModelNode> payload = response.get("result").asList();


                            for (ModelNode item : payload) {
                                // returned as type property (key=handler name)
                                Property property = item.asProperty();
                                ModelNode node = property.getValue().asObject();
                                String name = property.getName();

                                try {
                                    if (isLogger(node)) {
                                        loggers.add(makeLogger(node, name));
                                    } else {
                                        handlers.add(makeHandler(node, name));
                                    }
                                } catch (IllegalArgumentException e) {
                                    Log.error(name + " : " + Console.CONSTANTS.common_error_failedToDecode(), e);
                                    return;
                                }
                            }

                            LoggingInfo.this.handlers = sortHandlers(handlers);
                            LoggingInfo.this.loggers = sortLoggers(loggers);
                            
                            view.updateLoggingInfo(LoggingInfo.this);
                        }
                        
                        private boolean isLogger(ModelNode node) {
                            return node.has("handlers");
                        }
                        
                        private List<LoggerConfig> sortLoggers(List<LoggerConfig> loggers) {
                            Collections.sort(loggers, loggerComparator);
                            return loggers;
                        }

                        private List<LoggingHandler> sortHandlers(List<LoggingHandler> handlers) {
                            Collections.sort(handlers, handlerComparator);
                            return handlers;
                        }
                        
                        private LoggingHandler makeHandler(ModelNode node, String name) {
                            LoggingHandler model = factory.loggingHandler().as();
                            model.setName(name);
                            model.setType(handlerType);
                            model.setLevel(node.get("level").asString());
                            
                            if (node.get("encoding").isDefined()) {
                                model.setEncoding(node.get("encoding").asString());
                            }
                            
                            model.setFilter(node.get("filter").asString());
                            
                            if (node.get("formatter").isDefined()) {
                                model.setFormatter(node.get("formatter").asString());
                            }
                            
                            if (node.get("autoflush").isDefined()) {
                                model.setAutoflush(node.get("autoflush").asBoolean());
                            }
                            
                            if (node.get("append").isDefined()) {
                                model.setAppend(node.get("append").asBoolean());
                            }
                            
                            if (node.get("file").isDefined()) {
                                model.setFileRelativeTo(node.get("file").get("relative-to").asString());
                                model.setFilePath(node.get("file").get("path").asString());
                            }
                            
                            model.setRotateSize(node.get("rotate-size").asString());
                            
                            if (node.get("max-backup-index").isDefined()) {
                                model.setMaxBackupIndex(node.get("max-backup-index").asString());
                            }
                            
                            model.setTarget(node.get("target").asString());
                            
                            model.setOverflowAction(node.get("overflow-action").asString());
                            
                            if (node.get("subhandlers").isDefined()) {
                                List<ModelNode> subhandlerNodes = node.get("subhandlers").asList();
                                List<String> subhandlers = new ArrayList<String>(subhandlerNodes.size());
                                for (ModelNode handlerNode : subhandlerNodes) {
                                    subhandlers.add(handlerNode.asString());
                                }
                                model.setSubhandlers(subhandlers);
                            }
                            
                            model.setQueueLength(node.get("queue-length").asString());
                            model.setSuffix(node.get("suffix").asString());

                            if (node.get("class").isDefined()) {
                                model.setClassName(node.get("class").asString());
                            }
                            
                            if (node.get("module").isDefined()) {
                                model.setModule(node.get("module").asString());
                            }
                            
                            List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
                            if (node.get("properties").isDefined()) {
                                List<Property> props = node.get("properties").asPropertyList();
                                for (Property prop : props) {
                                    PropertyRecord property = factory.property().as();
                                    property.setKey(prop.getName());
                                    property.setValue(prop.getValue().asString());
                                    properties.add(property);
                                }
                            }
                            model.setProperties(properties);
                            
                            return model;
                        }
                        
                    });
                }
            }
        });
    }
}
