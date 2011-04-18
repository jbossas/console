/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.ButtonItem;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.PasswordBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class DataSourceEditor {

    private DataSourcePresenter presenter;
    private DefaultCellTable<DataSource> dataSourceTable;
    private ListDataProvider<DataSource> dataSourceProvider;

    public DataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("New DataSource", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDatasourceWizard();
            }
        }));

        layout.add(topLevelTools);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "margin:15px; width:95%");

        layout.add(vpanel);

        layout.setWidgetTopHeight(topLevelTools, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(vpanel, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.database());
        horzPanel.add(image);
        horzPanel.add(new ContentHeaderLabel("JDBC Data Source Configurations"));
        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);


        dataSourceTable = new DefaultCellTable<DataSource>(20);
        dataSourceProvider = new ListDataProvider<DataSource>();
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

        vpanel.add(dataSourceTable);


        // -----------

        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");

        detailPanel.add(new ContentGroupLabel("Details"));

        final Form<DataSource> form = new Form(DataSource.class);
        form.setNumColumns(2);

        ToolStrip detailToolStrip = new ToolStrip();
        detailToolStrip.addToolButton(
                new ToolButton("Edit",
                        new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {

                            }
                        }
                )
        );


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                DataSource currentSelection = getCurrentSelection();

                Feedback.confirm(
                        "Delete DataSource",
                        "Really delete this DataSource '"+currentSelection.getName()+"' ?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDelete(getCurrentSelection());
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton("Delete");
        deleteBtn.addClickHandler(clickHandler);
        detailToolStrip.addToolButton(deleteBtn);


         ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                String state = form.getEditedEntity().isEnabled() ? "Disable" : "Enable";
                final boolean nextState = !form.getEditedEntity().isEnabled();
                Feedback.confirm(state + " datasource", "Do you want to " + state + " this DataSource?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisable(form.getEditedEntity(), nextState);
                                }
                            }
                        });
            }
        };

        ToolButton enableBtn = new ToolButton("En/Disable");
        enableBtn.addClickHandler(disableHandler);
        detailToolStrip.addToolButtonRight(enableBtn);

        detailPanel.add(detailToolStrip);

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");
        CheckBoxItem enabledFlagItem = new CheckBoxItem("enabled", "Is enabled?");
        TextBoxItem driverItem = new TextBoxItem("driverName", "Driver");
        TextBoxItem driverClassItem = new TextBoxItem("driverClass", "Driver Class");

        TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");

        TextBoxItem userItem = new TextBoxItem("username", "Username");
        PasswordBoxItem passwordItem = new PasswordBoxItem("password", "Password");

        form.setFields(nameItem, jndiItem, enabledFlagItem);
        form.setFieldsInGroup("Connection", new DefaultGroupRenderer(), userItem, passwordItem, urlItem);
        form.setFieldsInGroup("Driver", new DisclosureGroupRenderer(), driverItem, driverClassItem);
        form.bind(dataSourceTable);
        form.setEnabled(false); // currently not editable

        Widget formWidget = form.asWidget();

        detailPanel.add(formWidget);

        vpanel.add(detailPanel);

        return layout;
    }

    public void updateDataSources(List<DataSource> datasources) {
        dataSourceProvider.setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getSelectionModel().setSelected(datasources.get(0), true);

    }

    public DataSource getCurrentSelection() {
        SingleSelectionModel<DataSource> selectionModel = (SingleSelectionModel<DataSource>)dataSourceTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }
}
