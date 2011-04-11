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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.ButtonItem;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
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
        layout.setStyleName("fill-layout");

        ToolStrip toolstrip = new ToolStrip();
        toolstrip.addToolButton(new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Adding datasources not implemented",Message.Severity.Warning)
                );
            }
        }));

        layout.add(toolstrip);

        // ----

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "margin:15px; width:95%");

        layout.add(vpanel);

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
        dataSourceTable.addColumn(statusColumn, "Enabled?");

        vpanel.add(dataSourceTable);


        // -----------

        final Form<DataSource> form = new Form(DataSource.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");

        CheckBoxItem enabledFlagItem = new CheckBoxItem("enabled", "Is enabled?");

        final ButtonItem enableItem = new ButtonItem("enabled", "Action") {
            @Override
            public void setValue(Boolean b) {

                if(b)
                    button.setText("Disable");
                else
                    button.setText("Enable");

            }
        };

        enableItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                String state = form.getEditedEntity().isEnabled() ? "Disable" : "Enable";
                Feedback.confirm(state + " datasource", "Do you want to " + state + " this datasource?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {

                                }
                            }
                        });
            }
        });

        TextBoxItem driverItem = new TextBoxItem("driverClass", "Driver");
        TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");

        TextBoxItem userItem = new TextBoxItem("username", "Username");
        PasswordBoxItem passwordItem = new PasswordBoxItem("password", "Password");

        form.setFields(nameItem, enableItem, jndiItem, driverItem, urlItem, userItem, passwordItem);
        form.bind(dataSourceTable);
        form.setEnabled(false); // currently not editable

        Widget formWidget = form.asWidget();

        vpanel.add(new ContentGroupLabel("Details"));
        vpanel.add(formWidget);


        layout.setWidgetTopHeight(toolstrip, 0, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(vpanel, 30, Style.Unit.PX, 100, Style.Unit.PCT);
        return layout;
    }

    public void updateDataSources(List<DataSource> datasources) {
        dataSourceProvider.setList(datasources);

        if(!datasources.isEmpty())
            dataSourceTable.getSelectionModel().setSelected(datasources.get(0), true);

    }
}
