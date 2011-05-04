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
package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandColumn;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSHeader;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class DeploymentsOverview extends SuspendableViewImpl implements DeploymentsPresenter.MyView {

    private DeploymentsPresenter presenter;
    private ListDataProvider<DeploymentRecord> domainDeploymentProvider = new ListDataProvider<DeploymentRecord>();
    private TabLayoutPanel tabLayoutpanel;
    private List<String> serverGroupNames;
    private Map<String, Widget> serverGroupTabsAdded = new HashMap<String, Widget>();
    private Map<String, ListDataProvider<DeploymentRecord>> serverGroupDeploymentProviders = new HashMap<String, ListDataProvider<DeploymentRecord>>();

    @Override
    public void setPresenter(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getSelectedServerGroup() {
        int selected = this.tabLayoutpanel.getSelectedIndex();
        Label label = (Label) this.tabLayoutpanel.getTabWidget(selected);
        return label.getText();
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        RHSHeader title = new RHSHeader(Console.CONSTANTS.common_label_manageDeployments());
        layout.add(title);
        layout.setWidgetTopHeight(title, 0, Style.Unit.PX, 28, Style.Unit.PX);

        final ToolStrip toolStrip = new ToolStrip();

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_addContent(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewDeploymentDialoge();
            }
        }));

        layout.add(toolStrip);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);

        DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PCT);
        panel.addStyleName("fill-layout-width");

        String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(), 
                                              Console.CONSTANTS.common_label_runtimeName(), 
                                              Console.CONSTANTS.common_label_addToGroup(),
                                              Console.CONSTANTS.common_label_remove()};
        List<Column> columns = makeNameAndRuntimeColumns();
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.ADD_TO_GROUP));
        columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.REMOVE_FROM_DOMAIN));

        Widget contentTable = makeDeploymentTable(Console.CONSTANTS.common_label_contentRepository(), domainDeploymentProvider, columns, columnHeaders);

        tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        panel.addSouth(tabLayoutpanel, 50);
        panel.add(contentTable);

        layout.add(panel);
        layout.setWidgetTopHeight(panel, 55, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo) {
        serverGroupNames = domainDeploymentInfo.getServerGroupNames();

        createAndRemoveTabs();

        // Set the backing data for domain tables
        domainDeploymentProvider.setList(domainDeploymentInfo.getDomainDeployments());

        // Set the backing data for server group tables
        for (Entry<String, List<DeploymentRecord>> entry : domainDeploymentInfo.getServerGroupDeployments().entrySet()) {
            this.serverGroupDeploymentProviders.get(entry.getKey()).setList(entry.getValue());
        }

        //  if (!deploymentRecords.isEmpty()) {
        //    deploymentTable.getSelectionModel().setSelected(deploymentRecords.get(0), true);
        //  }
    }

    private Widget makeDeploymentTable(
            String headerLabel,
            ListDataProvider<DeploymentRecord> dataProvider,
            List<Column> columns,
            String[] columnHeaders) {

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.getElement().setAttribute("style", "padding:15px;width:90%");

        // -----------

        vpanel.add(new ContentHeaderLabel(headerLabel));

        DefaultCellTable<DeploymentRecord> deploymentTable = new DefaultCellTable<DeploymentRecord>(20);
        dataProvider.addDataDisplay(deploymentTable);

        for (int i = 0; i < columnHeaders.length; i++) {
            deploymentTable.addColumn(columns.get(i), columnHeaders[i]);
        }

        ScrollPanel scroller = new ScrollPanel(deploymentTable);
        vpanel.add(scroller);

        return vpanel;
    }

    private List<Column> makeNameAndRuntimeColumns() {
        List<Column> columns = new ArrayList<Column>(2);

        columns.add(new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        });

        columns.add(new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        });

        return columns;
    }

    private Column makeEnabledColumn() {
        return new Column<DeploymentRecord, ImageResource>(new ImageResourceCell()) {

            @Override
            public ImageResource getValue(DeploymentRecord deployment) {

                ImageResource res = null;

                if (deployment.isEnabled()) {
                    res = Icons.INSTANCE.statusGreen_small();
                } else {
                    res = Icons.INSTANCE.statusRed_small();
                }

                return res;
            }

        };
    }

    private void createAndRemoveTabs() {
        // add new server groups
        for (String serverGroupName : serverGroupNames) {
            if (this.serverGroupTabsAdded.containsKey(serverGroupName)) {
                continue;
            }

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.setStyleName("fill-layout-width");

            this.tabLayoutpanel.add(vPanel, serverGroupName);
            this.serverGroupTabsAdded.put(serverGroupName, vPanel);
            ListDataProvider<DeploymentRecord> serverGroupProvider = new ListDataProvider<DeploymentRecord>();
            this.serverGroupDeploymentProviders.put(serverGroupName, serverGroupProvider);

            String[] columnHeaders = new String[]{Console.CONSTANTS.common_label_name(), 
                                                  Console.CONSTANTS.common_label_runtimeName(), 
                                                  Console.CONSTANTS.common_label_enabled(), 
                                                  Console.CONSTANTS.common_label_enOrDisable(), 
                                                  Console.CONSTANTS.common_label_remove()};
            List<Column> columns = makeNameAndRuntimeColumns();
            columns.add(makeEnabledColumn());
            columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.ENABLE_DISABLE));
            columns.add(new DeploymentCommandColumn(this.presenter, DeploymentCommand.REMOVE_FROM_GROUP));
            vPanel.add(makeDeploymentTable(serverGroupName + " " + Console.CONSTANTS.common_label_deployments(), 
                                           serverGroupProvider, columns, columnHeaders));
        }

        // find server groups to remove
        List<String> removals = new ArrayList<String>(); // avoid ConcurrentModificationException
        for (Map.Entry<String, Widget> entry : this.serverGroupTabsAdded.entrySet()) {
            String serverGroupName = entry.getKey();
            if (!serverGroupNames.contains(serverGroupName)) {
                removals.add(serverGroupName);
            }
        }

        // remove deleted server groups
        for (String serverGroupName : removals) {
            Widget widget = this.serverGroupTabsAdded.remove(serverGroupName);
            this.tabLayoutpanel.remove(widget);
            this.serverGroupDeploymentProviders.remove(serverGroupName);
        }
    }
}