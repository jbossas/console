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
import org.jboss.as.console.client.shared.subsys.logging.model.RootLogger;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkButton;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Main view class for Loggers.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class RootLoggerSubview extends AbstractLoggingSubview<RootLogger>
        implements FrameworkView, LogLevelConsumer, HandlerConsumer {

    private EntityToDmrBridge rootLoggerBridge;

    private EmbeddedHandlerView handlerView;

    public RootLoggerSubview(ApplicationMetaData applicationMetaData, DispatchAsync dispatcher) {
        super(RootLogger.class, applicationMetaData, EnumSet.of(FrameworkButton.ADD, FrameworkButton.REMOVE));
        rootLoggerBridge = new EntityToDmrBridgeImpl<RootLogger>(applicationMetaData, RootLogger.class, this, dispatcher);
    }

    @Override
    public void handlersUpdated(List<String> handlerList) {
        this.handlerView.getListView().setAvailableChoices(handlerList);
    }

    @Override
    public EntityToDmrBridge getEntityBridge() {
        return this.rootLoggerBridge;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_rootLogger();
    }

    @Override
    protected FormAdapter<RootLogger> makeAddEntityForm() {
        return new Form<RootLogger>(beanType);
    }

    @Override
    protected DefaultCellTable<RootLogger> makeEntityTable() {
        DefaultCellTable<RootLogger> table = new DefaultCellTable<RootLogger>(5);
        table.setVisible(false);
        return table;
    }

    @Override
    protected List<SingleEntityView<RootLogger>> provideAdditionalTabs(
            Class<?> beanType,
            FormMetaData formMetaData,
            FrameworkPresenter presenter) {

        List<SingleEntityView<RootLogger>> additionalTabs =
                new ArrayList<SingleEntityView<RootLogger>>();

        this.handlerView = new EmbeddedHandlerView(new FrameworkPresenter() {
            @Override
            public EntityToDmrBridge getEntityBridge() {
                return RootLoggerSubview.this.getEntityBridge();
            }
        });
        additionalTabs.add(handlerView);

        return additionalTabs;
    }
}
