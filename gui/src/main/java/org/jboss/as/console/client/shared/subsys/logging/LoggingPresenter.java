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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * The Presenter for loggers (LoggerConfig) and Handlers (LoggingHandler).
 * @author Stan Silvert
 * @date 3/29/11
 */
public class LoggingPresenter extends Presenter<LoggingPresenter.MyView, LoggingPresenter.MyProxy> {
    private static final String ROOT_LOGGER = "root-logger";

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private LoggingInfo loggingInfo;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.LoggingPresenter)
    public interface MyProxy extends Proxy<LoggingPresenter>, Place {
    }

    public interface MyView extends View {

        void setPresenter(LoggingPresenter presenter);

        void updateLoggingInfo(LoggingInfo loggingInfo);
        
        void enableLoggerDetails(boolean isEnabled);
        
        void enableHandlerDetails(boolean isEnabled);
    }

    @Inject
    public LoggingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.loggingInfo = new LoggingInfo(dispatcher, factory, view);
        this.dispatcher = dispatcher;
        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loggingInfo.refreshView(null, false);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
    public BeanFactory getBeanFactory() {
        return this.factory;
    }
    
    public void onAddHandler(LoggingHandler handler) {
        final String name = handler.getName();
        HandlerType type = HandlerType.findHandlerType(handler.getType());
        
        ModelNode operation = LoggingOperation.make(ADD);
        operation.get(ADDRESS).add(type.getDisplayName(), name);
        
        operation.get(HandlerAttribute.NAME.getDmrName()).set(handler.getName());
        operation.get(HandlerAttribute.LEVEL.getDmrName()).set(handler.getLevel());
        
        if (type == HandlerType.CUSTOM) {
            operation.get(HandlerAttribute.CLASS.getDmrName()).set(handler.getClassName());
            operation.get(HandlerAttribute.MODULE.getDmrName()).set(handler.getModule());
        }
        
        HandlerAttribute[] stringAttributes = new HandlerAttribute[] { 
            HandlerAttribute.FORMATTER,
            HandlerAttribute.ROTATE_SIZE, HandlerAttribute.MAX_BACKUP_INDEX, HandlerAttribute.TARGET,
            HandlerAttribute.TARGET, HandlerAttribute.SUFFIX, HandlerAttribute.OVERFLOW_ACTION, HandlerAttribute.QUEUE_LENGTH };
        
        for (HandlerAttribute attrib : stringAttributes) {
            if (type.hasAttribute(attrib)) {
                operation.get(attrib.getDmrName()).set(attrib.getDefaultValue());
            }
        }
        
        HandlerAttribute[] booleanAttributes = new HandlerAttribute[] { HandlerAttribute.AUTOFLUSH, HandlerAttribute.APPEND };
        for (HandlerAttribute attrib : booleanAttributes) {
            if (type.hasAttribute(attrib)) {
                operation.get(attrib.getDmrName()).set(Boolean.parseBoolean(attrib.getDefaultValue()));
            }
        } 
        
        HandlerAttribute[] fileAttributes = new HandlerAttribute[] { HandlerAttribute.FILE_RELATIVE_TO, HandlerAttribute.FILE_PATH };
        for (HandlerAttribute attrib : fileAttributes) {
            if (type.hasAttribute(attrib)) {
                operation.get("file").get(attrib.getDmrName()).set(attrib.getDefaultValue());
            }
        } 
                
        execute(operation, name, true, "Success: Added handler " + name);
    }
    
    public void onRemoveHandler(final String name, String type) {
        ModelNode operation = LoggingOperation.make(REMOVE);
        operation.get(ADDRESS).add(type, name);
        
        execute(operation, null, true, "Success: Removed handler " + name);
    }
    
    public void onAssignHandlerToHandler(String handlerName, String handlerType, String handlerToAssign) {
        ModelNode operation = LoggingOperation.make("assign-subhandler");
        operation.get(ADDRESS).add(handlerType, handlerName);
        operation.get(NAME).set(handlerToAssign);
        
        execute(operation, handlerName, true, "Success: Assigned subhandler " + handlerToAssign + " to handler " + handlerName);
    }
    
    public void onUnassignHandlerFromHandler(String handlerName, String handlerType, String handlerToUnassign) {
        ModelNode operation = LoggingOperation.make("unassign-subhandler");
        operation.get(ADDRESS).add(handlerType, handlerName);
        operation.get(NAME).set(handlerToUnassign);
        
        execute(operation, handlerName, true, "Success: Unassigned subhandler " + handlerToUnassign + " from handler " + handlerName);
    }
    
    public void onAddLogger(final String name, final String level) {
        ModelNode operation = LoggingOperation.make(ADD);
        operation.get(ADDRESS).add("logger", name);
        operation.get("category").set(name);
        operation.get("level").set(level);
        
        execute(operation, name, false, "Success: Added logger " + name);
    }
    
    public void onRemoveLogger(final String name) {
        ModelNode operation = null;
        if (name.equals(ROOT_LOGGER)) {
            operation = LoggingOperation.make("remove-root-logger");
        } else {
            operation = LoggingOperation.make(REMOVE);
            operation.get(ADDRESS).add("logger", name);
        }
        
        execute(operation, null, false, "Success: Removed logger " + name);
    }
    
    public void onAssignHandlerToLogger(String loggerName, String handlerName) {
        ModelNode operation = null;
        if (loggerName.equals(ROOT_LOGGER)) {
            operation = LoggingOperation.make("root-logger-assign-handler");
        } else {
            operation = LoggingOperation.make("assign-handler");
            operation.get(ADDRESS).add("logger", loggerName);
        }
        
        operation.get(NAME).set(handlerName);
        
        execute(operation, loggerName, false, "Success: Assigned handler " + handlerName + " to logger " + loggerName);
    }
    
    public void onUnassignHandlerFromLogger(String loggerName, String handlerName) {
        ModelNode operation = null;
        if (loggerName.equals(ROOT_LOGGER)) {
            operation = LoggingOperation.make("root-logger-unassign-handler");
        } else {
            operation = LoggingOperation.make("unassign-handler");
            operation.get(ADDRESS).add("logger", loggerName);
        }
        
        operation.get(NAME).set(handlerName);
        
        execute(operation, loggerName, false, "Success: Unssigned handler " + handlerName + " from logger " + loggerName);
    }
    
    public void onEditHandler() {
        getView().enableHandlerDetails(true);
    }
    
    public void onSaveHandlerDetails(final String name, String handlerType, Map<String, Object> changedValues) {
        getView().enableHandlerDetails(false);
        if (changedValues.isEmpty()) return;
        
        ModelNode operation = LoggingOperation.make("update-properties");
        operation.get(ADDRESS).add(handlerType, name);
        
        for (Map.Entry<String, Object> entry : changedValues.entrySet()) {
            HandlerAttribute attrib = HandlerAttribute.findHandlerAttribute(entry.getKey());
            String dmrName = attrib.getDmrName();
            Object value = entry.getValue();
            
            if ((attrib == HandlerAttribute.FILE_PATH) || (attrib == HandlerAttribute.FILE_RELATIVE_TO)) {
                operation.get("file").get(dmrName).set(value.toString());
            } else if (attrib == HandlerAttribute.PROPERTIES) {
                ModelNode propList = new ModelNode();
                List<PropertyRecord> properties = (List<PropertyRecord>)value;
                for (PropertyRecord prop : properties) {
                    propList.add(prop.getKey(), prop.getValue());
                }
                operation.get(dmrName).set(propList);
            } else {
                operation.get(dmrName).set(value.toString());
            }
        }


        //TODO: Workaround for https://issues.jboss.org/browse/AS7-2195
        operation.get("suffix").set(".yyyy-MM-dd");


        System.out.println(operation);

        execute(operation, name, true, "Success: Updated Log Level");
    }
    
    public void onEditLogger() {
        getView().enableLoggerDetails(true);
    }
    
    public void onSaveLoggerDetails(final String name, Map<String, Object> changedValues) {
        getView().enableLoggerDetails(false);
        if (changedValues.isEmpty()) return;
        
        String newLevel = (String)changedValues.get("level");
        if (newLevel == null) return;
        
        // can only change level for now
        ModelNode operation = null;
        if (name.equals(ROOT_LOGGER)) {
            operation = LoggingOperation.make("change-root-log-level");
            if (this.loggingInfo.getRootLogger().getLevel().equals("undefined")) {
                operation = LoggingOperation.make("set-root-logger");
            }
        } else {
            operation = LoggingOperation.make("change-log-level");
            operation.get(ADDRESS).add("logger", name);
        }
        operation.get("level").set(newLevel);
        
        execute(operation, name, false, "Success: Updated Log Level");
    }
    
    private void execute(ModelNode operation, final String nameEditedOrAdded, final boolean isHandlerOp, final String successMessage) {
        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
                Console.info(successMessage);
                loggingInfo.refreshView(nameEditedOrAdded, isHandlerOp);
            }
        });
    }
}
