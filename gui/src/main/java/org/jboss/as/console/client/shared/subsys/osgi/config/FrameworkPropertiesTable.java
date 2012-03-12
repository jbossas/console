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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.osgi.config.wizard.NewPropertyWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Bosschaert
 */
public class FrameworkPropertiesTable implements PropertyManagement {
    private final OSGiConfigurationPresenter presenter;
    private List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
    private DefaultWindow propertyWindow;
    private PropertyEditor propEditor;

    FrameworkPropertiesTable(OSGiConfigurationPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
      propEditor = new PropertyEditor(this, true, 10);
      propEditor.setHelpText(Console.MESSAGES.subsys_osgi_frameworkPropertiesHelp());

      Widget widget = propEditor.asWidget();
      propEditor.setAllowEditProps(false);
      return widget;
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        presenter.onAddProperty(prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onDeleteProperty(prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        presenter.onChangeProperty(prop);
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.CONSTANTS.subsys_osgi_frameworkPropertyAdd());
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.trapWidget(new NewPropertyWizard(this, reference).asWidget());
        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void setProperties(List<PropertyRecord> propList) {
        properties = propList;
        propEditor.setProperties("", properties);
    }
}
