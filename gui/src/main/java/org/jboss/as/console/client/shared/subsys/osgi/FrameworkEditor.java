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
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiPreloadedModule;
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
    private PreloadedModulesTable preloadedModulesTable;

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

        vpanel.add(new ContentHeaderLabel("OSGi Framework Configuration"));
        vpanel.add(new ContentGroupLabel("Settings"));

        form = new Form<OSGiSubsystem>(OSGiSubsystem.class);
        form.setNumColumns(1);

        CheckBoxItem activationMode = new CheckBoxItem("lazyActivation", "Lazy Activation");
        form.setFields(activationMode);
        vpanel.add(form.asWidget());

        addProperties(vpanel);
        addPreLoadedModules(vpanel);

        layout.add(scroll);
        layout.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 26, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    private void addProperties(Panel layout) {
        layout.add(new ContentGroupLabel("Framework Properties"));

        propertiesTable = new FrameworkPropertiesTable(presenter);
        layout.add(propertiesTable.asWidget());
    }

    private void addPreLoadedModules(Panel layout) {
        layout.add(new ContentGroupLabel("Pre-loaded Modules"));
        ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButton(new ToolButton(Console.CONSTANTS.common_label_edit(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                OSGiPreloadedModule module =  preloadedModulesTable.getSelection();
                presenter.launchModuleWizard(module);
            }
        }));
        toolStrip.addToolButton(new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final OSGiPreloadedModule module =  preloadedModulesTable.getSelection();
                Feedback.confirm("Remove Pre-Loaded Module", "Remove from pre-loaded modules: " + module.getIdentifier() + "?",
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed)
                                presenter.onDeletePreloadedModule(module.getIdentifier());
                        }
                    });
            }
        }));
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchModuleWizard(null);
            }
        }));
        layout.add(toolStrip);

        preloadedModulesTable = new PreloadedModulesTable();
        layout.add(preloadedModulesTable.asWidget());
    }

    void setProviderDetails(OSGiSubsystem provider) {
        form.edit(provider);
        form.setEnabled(false);
    }

    void updateProperties(List<PropertyRecord> properties) {
        propertiesTable.setProperties(properties);
    }

    void updatePreloadedModules(List<OSGiPreloadedModule> modules) {
        preloadedModulesTable.setModules(modules);
    }
}
