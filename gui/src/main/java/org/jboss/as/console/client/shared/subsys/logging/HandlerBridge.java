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

import com.google.gwt.autobean.shared.AutoBean;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

import java.util.List;

/**
 * Adapter for CRUD on logging handlers.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HandlerBridge implements EntityBridge<LoggingHandler> {

    private LoggingPresenter presenter;
    
    public HandlerBridge(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAdd(FormAdapter<LoggingHandler> form) {
        presenter.onAddHandler(form.getUpdatedEntity());
    }

    @Override
    public void onAssignHandler(FormAdapter<LoggingHandler> form) {
        LoggingHandler editedHandler = form.getEditedEntity();
        presenter.onAssignHandlerToHandler(editedHandler.getName(), editedHandler.getType(), form.getUpdatedEntity().getHandlerToAssign());
    }

    @Override
    public void onUnassignHandler(FormAdapter<LoggingHandler> form) {
        LoggingHandler editedHandler = form.getEditedEntity();
        presenter.onUnassignHandlerFromHandler(editedHandler.getName(), editedHandler.getType(), form.getUpdatedEntity().getHandlerToUnassign());
    }
    
    @Override
    public String getName(LoggingHandler handler) {
        return handler.getName();
    }

    @Override
    public void onEdit() {
        presenter.onEditHandler();
    }

    @Override
    public void onRemove(FormAdapter<LoggingHandler> form) {
        presenter.onRemoveHandler(form.getEditedEntity().getName(), form.getEditedEntity().getType());
    }

    @Override
    public void onSaveDetails(FormAdapter<LoggingHandler> form) {
        presenter.onSaveHandlerDetails(form.getEditedEntity().getName(), form.getEditedEntity().getType(), form.getChangedValues());
    }

    @Override
    public AutoBean<LoggingHandler> newEntity() {
        return null;
//        return presenter.getBeanFactory().loggingHandler();
    }

    @Override
    public boolean isAssignHandlerAllowed(LoggingHandler handler) {
        // Subhandlers can only be assigned to async handlers.
        return handler.getType().equals(HandlerType.ASYNC.getDisplayName());
    }

    @Override
    public List<String> getAssignedHandlers(LoggingHandler handler) {
        return handler.getSubhandlers();
    }
    
}
