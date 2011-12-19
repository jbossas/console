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

package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.util.LoadingOverlay;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/18/11
 */
public class DatasourceStep2 {


    private NewDatasourceWizard wizard;
    private DataSource editedEntity;
    private SingleSelectionModel<JDBCDriver> selectionModel;
    private CellTable<JDBCDriver> table;
    private boolean isStandalone;
    private int selectedTab;

    public DatasourceStep2(NewDatasourceWizard wizard) {
        this.wizard = wizard;
        this.isStandalone = wizard.getBootstrap().isStandalone();
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:10px; vertical-align:center;width:95%");

        HTML desc = new HTML("<h3>"+ Console.CONSTANTS.subsys_jca_dataSource_step2()+"</h3>");
        desc.getElement().setAttribute("style", "padding-bottom:10px;");

        layout.add(desc);
        layout.add(new ContentDescription("Select one of the deployed JDBC driver."));


        // ---

        table = new DefaultCellTable<JDBCDriver>(5);

        TextColumn<JDBCDriver> nameColumn = new TextColumn<JDBCDriver>() {
            @Override
            public String getValue(JDBCDriver record) {
                return record.getName();
            }
        };

        TextColumn<JDBCDriver> groupColumn = new TextColumn<JDBCDriver>() {
            @Override
            public String getValue(JDBCDriver record) {
                return record.getGroup();
            }
        };

        table.addColumn(nameColumn, "Name");

        if(!isStandalone)
            table.addColumn(groupColumn, "Server Group");

        selectionModel = new SingleSelectionModel<JDBCDriver>();
        table.setSelectionModel(selectionModel);

        provisionTable(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);

        VerticalPanel driverPanel = new VerticalPanel();
        driverPanel.add(table);
        driverPanel.add(pager);


        // --

        final Form<JDBCDriver> form = new Form<JDBCDriver>(JDBCDriver.class);
        TextBoxItem name = new TextBoxItem("name", "Name");
        TextBoxItem driverClass = new TextBoxItem("driverClass", "Driver Class");
        NumberBoxItem major = new NumberBoxItem("majorVersion", "Major Version");
        NumberBoxItem minor = new NumberBoxItem("minorVersion", "Minor Version");

        form.setFields(name, driverClass, major, minor);

        // --

        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                selectedTab = event.getSelectedItem();
            }
        });

        tabs.add(driverPanel, "Chose Driver");
        //tabs.add(form.asWidget(), "Specify Driver");

        layout.add(tabs);
        tabs.selectTab(0);


        // ----

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                JDBCDriver driver = null;

                if(selectedTab==0){
                    // reset state
                    form.clearValues();

                    SingleSelectionModel<JDBCDriver> selection =
                            (SingleSelectionModel<JDBCDriver>) table.getSelectionModel();
                    driver = selection.getSelectedObject();
                }
                else
                {
                    FormValidation formValidation = form.validate();
                    if(!formValidation.hasErrors())
                    {
                        driver = form.getUpdatedEntity();
                    }
                }

                if(driver!=null) { // force selected driver
                    editedEntity.setDriverName(driver.getName());
                    editedEntity.setDriverClass(driver.getDriverClass());
                    editedEntity.setMajorVersion(driver.getMajorVersion());
                    editedEntity.setMinorVersion(driver.getMinorVersion());

                    wizard.onConfigureDriver(editedEntity);
                }
                else {
                    Console.warning(Console.CONSTANTS.subsys_jca_dataSource_select_driver(),
                            "If no driver is available you may need to deploy one!");
                }

            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                wizard.getPresenter().closeDialogue();
            }
        };

        DialogueOptions options = new DialogueOptions(
                "Next &rsaquo;&rsaquo;",submitHandler,
                "Cancel",cancelHandler
        );

        return new WindowContentBuilder(layout, options).build();
    }

    private void provisionTable(final CellTable<JDBCDriver> table) {


        /*wizard.getPresenter().loadDriver(new SimpleCallback<List<JDBCDriver>>() {
            @Override
            public void onSuccess(List<JDBCDriver> drivers) {

            }
        });*/

        List<JDBCDriver> drivers = wizard.getDrivers();
        table.setRowCount(drivers.size(), true);
        table.setRowData(drivers);

        // clear selection
        JDBCDriver selectedDriver = selectionModel.getSelectedObject();
        if(selectedDriver!=null)
            selectionModel.setSelected(selectedDriver, false);

        // new default selection
        if(drivers.size()>0) {
            selectionModel.setSelected(drivers.get(0), true);
        }
    }

    void edit(DataSource entity)
    {
        this.editedEntity = entity;

    }

    private CellTable<JDBCDriver> getTable() {
        return table;
    }
}
