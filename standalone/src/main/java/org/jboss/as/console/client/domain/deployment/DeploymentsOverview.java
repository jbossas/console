package org.jboss.as.console.client.domain.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.StackSectionHeader;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.Arrays;
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

        DeploymentHeader title = new DeploymentHeader();
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        // -----------

        ContentHeaderLabel nameLabel = new ContentHeaderLabel("Available Deployments");
        nameLabel.setIcon("common/server_group.png");
        vpanel.add(nameLabel);

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

        deploymentTable.addColumn(dplNameColumn, "Name");
        deploymentTable.addColumn(dplRuntimeColumn, "Runtime Name");

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
        layout.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 60, Style.Unit.PCT);

        // ----------- --------------------------------------------------

        StackLayoutPanel stack = new StackLayoutPanel(Style.Unit.PX);
        stack.addStyleName("section-stack");
        stack.getElement().setAttribute("style", "background:#ffffff;");

        LayoutPanel formPanel = new LayoutPanel();
        formPanel.getElement().setAttribute("style", "margin:15px;");


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

        Widget formWidget = form.asWidget();
        formPanel.add(formWidget);
        formPanel.setWidgetTopHeight(formWidget, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        stack.add(formPanel, new StackSectionHeader("Deployment Details"), 28);


        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                form.edit(selectionModel.getSelectedObject());
            }
        });

        deploymentTable.setSelectionModel(selectionModel);


        layout.add(stack);
        layout.setWidgetBottomHeight(stack, 0, Style.Unit.PX, 40, Style.Unit.PCT);

        return layout;
    }


    @Override
    public void updateDeployments(List<DeploymentRecord> deploymentRecords) {
        deploymentProvider.setList(deploymentRecords);
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
