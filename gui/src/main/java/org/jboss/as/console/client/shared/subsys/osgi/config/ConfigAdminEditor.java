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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.general.InputWindow;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.config.wizard.NewPropertyWizard;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author David Bosschaert
 */
public class ConfigAdminEditor implements PropertyManagement {
    private final OSGiConfigurationPresenter presenter;
    private PIDTable pidTable;
    private PropertyEditor propertyEditor;
    private DefaultWindow dialog;

    ConfigAdminEditor(OSGiConfigurationPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButton(new ToolButton(Console.CONSTANTS.common_label_edit(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final OSGiConfigAdminData model = pidTable.getSelection();

                dialog = new DefaultWindow(Console.CONSTANTS.subsys_osgi_configAdminEditPID());
                dialog.setWidth(320);
                dialog.setHeight(140);
                dialog.setWidget(new InputWindow(model.getPid(), new InputWindow.Result() {
                    @Override
                    public void result(String value) {
                        if (value != null && !value.equals(model.getPid())) {
                            presenter.onDeleteConfigurationAdminData(model.getPid());
                            model.setPid(value);
                            presenter.onAddConfigurationAdminData(model);
                        }
                        closePropertyDialoge();
                    }
                }).asWidget());
                dialog.setGlassEnabled(true);
                dialog.center();
            }
        }));
        topLevelTools.addToolButton(new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final OSGiConfigAdminData model = pidTable.getSelection();
                Feedback.confirm(Console.MESSAGES.subsys_osgi_removeConfigAdmin(), Console.MESSAGES.subsys_osgi_removeConfigAdminConfirm(model.getPid()),
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean isConfirmed) {
                            if (isConfirmed)
                                presenter.onDeleteConfigurationAdminData(model.getPid());
                        }
                    });
            }
        }));
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewCASPropertyWizard();
            }
        }));
        layout.add(topLevelTools);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.add(new ContentHeaderLabel(Console.CONSTANTS.subsys_osgi_configAdminHeader()));
        vpanel.add(horzPanel);

        pidTable = new PIDTable();

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_osgi_configAdminPIDLabel()));
        vpanel.add(pidTable.asWidget());

        propertyEditor = new PropertyEditor(this, true, 10);
        final SingleSelectionModel<OSGiConfigAdminData> selectionModel = new SingleSelectionModel<OSGiConfigAdminData>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                OSGiConfigAdminData pid = selectionModel.getSelectedObject();
                propertyEditor.setProperties(pid.getPid(), pid.getProperties());
            }
        });
        pidTable.setSelectionModel(selectionModel);

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.subsys_osgi_configAdminValuesLabel()));
        vpanel.add(propertyEditor.asWidget());
        propertyEditor.setAllowEditProps(false);

        return layout;
    }

    private OSGiConfigAdminData findData(String pid) {
        for (OSGiConfigAdminData data : pidTable.getData()) {
            if (pid.equals(data.getPid())) {
                return data;
            }
        }
        return null;
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        dialog.hide();
        OSGiConfigAdminData data = findData(reference);
        data.getProperties().add(prop);
        presenter.onDeleteConfigurationAdminData(data.getPid());
        presenter.onAddConfigurationAdminData(data);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        OSGiConfigAdminData data = findData(reference);
        data.getProperties().remove(prop);
        presenter.onDeleteConfigurationAdminData(data.getPid());
        presenter.onAddConfigurationAdminData(data);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }
    
    @Override
    public void launchNewPropertyDialoge(String reference) {
        dialog = new DefaultWindow(Console.CONSTANTS.subsys_osgi_configAdminValueAdd());
        dialog.setWidth(320);
        dialog.setHeight(240);
        dialog.setWidget(new NewPropertyWizard(this, reference).asWidget());
        dialog.setGlassEnabled(true);
        dialog.center();
    }

    @Override
    public void closePropertyDialoge() {
        dialog.hide();
    }

    void update(List<OSGiConfigAdminData> casDataList, String selectPid) {
        pidTable.setData(casDataList, selectPid);
    }
}
