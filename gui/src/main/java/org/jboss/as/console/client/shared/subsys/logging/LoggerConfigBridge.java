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
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

import java.util.List;

/**
 * Adapter for CRUD on LoggerConfig
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggerConfigBridge implements EntityBridge<LoggerConfig> {

    private LoggingPresenter presenter;
    
    public LoggerConfigBridge(LoggingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAdd(FormAdapter<LoggerConfig> form) {
        String name = form.getUpdatedEntity().getName();
        String level = form.getUpdatedEntity().getLevel();
        presenter.onAddLogger(name, level);
    }

    @Override
    public void onAssignHandler(FormAdapter<LoggerConfig> form) {
        presenter.onAssignHandlerToLogger(form.getEditedEntity().getName(), form.getUpdatedEntity().getHandlerToAssign());
    }

    @Override
    public void onUnassignHandler(FormAdapter<LoggerConfig> form) {
        presenter.onUnassignHandlerFromLogger(form.getEditedEntity().getName(), form.getUpdatedEntity().getHandlerToUnassign());
    }
    
    @Override
    public String getName(LoggerConfig logger) {
        return logger.getName();
    }

    @Override
    public void onEdit() {
        presenter.onEditLogger();
    }

    @Override
    public void onRemove(FormAdapter<LoggerConfig> form) {
        presenter.onRemoveLogger(form.getEditedEntity().getName());
    }

    @Override
    public void onSaveDetails(FormAdapter<LoggerConfig> form) {
        presenter.onSaveLoggerDetails(form.getEditedEntity().getName(), form.getChangedValues());
    }

    @Override
    public AutoBean<LoggerConfig> newEntity() {
        return null;
//        return presenter.getBeanFactory().loggerConfig();
    }

    @Override
    public boolean isAssignHandlerAllowed(LoggerConfig logger) {
        return true;
    }

    @Override
    public List<String> getAssignedHandlers(LoggerConfig logger) {
        return logger.getHandlers();
    }
}
