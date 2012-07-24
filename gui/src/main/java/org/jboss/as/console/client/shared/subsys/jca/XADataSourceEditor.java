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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class XADataSourceEditor implements PropertyManagement {

    private DataSourcePresenter presenter;
    private DefaultCellTable<XADataSource> dataSourceTable;
    private ListDataProvider<XADataSource> dataSourceProvider;
    private XADataSourceDetails details;
    private PropertyEditor propertyEditor;
    private PoolConfigurationView poolConfig;
    private XADataSourceConnection connectionEditor;
    private DataSourceSecurityEditor securityEditor;
    private DataSourceValidationEditor validationEditor;
    private ToolButton disableBtn;

    public XADataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        ToolButton commonLabelAddBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewXADatasourceWizard();
            }
        });
        commonLabelAddBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_xADataSourceEditor());
        topLevelTools.addToolButtonRight(commonLabelAddBtn);


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final XADataSource currentSelection = details.getCurrentSelection();
                if(currentSelection!=null)
                {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("XA Datasource"),
                            Console.MESSAGES.deleteConfirm("XA Datasource "+currentSelection.getName()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if (isConfirmed) {
                                        presenter.onDeleteXA(currentSelection);
                                    }
                                }
                            });
                }
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_delete_xADataSourceEditor());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(scroll, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        vpanel.add(new ContentHeaderLabel("JDBC XA Datasources"));

        vpanel.add(new ContentDescription(Console.CONSTANTS.subsys_jca_xadataSources_desc()));

        dataSourceTable = new DefaultCellTable<XADataSource>(8,
                new ProvidesKey<XADataSource>() {
                    @Override
                    public Object getKey(XADataSource item) {
                        return item.getJndiName();
                    }
                });

        dataSourceProvider = new ListDataProvider<XADataSource>();
        dataSourceProvider.addDataDisplay(dataSourceTable);


        TextColumn<DataSource> nameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getName();
            }
        };

        TextColumn<DataSource> jndiNameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getJndiName();
            }
        };

        Column<DataSource, ImageResource> statusColumn =
                new Column<DataSource, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(DataSource dataSource) {

                        ImageResource res = null;

                        if(dataSource.isEnabled())
                            res = Icons.INSTANCE.status_good();
                        else
                            res = Icons.INSTANCE.status_bad();

                        return res;
                    }
                };


        dataSourceTable.addColumn(nameColumn, "Name");
        dataSourceTable.addColumn(jndiNameColumn, "JNDI");
        dataSourceTable.addColumn(statusColumn, "Enabled?");

        vpanel.add(new ContentGroupLabel(Console.MESSAGES.available("XA Datasources")));
        vpanel.add(topLevelTools.asWidget());
        vpanel.add(dataSourceTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(dataSourceTable);
        vpanel.add(pager);

        // -----------
        details = new XADataSourceDetails(presenter);


        propertyEditor = new PropertyEditor(this, true);
        propertyEditor.setHelpText(Console.CONSTANTS.subsys_jca_dataSource_xaprop_help());

        final SingleSelectionModel<XADataSource> selectionModel = new SingleSelectionModel<XADataSource>();
        selectionModel.addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
                    @Override
                    public void onSelectionChange(SelectionChangeEvent event) {
                        XADataSource dataSource = selectionModel.getSelectedObject();
                        String nextState = dataSource.isEnabled() ? Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                        disableBtn.setText(nextState);

                        presenter.loadXAProperties(dataSource.getName());
                        presenter.loadPoolConfig(true, dataSource.getName());
                    }
                });
        dataSourceTable.setSelectionModel(selectionModel);


        ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final XADataSource selection = getCurrentSelection();
                final boolean doEnable = !selection.isEnabled();
                Feedback.confirm(Console.MESSAGES.modify("XA datasource"), Console.MESSAGES.modifyConfirm("XA datasource " + selection.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisableXA(selection, doEnable);
                                }
                            }
                        });
            }
        };

        disableBtn = new ToolButton(Console.CONSTANTS.common_label_enOrDisable());
        disableBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_enOrDisable_xADataSourceDetails());
        disableBtn.addClickHandler(disableHandler);
        topLevelTools.addToolButtonRight(disableBtn);

        // -----

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");
        bottomPanel.add(details.asWidget(), "Attributes");
        details.getForm().bind(dataSourceTable);

        final FormToolStrip.FormCallback<XADataSource> xaCallback = new FormToolStrip.FormCallback<XADataSource>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                DataSource ds = getCurrentSelection();
                presenter.onSaveXADetails(ds.getName(), changeset);
            }

            @Override
            public void onDelete(XADataSource entity) {
                // n/a
            }
        };

        final FormToolStrip.FormCallback<DataSource> dsCallback = new FormToolStrip.FormCallback<DataSource>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                DataSource ds = getCurrentSelection();
                presenter.onSaveXADetails(ds.getName(), changeset);
            }

            @Override
            public void onDelete(DataSource entity) {
                // n/a
            }
        };

        connectionEditor = new XADataSourceConnection(presenter, xaCallback);
        connectionEditor.getForm().bind(dataSourceTable);
        bottomPanel.add(connectionEditor.asWidget(), "Connection");

        securityEditor = new DataSourceSecurityEditor(dsCallback);
        securityEditor.getForm().bind(dataSourceTable);
        bottomPanel.add(securityEditor.asWidget(), "Security");

        bottomPanel.add(propertyEditor.asWidget(), "Properties");
        propertyEditor.setAllowEditProps(false);

        poolConfig = new PoolConfigurationView(new PoolManagement() {
            @Override
            public void onSavePoolConfig(String parentName, Map<String, Object> changeset) {
                presenter.onSavePoolConfig(parentName, changeset, true);
            }

            @Override
            public void onResetPoolConfig(String parentName, PoolConfig entity) {
                presenter.onDeletePoolConfig(parentName, entity, true);
            }

            @Override
            public void onDoFlush(String editedName) {
                presenter.onDoFlush(true, editedName);
            }
        });
        bottomPanel.add(poolConfig.asWidget(), "Pool");
        poolConfig.getForm().bind(dataSourceTable);

        validationEditor = new DataSourceValidationEditor(dsCallback);
        validationEditor.getForm().bind(dataSourceTable);
        bottomPanel.add(validationEditor.asWidget(), "Validation");

        bottomPanel.selectTab(0);
        vpanel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_selection()));
        vpanel.add(bottomPanel);
        return layout;
    }


    public void updateDataSources(List<XADataSource> datasources) {

        // requires manual cleanup
        propertyEditor.clearValues();

        dataSourceProvider.setList(datasources);

        dataSourceTable.selectDefaultEntity();

    }

    public void setEnabled(boolean isEnabled) {
        details.setEnabled(isEnabled);
    }

    private XADataSource getCurrentSelection() {
        return ((SingleSelectionModel<XADataSource>)dataSourceTable.getSelectionModel()).getSelectedObject();
    }

    // property management below

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        presenter.onCreateXAProperty(reference, prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onDeleteXAProperty(reference, prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        presenter.launchNewXAPropertyDialoge(reference);
    }

    @Override
    public void closePropertyDialoge() {
        presenter.closeXAPropertyDialoge();
    }

    public void enableDetails(boolean b) {
        details.setEnabled(b);
    }

    public void setPoolConfig(String name, PoolConfig poolConfig) {
        this.poolConfig.updateFrom(name, poolConfig);
    }

    public void setXaProperties(String dataSourceName, List<PropertyRecord> result) {
        propertyEditor.setProperties(dataSourceName, result);
    }
}
