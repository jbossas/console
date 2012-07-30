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
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

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

        VerticalPanel overviewPanel = new VerticalPanel();
        overviewPanel.setStyleName("fill-layout-width");
        overviewPanel.getElement().setAttribute("style", "padding:10px");

        overviewPanel.add(new ContentHeaderLabel("Server Groups"));
        overviewPanel.add(new ContentDescription("Deployment contents assigned to specific server groups."));
        overviewPanel.add(serverGroupTable);
        // --

        groupDeployments = new ServerGroupDeploymentView(executor);

        panel.addPage(Console.CONSTANTS.common_label_back(), overviewPanel);
        panel.addPage("Group Deployments", groupDeployments.asWidget());

        panel.showPage(0);

        LayoutPanel layout = new LayoutPanel();
        Widget panelWidget = panel.asWidget();

        layout.add(panelWidget);
        //layout.setWidgetLeftWidth(panelWidget, 0, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;

    }

    public void setGroups(List<ServerGroupRecord> serverGroups) {
        dataProvider.setList(serverGroups);
    }
}
