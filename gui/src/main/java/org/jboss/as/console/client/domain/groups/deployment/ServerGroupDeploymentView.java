package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.DeploymentNameColumn;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/30/12
 */
public class ServerGroupDeploymentView {

    private DefaultCellTable<DeploymentRecord> table;
    private ListDataProvider<DeploymentRecord> dataProvider;
    private DeploymentsPresenter presenter;
    private ContentHeaderLabel header;
    private ServerGroupRecord currentSelection;
    private DeploymentFilter filter;

    public ServerGroupDeploymentView(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        final DeploymentNameColumn deploymentNameColumn = new DeploymentNameColumn();

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                String title = null;
                if(record.getRuntimeName().length()>27)
                    title = record.getRuntimeName().substring(0,26)+"...";
                else
                    title = record.getRuntimeName();
                return title;
            }
        };


        final Column<DeploymentRecord, ImageResource> statusColumn = new Column<DeploymentRecord, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(DeploymentRecord deployment) {

                ImageResource res = null;

                if (deployment.isEnabled()) {
                    res = Icons.INSTANCE.status_good();
                } else {
                    res = Icons.INSTANCE.status_bad();
                }

                return res;
            }

        };

        this.table = new DefaultCellTable<DeploymentRecord>(8, new ProvidesKey<DeploymentRecord>() {
            @Override
            public Object getKey(DeploymentRecord deploymentRecord) {
                return deploymentRecord.getName();
            }

        });
        dataProvider = new ListDataProvider<DeploymentRecord>();
        dataProvider.addDataDisplay(table);

        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>();
        table.setSelectionModel(selectionModel);

        table.addColumn(deploymentNameColumn, Console.CONSTANTS.common_label_name());
        table.addColumn(dplRuntimeColumn, Console.CONSTANTS.common_label_runtimeName());
        table.addColumn(statusColumn, Console.CONSTANTS.common_label_enabled());


        Form<DeploymentRecord> form = new Form<DeploymentRecord>(DeploymentRecord.class);
        form.setNumColumns(2);
        form.setEnabled(true);
        TextAreaItem name = new TextAreaItem("name", "Name");
        //TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        form.setFields(name);

        form.bind(table);

        ToolStrip tools = new ToolStrip();

        filter = new DeploymentFilter(dataProvider);
        tools.addToolWidget(filter.asWidget());

        tools.addToolButtonRight(new ToolButton("Assign", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                presenter.onAssignDeploymentToGroup(currentSelection);
            }
        }));

        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {
                    presenter.onRemoveDeploymentInGroup(selection);
                }
            }
        }));

        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                DeploymentRecord selection = selectionModel.getSelectedObject();
                if(selection!=null)
                {

                    presenter.onDisableDeploymentInGroup(selection);

                }
            }
        }));

        header = new ContentHeaderLabel();

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(header)
                .setMaster(Console.MESSAGES.available("Deployments"), table)
                .setMasterTools(tools)
                .setDescription("Deployments assigned to this server group.")
                .setDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());

        return layout.build();
    }

    public void setGroup(ServerGroupRecord selection) {
        this.currentSelection = selection;

        header.setText("Deployments in group: "+ selection.getName());
    }

    public void setDeploymentInfo(List<DeploymentRecord> deploymentRecords) {
        dataProvider.setList(deploymentRecords);
        dataProvider.flush();
        table.selectDefaultEntity();
        filter.reset(true);

    }

    public ServerGroupRecord getCurrentSelection() {
        return currentSelection;
    }
}
