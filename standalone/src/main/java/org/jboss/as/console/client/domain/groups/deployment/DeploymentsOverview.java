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

package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.RHSHeader;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DeploymentsOverview extends SuspendableViewImpl implements DeploymentsPresenter.MyView {

    private DeploymentsPresenter presenter;
    private ListDataProvider<DeploymentRecord> deploymentProvider;
    private DefaultCellTable<DeploymentRecord> deploymentTable;

    private ComboBox groupFilter;
    private Form<DeploymentRecord> form;

    @Override
    public void setPresenter(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        RHSHeader title = new RHSHeader("Current Deployments");
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

        // --

        final ToolStrip topLevelTools = new ToolStrip();
        final ToolButton newButton= new ToolButton("New Deployment");
        newButton.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.launchNewDeploymentDialoge();
            }
        });

        topLevelTools.addToolButtonRight(newButton);
        layout.add(topLevelTools);
        layout.setWidgetTopHeight(topLevelTools, 28, Style.Unit.PX, 30, Style.Unit.PX);

        // --
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        // -----------

        ContentHeaderLabel nameLabel = new ContentHeaderLabel("Available Deployments");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.deployment());
        horzPanel.add(image);
        image.getElement().getParentElement().setAttribute("width", "25");

        horzPanel.add(nameLabel);

        vpanel.add(horzPanel);

        deploymentTable = new DefaultCellTable<DeploymentRecord>(20);
        deploymentProvider = new ListDataProvider<DeploymentRecord>();
        deploymentProvider.addDataDisplay(deploymentTable);

        TextColumn<DeploymentRecord> dplNameColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        };

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        };

        TextColumn<DeploymentRecord> groupColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getServerGroup();
            }
        };


        deploymentTable.addColumn(dplNameColumn, "Name");
        deploymentTable.addColumn(dplRuntimeColumn, "Runtime Name");
        deploymentTable.addColumn(groupColumn, "Server Group");

        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");

        groupFilter = new ComboBox();
        groupFilter.setValues(Arrays.asList(new String[] {"war", "ear", "rar", "other"}));
        groupFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterGroup(event.getValue());
            }
        });
        Widget groupFilterWidget = groupFilter.asWidget();
        groupFilterWidget.getElement().setAttribute("style", "width:200px;");


        tableOptions.add(new Label("Server Group:"));
        tableOptions.add(groupFilterWidget);


        ComboBox typeFilter = new ComboBox();
        typeFilter.setValues(Arrays.asList(new String[]{"", "war", "ear", "rar", "other"}));
        typeFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterType(event.getValue());
            }
        });


        Widget filterWidget = typeFilter.asWidget();
        filterWidget.getElement().setAttribute("style", "width:60px;");

        tableOptions.add(new Label("Type:"));
        tableOptions.add(filterWidget);

        tableOptions.getElement().setAttribute("style", "float:right;");
        vpanel.add(tableOptions);
        vpanel.add(deploymentTable);


        ScrollPanel scroll = new ScrollPanel();
        scroll.add(vpanel);

        layout.add(scroll);
        layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 65, Style.Unit.PCT);

        // ----------- --------------------------------------------------


        LayoutPanel formPanel = new LayoutPanel();
        formPanel.getElement().setAttribute("style", "padding:15px;");

        final ToolStrip toolStrip = new ToolStrip();
        final ToolButton edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {

                }
                else
                {

                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Deployment",
                        "Do you want to delete this deployment?",
                        new Feedback.ConfirmationHandler()
                        {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                {
                                    SingleSelectionModel<DeploymentRecord> selectionModel = (SingleSelectionModel) deploymentTable.getSelectionModel();
                                    presenter.deleteDeployment(
                                            selectionModel.getSelectedObject()

                                    );
                                }
                            }
                        });
            }
        });
        toolStrip.addToolButton(delete);

        formPanel.add(toolStrip);
        formPanel.setWidgetTopHeight(toolStrip, 0, Style.Unit.PX, 30, Style.Unit.PX);

        form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);

        TextItem groupItem = new TextItem("serverGroup", "Deployed to Group");
        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem runtimeName = new TextBoxItem("runtimeName", "Runtime Name");
        TextItem shaItem = new TextItem("sha", "Sha");
        CheckBoxItem suspendedItem = new CheckBoxItem("suspended", "Suspended?");

        form.setFields(groupItem, nameItem,  runtimeName, shaItem, suspendedItem);
        form.bind(deploymentTable);

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "padding-top:15px");
        formPanel.add(formWidget);
        formPanel.setWidgetTopHeight(formWidget, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        // ------------------------------------------

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(formPanel, "Deployment Details");
        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);

        layout.setWidgetBottomHeight(tabLayoutpanel, 0, Style.Unit.PX, 35, Style.Unit.PCT);

        return layout;
    }


    @Override
    public void updateDeployments(List<DeploymentRecord> deploymentRecords) {

        Collections.sort(deploymentRecords, new Comparator<DeploymentRecord>() {

            @Override
            public int compare(DeploymentRecord first, DeploymentRecord second) {
                if(first.getServerGroup().equals(second.getServerGroup()))
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
        });

        deploymentProvider.setList(deploymentRecords);

        if(!deploymentRecords.isEmpty())
            deploymentTable.getSelectionModel().setSelected(deploymentRecords.get(0), true);

    }

    @Override
    public void updateGroups(List<ServerGroupRecord> serverGroupRecords) {

        List<String> names = new ArrayList<String>(serverGroupRecords.size());
        names.add("");
        for(ServerGroupRecord rec : serverGroupRecords)
            names.add(rec.getGroupName());

        groupFilter.setValues(names);
    }
}
