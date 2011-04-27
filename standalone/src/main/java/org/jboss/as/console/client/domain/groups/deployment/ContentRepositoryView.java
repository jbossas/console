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

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tables.HyperlinkCell;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 4/27/11
 */
public class ContentRepositoryView {

    private DeploymentsPresenter presenter;
    private DefaultCellTable<DeploymentRecord> deploymentTable;
    private ListDataProvider<DeploymentRecord> deploymentProvider;

    public ContentRepositoryView(DeploymentsPresenter presenter) {
        this.presenter = presenter;
    }

    private Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();

        final ToolStrip topLevelTools = new ToolStrip();
        final ToolButton newButton = new ToolButton("New Deployment");
        newButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.launchNewDeploymentDialoge();
            }
        });

        topLevelTools.addToolButtonRight(newButton);
        layout.add(topLevelTools);

        // ----------

        VerticalPanel panel = new VerticalPanel();
        layout.add(panel);


        String[] columnHeaders = new String[] {"Name", "Runtime Name", "Action", "Action"};

        deploymentTable = new DefaultCellTable<DeploymentRecord>(20);
        deploymentProvider = new ListDataProvider<DeploymentRecord>();
        deploymentProvider.addDataDisplay(deploymentTable);


        final TextColumn<DeploymentRecord> nameCol = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        };

        deploymentTable.addColumn(nameCol, "Name");

        final TextColumn<DeploymentRecord> runtimeCol = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        };

        deploymentTable.addColumn(runtimeCol, "Runtime Name");

        HyperlinkCell hyperlinkCell = new HyperlinkCell("Add to Group", new ActionCell.Delegate<String>() {
            @Override
            public void execute(String rowNum) {

            }
        });

        Column<DeploymentRecord, String> hyperlinkColumn = new Column<DeploymentRecord, String>(hyperlinkCell) {
            @Override
            public String getValue(DeploymentRecord object) {
                return "";
            }

        };

        deploymentTable.addColumn(runtimeCol, "Runtime Name");


        // ----------

        layout.setWidgetTopHeight(topLevelTools, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }
}
