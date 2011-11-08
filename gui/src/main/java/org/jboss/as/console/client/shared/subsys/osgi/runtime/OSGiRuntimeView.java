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
package org.jboss.as.console.client.shared.subsys.osgi.runtime;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class OSGiRuntimeView extends SuspendableViewImpl implements OSGiRuntimePresenter.MyView {
    private final BundleRuntimeView bundles;
    private final FrameworkRuntimeView framework;

    @Inject
    public OSGiRuntimeView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        framework = new FrameworkRuntimeView(propertyMetaData, dispatcher);
        bundles = new BundleRuntimeView(propertyMetaData, dispatcher);
    }

    @Override
    public Widget createWidget() {
        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(framework.asWidget(), framework.getEntityDisplayName());
        tabLayoutpanel.add(bundles.asWidget(), bundles.getEntityDisplayName());
        tabLayoutpanel.selectTab(1);

        return tabLayoutpanel;
    }

    @Override
    public void initialLoad() {
        ModelNode address = Baseadress.get();
        if (address.asObject().get("profile").isDefined()) {
            // Temporary disabled in domains mode until it is available properly there.
            Feedback.alert(Console.CONSTANTS.subsys_osgi(), "OSGi Subsystem runtime information is not available on the profile level.");
            return;
        }

        bundles.initialLoad();

        // Async to speed up loading
        Console.schedule(new Command() {
            @Override
            public void execute() {
                framework.initialLoad();
            }
        });
    }

    @Override
    public void setPresenter(OSGiRuntimePresenter presenter) {
        bundles.setPresenter(presenter);
    }
}
