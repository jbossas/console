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
package org.jboss.as.console.client.shared.subsys.ejb3;

import java.util.List;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;

/**
 * @author David Bosschaert
 */
public class ServicesView extends SuspendableViewImpl {
    private final AsyncServiceView asyncServiceView;
    private final TimerServiceView timerServiceView;
    private final RemoteServiceView remoteServiceView;

    public ServicesView(PropertyMetaData propertyMetaData, DispatchAsync dispatcher) {
        asyncServiceView = new AsyncServiceView(propertyMetaData, dispatcher);
        timerServiceView = new TimerServiceView(propertyMetaData, dispatcher);
        remoteServiceView = new RemoteServiceView(propertyMetaData, dispatcher);
    }

    @Override
    public Widget createWidget() {
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");
        vpanel.add(new ContentGroupLabel("EJB Services"));

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        bottomPanel.add(timerServiceView.asWidget(), timerServiceView.getEntityDisplayName());
        bottomPanel.add(asyncServiceView.asWidget(), asyncServiceView.getEntityDisplayName());
        bottomPanel.add(remoteServiceView.asWidget(), remoteServiceView.getEntityDisplayName());
        bottomPanel.selectTab(0);

        vpanel.add(bottomPanel);

        return vpanel;
    }

    public void initialLoad() {
        asyncServiceView.initialLoad();
        timerServiceView.initialLoad();
        remoteServiceView.initialLoad();
    }

    public void setThreadPoolNames(List<String> threadPoolNames) {
        asyncServiceView.setThreadPoolNames(threadPoolNames);
        timerServiceView.setThreadPoolNames(threadPoolNames);
        remoteServiceView.setThreadPoolNames(threadPoolNames);
    }
}
