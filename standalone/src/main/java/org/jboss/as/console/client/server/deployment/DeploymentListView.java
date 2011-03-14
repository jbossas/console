package org.jboss.as.console.client.server.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.RHSHeader;
import org.jboss.as.console.client.shared.DeploymentRecord;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.Arrays;

/**
 * @author Heiko Braun
 * @date 3/14/11
 */
public class DeploymentListView extends SuspendableViewImpl implements DeploymentListPresenter.MyView{


    private DeploymentListPresenter presenter;
    private DefaultCellTable<DeploymentRecord> deploymentTable;
    private ListDataProvider<DeploymentRecord> deploymentProvider;
    private Form<DeploymentRecord> form;

    @Override
    public void setPresenter(DeploymentListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        RHSHeader title = new RHSHeader("Server Deployments");
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

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


        deploymentTable.addColumn(dplNameColumn, "Name");
        deploymentTable.addColumn(dplRuntimeColumn, "Runtime Name");

        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");


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
        layout.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 65, Style.Unit.PCT);

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
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
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

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem runtimeName = new TextBoxItem("runtimeName", "Runtime Name");
        TextItem shaItem = new TextItem("sha", "Sha");
        CheckBoxItem suspendedItem = new CheckBoxItem("suspended", "Suspended?");

        form.setFields(nameItem,  runtimeName, shaItem, suspendedItem);
        form.bind(deploymentTable);

        Widget formWidget = form.asWidget();
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
}
