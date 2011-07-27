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
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 5/6/11
 */
public class XADatasourceStep2 {

    private NewXADatasourceWizard wizard;
    private SingleSelectionModel<JDBCDriver> selectionModel;
    private XADataSource editedEntity;
    private CellTable<JDBCDriver> table;
    private ComboBox groupSelection;
    private boolean isStandalone;

    public XADatasourceStep2(NewXADatasourceWizard wizard) {
        this.wizard = wizard;
        this.isStandalone = wizard.getBootstrap().isStandalone();
    }

    void edit(XADataSource dataSource) {
        this.editedEntity = dataSource;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h3>Step 2/4: Datasource Class</h3>"));

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

        TextColumn<JDBCDriver> xaClassColumn = new TextColumn<JDBCDriver>() {
            @Override
            public String getValue(JDBCDriver record) {
                return record.getXaDataSourceClass();
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(xaClassColumn, "Datasource Class");

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

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SingleSelectionModel<JDBCDriver> selection =
                        (SingleSelectionModel<JDBCDriver>) table.getSelectionModel();
                JDBCDriver driver = selection.getSelectedObject();

                if(driver!=null) { // force selected driver
                    editedEntity.setDriverName(driver.getName());
                    editedEntity.setDriverClass(driver.getDriverClass());
                    editedEntity.setDataSourceClass(driver.getXaDataSourceClass());
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

        return new WindowContentBuilder(layout,options).build();
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

    private CellTable<JDBCDriver> getTable() {
        return table;
    }
}
