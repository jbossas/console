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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.subsys.logging.model.CustomHandler;
import org.jboss.as.console.client.shared.viewframework.EmbeddedPropertyView;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Subview for Custom Handlers.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CustomHandlerSubview extends AbstractHandlerSubview<CustomHandler>
        implements FrameworkView, LogLevelConsumer, HandlerProducer {

    private EmbeddedPropertyView handlerView;

    public CustomHandlerSubview(ApplicationMetaData applicationMetaData,
                                DispatchAsync dispatcher,
                                HandlerListManager handlerListManager) {
        super(CustomHandler.class, applicationMetaData, dispatcher, handlerListManager);
    }

    @Override
    protected FormAdapter<CustomHandler> makeAddEntityForm() {
        Form<CustomHandler> form = new Form(type);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                levelItemForAdd,
                formMetaData.findAttribute("module").getFormItemForAdd(),
                formMetaData.findAttribute("className").getFormItemForAdd());
        return form;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_customHandlers();
    }


   @Override
    protected List<SingleEntityView<CustomHandler>> provideAdditionalTabs(
            Class<?> beanType,
            FormMetaData formMetaData,
            FrameworkPresenter presenter) {

        List<SingleEntityView<CustomHandler>> additionalTabs =
                new ArrayList<SingleEntityView<CustomHandler>>(1);

        this.handlerView = new EmbeddedPropertyView(new FrameworkPresenter() {
            @Override
            public EntityToDmrBridge getEntityBridge() {
                return CustomHandlerSubview.this.getEntityBridge();
            }
        });
        additionalTabs.add(handlerView);

        return additionalTabs;

    }
}
