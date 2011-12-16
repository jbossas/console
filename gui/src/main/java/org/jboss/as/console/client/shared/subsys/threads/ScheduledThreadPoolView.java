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
package org.jboss.as.console.client.shared.subsys.threads;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.threads.model.ScheduledThreadPool;
import org.jboss.as.console.client.shared.viewframework.EmbeddedPropertyView;
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
 * Main view class for Scheduled Thread Pools
 *
 * @author Stan Silvert
 */
public class ScheduledThreadPoolView extends AbstractThreadPoolView<ScheduledThreadPool> implements FrameworkView {

    private FrameworkPresenter presenter;
    private EmbeddedPropertyView propertyView;

    public ScheduledThreadPoolView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(ScheduledThreadPool.class, propertyMetaData, dispatcher);
    }

    @Override
    protected String getEntityDisplayName() {
        return "Scheduled Pools";
    }

    @Override
    protected FormAdapter<ScheduledThreadPool> makeAddEntityForm() {
        Form<ScheduledThreadPool> form = new Form(ScheduledThreadPool.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                formMetaData.findAttribute("keepaliveTimeout").getFormItemForAdd(),
                formMetaData.findAttribute("keepaliveTimeoutUnit").getFormItemForAdd(),
                formMetaData.findAttribute("maxThreadsCount").getFormItemForAdd(),
                formMetaData.findAttribute("maxThreadsPerCPU").getFormItemForAdd());
        return form;
    }

    @Override
    protected List<SingleEntityView<ScheduledThreadPool>> provideAdditionalTabs(
            Class<?> beanType,
            FormMetaData formMetaData,
            FrameworkPresenter presenter) {

        this.presenter = presenter;

        List<SingleEntityView<ScheduledThreadPool>> additionalTabs = new ArrayList<SingleEntityView<ScheduledThreadPool>>();
        propertyView = new EmbeddedPropertyView(presenter);
        additionalTabs.add(propertyView);

        return additionalTabs;
    }

}
