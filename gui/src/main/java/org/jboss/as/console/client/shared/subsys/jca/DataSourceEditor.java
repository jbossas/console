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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.FormEditor;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class DataSourceEditor {

    private DataSourcePresenter presenter;
    private DatasourceTable dataSourceTable;
    private DataSourceDetails details;
    private PoolConfigurationView poolConfig;
    private ConnectionProperties connectionProps ;
    private FormEditor<DataSource> securityEditor;
    private DataSourceValidationEditor validationEditor;
    private DataSourceConnectionEditor connectionEditor;
    private ToolButton disableBtn;

    public DataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDatasourceWizard();
            }
        }));


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final DataSource currentSelection = details.getCurrentSelection();
                if(currentSelection!=null)
                {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("Datasource"),
                            Console.MESSAGES.deleteConfirm("Datasource "+currentSelection.getName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.onDelete(currentSelection);
                                    }
                                }
                            });
                }
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        vpanel.add(new ContentHeaderLabel("JDBC Datasources"));
        vpanel.add(new ContentDescription(Console.CONSTANTS.subsys_jca_dataSources_desc()));

        dataSourceTable = new DatasourceTable();

        vpanel.add(new ContentGroupLabel(Console.MESSAGES.available("Datasources")));
        vpanel.add(topLevelTools.asWidget());
        vpanel.add(dataSourceTable.asWidget());


        // -----------
        details = new DataSourceDetails(presenter);
        details.bind(dataSourceTable.getCellTable());
        SingleSelectionModel<DataSource> selectionModel =
                (SingleSelectionModel<DataSource>)dataSourceTable.getCellTable().getSelectionModel();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler () {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                DataSource selectedObject = ((SingleSelectionModel<DataSource>) dataSourceTable.getCellTable().getSelectionModel()).getSelectedObject();
                presenter.loadPoolConfig(false, selectedObject.getName());
                presenter.onLoadConnectionProperties(selectedObject.getName());

            }
        });

        // -----------------

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        bottomPanel.add(details.asWidget(), "Attributes");

         // -----------------

        final FormToolStrip.FormCallback<DataSource> formCallback = new FormToolStrip.FormCallback<DataSource>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                DataSource ds = getCurrentSelection();
                presenter.onSaveDSDetails(ds.getName(), changeset);
            }

            @Override
            public void onDelete(DataSource entity) {
                // n/a
            }
        };

        connectionEditor = new DataSourceConnectionEditor(presenter, formCallback);
        connectionEditor.getForm().bind(dataSourceTable.getCellTable());
        bottomPanel.add(connectionEditor.asWidget(), "Connection");

        securityEditor = new DataSourceSecurityEditor(formCallback);
        securityEditor.getForm().bind(dataSourceTable.getCellTable());
        bottomPanel.add(securityEditor.asWidget(), "Security");

        // -----------------

        connectionProps = new ConnectionProperties(presenter);
        bottomPanel.add(connectionProps.asWidget(), "Properties");

        // -----------------

        poolConfig = new PoolConfigurationView(new PoolManagement() {
            @Override
            public void onSavePoolConfig(String parentName, Map<String, Object> changeset) {
                presenter.onSavePoolConfig(parentName, changeset, false);
            }

            @Override
            public void onResetPoolConfig(String parentName, PoolConfig entity) {
                presenter.onDeletePoolConfig(parentName, entity, false);
            }

            @Override
            public void onDoFlush(String editedName) {
                if(getCurrentSelection().isEnabled())
                    presenter.onDoFlush(false, editedName);
                else
                    Console.error(Console.CONSTANTS.subsys_jca_error_datasource_notenabled());
            }
        });

        bottomPanel.add(poolConfig.asWidget(), "Pool");


        // ----

        validationEditor = new DataSourceValidationEditor(formCallback);
        validationEditor.getForm().bind(dataSourceTable.getCellTable());
        bottomPanel.add(validationEditor.asWidget(), "Validation");

        bottomPanel.selectTab(0);

        // -----------------

        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_selection()));

        // --
        ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final DataSource selection = getCurrentSelection();
                final boolean nextState = !selection.isEnabled();
                Feedback.confirm(Console.MESSAGES.modify("datasource"),
                        Console.MESSAGES.modifyConfirm("Datasource "+selection.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisable(selection, nextState);
                                }
                            }
                        });
            }
        };

        disableBtn = new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), disableHandler);
        disableBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_enOrDisable_dataSourceDetails());

        dataSourceTable.getCellTable().getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                String nextState = getCurrentSelection().isEnabled() ? Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                disableBtn.setText(nextState);
            }
        }) ;


        topLevelTools.addToolButtonRight(disableBtn);


        // --

        vpanel.add(bottomPanel);

        return layout;
    }


    private DataSource getCurrentSelection() {
        DataSource ds = ((SingleSelectionModel<DataSource>) dataSourceTable.getCellTable().getSelectionModel()).getSelectedObject();
        return ds;
    }

    public void updateDataSources(List<DataSource> datasources) {

        dataSourceTable.getDataProvider().setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getCellTable().getSelectionModel().setSelected(datasources.get(0), true);

    }

    public void setEnabled(boolean isEnabled) {


    }

    public void enableDetails(boolean b) {
        details.setEnabled(b);
    }

    public void setPoolConfig(String name, PoolConfig poolConfig) {
        this.poolConfig.updateFrom(name, poolConfig);
    }

    public void setConnectionProperties(String reference, List<PropertyRecord> properties) {
        connectionProps.setProperties(reference, properties);
    }
}
