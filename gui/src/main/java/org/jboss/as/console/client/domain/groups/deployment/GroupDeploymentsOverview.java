package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeployCommandExecutor;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/30/12
 */
public class GroupDeploymentsOverview {

    private ServerGroupDeploymentView groupDeployments;
    private DeployCommandExecutor executor;
    private DefaultCellTable<ServerGroupRecord> serverGroupTable;
    private ListDataProvider<ServerGroupRecord> dataProvider;
    private PagedView panel;
    private Map<String, List<DeploymentRecord>> deploymentPerGroup;

    public GroupDeploymentsOverview(DeployCommandExecutor executor) {
        this.executor = executor;
    }

    Widget asWidget() {

        panel = new PagedView();

        serverGroupTable = new DefaultCellTable<ServerGroupRecord>(8, new ProvidesKey<ServerGroupRecord>() {
            @Override
            public Object getKey(ServerGroupRecord serverGroupRecord) {
                return serverGroupRecord.getGroupName();
            }
        });
        dataProvider = new ListDataProvider<ServerGroupRecord>();
        this.dataProvider.addDataDisplay(serverGroupTable);

        final SingleSelectionModel<ServerGroupRecord> selectionModel = new SingleSelectionModel<ServerGroupRecord>();
        serverGroupTable.setSelectionModel(selectionModel);

        Column nameColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getGroupName();
            }
        };

        Column profileColumn = new TextColumn<ServerGroupRecord>() {
            @Override
            public String getValue(ServerGroupRecord serverGroup) {
                return serverGroup.getProfileName();
            }
        };

        serverGroupTable.addColumn(nameColumn, Console.CONSTANTS.common_label_serverGroup());
        serverGroupTable.addColumn(profileColumn, Console.CONSTANTS.common_label_profile());

        Column<ServerGroupRecord, ServerGroupRecord> option = new Column<ServerGroupRecord, ServerGroupRecord>(
                new TextLinkCell<ServerGroupRecord>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<ServerGroupRecord>() {
                    @Override
                    public void execute(ServerGroupRecord selection) {
                        groupDeployments.setGroup(selection);
                        groupDeployments.setDeploymentInfo(deploymentPerGroup.get(selection.getGroupName()));
                        panel.showPage(1);
                    }
                })
        ) {
            @Override
            public ServerGroupRecord getValue(ServerGroupRecord manager) {
                return manager;
            }
        };
        serverGroupTable.addColumn(option, Console.CONSTANTS.common_label_option());

        SimpleLayout overviewPanel = new SimpleLayout()
                .setPlain(true)
                .setHeadline("Server Groups")
                .setDescription("Please chose a server group to assign deployment contents.")
                .addContent("Available Groups", serverGroupTable.asWidget());

        // --

        groupDeployments = new ServerGroupDeploymentView(executor);

        panel.addPage(Console.CONSTANTS.common_label_back(), overviewPanel.build());
        panel.addPage("Group Deployments", groupDeployments.asWidget());

        panel.showPage(0);

        LayoutPanel layout = new LayoutPanel();
        Widget panelWidget = panel.asWidget();

        layout.add(panelWidget);

        return layout;

    }

    public void setGroups(List<ServerGroupRecord> serverGroups) {
        dataProvider.setList(serverGroups);
    }

    public void setGroupDeployments(Map<String, List<DeploymentRecord>> deploymentPerGroup) {
        this.deploymentPerGroup = deploymentPerGroup;
    }
}
