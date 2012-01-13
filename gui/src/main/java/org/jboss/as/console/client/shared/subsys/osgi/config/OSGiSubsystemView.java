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
package org.jboss.as.console.client.shared.subsys.osgi.config;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiSubsystem;

/**
 * @author David Bosschaert
 */
public class OSGiSubsystemView extends SuspendableViewImpl implements OSGiConfigurationPresenter.MyView {

    private OSGiConfigurationPresenter presenter;
    private FrameworkEditor frameworkEditor;

    @Override
    public Widget createWidget() {
        frameworkEditor = new FrameworkEditor(presenter);

        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(frameworkEditor.asWidget(), Console.CONSTANTS.subsys_osgi_framework());
        tabLayoutPanel.selectTab(0);

        return tabLayoutPanel;
    }

    @Override
    public void setPresenter(OSGiConfigurationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProviderDetails(OSGiSubsystem provider) {
        frameworkEditor.setProviderDetails(provider);
    }

    @Override
    public void updateProperties(List<PropertyRecord> properties) {
        frameworkEditor.updateProperties(properties);
    }

    @Override
    public void updateCapabilities(List<OSGiCapability> capabilities) {
        frameworkEditor.updateCapabilities(capabilities);
    }
}
