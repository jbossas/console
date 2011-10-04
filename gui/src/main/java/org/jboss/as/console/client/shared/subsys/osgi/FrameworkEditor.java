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
package org.jboss.as.console.client.shared.subsys.osgi;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiSubsystem;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author David Bosschaert
 */
public class FrameworkEditor {
    private final OSGiPresenter presenter;
    private Form<OSGiSubsystem> form;
    private FrameworkPropertiesTable propertiesTable;
    private CapabilitiesTable capabilitiesTable;

    FrameworkEditor(OSGiPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        ScrollPanel scroll = new ScrollPanel();
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");
        scroll.add(vpanel);

        // Add an empty toolstrip to make this panel look similar to others
        ToolStrip toolStrip = new ToolStrip();
        layout.add(toolStrip);

        vpanel.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_osgi_frameworkHeader()));
        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_settings()));

        form = new Form<OSGiSubsystem>(OSGiSubsystem.class);
        form.setNumColumns(1);

        CheckBoxItem activationMode = new CheckBoxItem("lazyActivation", Console.CONSTANTS.common_label_lazyActivation());
        activationMode.asWidget().addHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                presenter.onActivationChange(event.getValue());
            }
        }, ValueChangeEvent.getType());
        form.setFields(activationMode);
        vpanel.add(form.asWidget());


        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_osgi_frameworkConfiguration()));
        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        propertiesTable = new FrameworkPropertiesTable(presenter);
        bottomPanel.add(propertiesTable.asWidget(), Console.CONSTANTS.subsys_osgi_properties());

        VerticalPanel panel = new VerticalPanel();
        addCapabilities(panel);
        bottomPanel.add(panel, Console.CONSTANTS.subsys_osgi_capabilities());

        bottomPanel.selectTab(0);
        vpanel.add(bottomPanel);

        layout.add(scroll);
        layout.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 26, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    private void addCapabilities(Panel layout) {
        ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButton(new ToolButton(Console.CONSTANTS.common_label_edit(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                OSGiCapability capability =  capabilitiesTable.getSelection();
                presenter.launchCapabilityWizard(capability);
            }
        }));
        toolStrip.addToolButton(new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final OSGiCapability capability =  capabilitiesTable.getSelection();
                Feedback.confirm(Console.MESSAGES.subsys_osgi_removeCapability(),
                    Console.MESSAGES.subsys_osgi_removeCapabilityConfirm(capability.getIdentifier()),
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed)
                                presenter.onDeleteCapability(capability.getIdentifier());
                        }
                    });
            }
        }));
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchCapabilityWizard(null);
            }
        }));
        layout.add(toolStrip);

        StaticHelpPanel helpPanel = new StaticHelpPanel(Console.MESSAGES.subsys_osgi_capabilitiesHelp());
        layout.add(helpPanel.asWidget());

        capabilitiesTable = new CapabilitiesTable();
        layout.add(capabilitiesTable.asWidget());
    }

    void setProviderDetails(OSGiSubsystem provider) {
        form.edit(provider);
    }

    void updateProperties(List<PropertyRecord> properties) {
        propertiesTable.setProperties(properties);
    }

    void updateCapabilities(List<OSGiCapability> capabilities) {
        capabilitiesTable.setCapabilities(capabilities);
    }
}
