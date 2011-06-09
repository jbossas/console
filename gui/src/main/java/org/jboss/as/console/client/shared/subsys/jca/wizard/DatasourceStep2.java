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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.DefaultPager;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.WindowContentBuilder;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 4/18/11
 */
public class DatasourceStep2 {


    private NewDatasourceWizard wizard;
    private DataSource editedEntity;
    private SingleSelectionModel<JDBCDriver> selectionModel;
    private CellTable<JDBCDriver> table;
    private ComboBox groupSelection;
    private boolean isStandalone;

    public DatasourceStep2(NewDatasourceWizard wizard) {
        this.wizard = wizard;
        this.isStandalone = wizard.getBootstrap().isStandalone();
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:10px; vertical-align:center;width:95%");

        HTML desc = new HTML("<h3>Step 2/3: JDBC Driver</h3>Please chose one of the available drivers.");
        desc.getElement().setAttribute("style", "padding-bottom:10px;");

        layout.add(desc);

        if(!isStandalone)
        {
            groupSelection = new ComboBox();

            Set<String> groupNames = new HashSet<String>(wizard.getDrivers().size());
            for(JDBCDriver driver : wizard.getDrivers())
                groupNames.add(driver.getGroup());
            groupSelection.setValues(groupNames);
            groupSelection.setItemSelected(0, true);

            HorizontalPanel horz = new HorizontalPanel();
            horz.setStyleName("fill-layout-width");
            Label label = new HTML("Server Group"+":&nbsp;");
            label.setStyleName("form-item-title");
            horz.add(label);
            Widget selector = groupSelection.asWidget();
            horz.add(selector);

            label.getElement().getParentElement().setAttribute("align", "right");
            selector.getElement().getParentElement().setAttribute("width", "100%");
            layout.add(horz);

            groupSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    filterTable(event.getValue(), getTable());
                }
            });
        }

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

        // filter and select first record
        if(isStandalone)
            provisionTable(table);
        else
            filterTable(groupSelection.getSelectedValue(), table);

        layout.add(table);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);
        layout.add(pager);

        // ----

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                SingleSelectionModel<JDBCDriver> selection =
                        (SingleSelectionModel<JDBCDriver>) table.getSelectionModel();
                JDBCDriver driver = selection.getSelectedObject();

                if(driver!=null) { // force selected driver
                    editedEntity.setDriverName(driver.getName());
                    editedEntity.setDriverClass(driver.getDriverClass());
                    editedEntity.setMajorVersion(driver.getMajorVersion());
                    editedEntity.setMinorVersion(driver.getMinorVersion());

                    wizard.onConfigureDriver(editedEntity);
                }
                else {
                    Window.alert("Please select a driver!");
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
                "cancel",cancelHandler
        );

        return new WindowContentBuilder(layout, options).build();
    }

     private void provisionTable(CellTable<JDBCDriver> table) {


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

    private void filterTable(String group, CellTable<JDBCDriver> table) {


        List<JDBCDriver> drivers = wizard.getDrivers();
        List<JDBCDriver> filtered = new ArrayList<JDBCDriver>();
        for(JDBCDriver candidate : drivers)
        {
            if(group.equals(candidate.getGroup()))
                filtered.add(candidate);
        }

        table.setRowCount(filtered.size(), true);
        table.setRowData(filtered);

        // clear selection
        JDBCDriver selectedDriver = selectionModel.getSelectedObject();
        if(selectedDriver!=null)
            selectionModel.setSelected(selectedDriver, false);

        // new default selection
        if(filtered.size()>0) {
            selectionModel.setSelected(filtered.get(0), true);
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
