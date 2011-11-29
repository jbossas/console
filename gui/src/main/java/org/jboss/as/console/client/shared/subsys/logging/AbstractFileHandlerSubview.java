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

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

/**
 * Main view class for Loggers.
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public abstract class AbstractFileHandlerSubview<T> extends AbstractHandlerSubview implements FrameworkView, LogLevelConsumer, HandlerProducer {

    public AbstractFileHandlerSubview(Class<T> type,
                                 ApplicationMetaData applicationMetaData, 
                                 DispatchAsync dispatcher, 
                                 HandlerListManager handlerListManager) {
        super(type, applicationMetaData, dispatcher, handlerListManager);
    }

    @Override
    protected FormAdapter<T> makeAddEntityForm() {
        Form<T> form = new Form(type);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(), 
                       levelItemForAdd,
                       formMetaData.findAttribute("filePath").getFormItemForAdd(),
                       formMetaData.findAttribute("fileRelativeTo").getFormItemForAdd());
        return form;
    }
}
