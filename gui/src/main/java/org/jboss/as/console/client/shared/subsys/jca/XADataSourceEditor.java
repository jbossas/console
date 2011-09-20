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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;

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

    public XADataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.subsys_jca_newDataSource(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewXADatasourceWizard();
            }
        }));


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final XADataSource currentSelection = details.getCurrentSelection();
                if(currentSelection!=null)
                {
                    Feedback.confirm(
                            "Delete DataSource",
                            "Really delete this DataSource '" + currentSelection.getName() + "' ?",
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
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(vpanel);
        layout.add(scroll);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.database());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("XA Datasource Configurations"));
        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);


        dataSourceTable = new DefaultCellTable<XADataSource>(20);
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

        TextColumn<DataSource> poolColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getPoolName();
            }
        };

        Column<DataSource, ImageResource> statusColumn =
                new Column<DataSource, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(DataSource dataSource) {

                        ImageResource res = null;

                        if(dataSource.isEnabled())
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };


        dataSourceTable.addColumn(nameColumn, "Name");
        dataSourceTable.addColumn(jndiNameColumn, "JNDI");
        dataSourceTable.addColumn(poolColumn, "Pool");
        dataSourceTable.addColumn(statusColumn, "Enabled?");

        vpanel.add(new ContentGroupLabel("Registered XA Datasources"));
        vpanel.add(dataSourceTable);


        // -----------
        details = new XADataSourceDetails(presenter);
        propertyEditor = new PropertyEditor(this,true);
        propertyEditor.setHelpText("Properties to assign to the XADataSource implementation class.");

        final SingleSelectionModel<XADataSource> selectionModel = new SingleSelectionModel<XADataSource>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                XADataSource dataSource = selectionModel.getSelectedObject();
                details.setSelectedRecord(dataSource);
                propertyEditor.setProperties(dataSource.getName(), dataSource.getProperties());
            }
        });
        dataSourceTable.setSelectionModel(selectionModel);


        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler () {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                XADataSource selectedObject = ((SingleSelectionModel<XADataSource>) dataSourceTable.getSelectionModel()).getSelectedObject();
                presenter.loadPoolConfig(true, selectedObject.getName());
            }
        });

        TabPanel bottomPanel = new TabPanel();
        bottomPanel.setStyleName("default-tabpanel");

        bottomPanel.add(details.asWidget(), "Attributes");
        bottomPanel.add(propertyEditor.asWidget(), "XA Properties");
        propertyEditor.setAllowEditProps(false); // TODO: modifications of XA properties

        poolConfig = new PoolConfigurationView(presenter, true);
        bottomPanel.add(poolConfig.asWidget(), "Pool");

        bottomPanel.selectTab(0);
        vpanel.add(new ContentGroupLabel("Datasource"));
        vpanel.add(bottomPanel);
        return layout;
    }


    public void updateDataSources(List<XADataSource> datasources) {
        dataSourceProvider.setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getSelectionModel().setSelected(datasources.get(0), true);

    }

    public void setEnabled(boolean isEnabled) {
        details.setEnabled(isEnabled);
    }


    // property management below
    // TODO: https://issues.jboss.org/browse/AS7-874

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        Console.error("Not implemented yet!");
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        Console.error("Not implemented yet!");
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        Console.error("Not implemented yet!");    // https://issues.jboss.org/browse/AS7-874
    }

    @Override
    public void closePropertyDialoge() {

    }

    public void enableDetails(boolean b) {
        details.setEnabled(b);
    }

    public void setPoolConfig(String name, PoolConfig poolConfig) {
        this.poolConfig.updateFrom(name, poolConfig);
    }
}
