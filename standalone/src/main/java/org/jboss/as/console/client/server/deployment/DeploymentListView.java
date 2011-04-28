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

package org.jboss.as.console.client.server.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import java.util.List;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.ActionColumnFactory;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.RHSHeader;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;


/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/14/11
 */
public class DeploymentListView extends SuspendableViewImpl implements DeploymentListPresenter.MyView{


    private DeploymentListPresenter presenter;
    private DefaultCellTable<DeploymentRecord> deploymentTable;
    private ListDataProvider<DeploymentRecord> deploymentProvider;

    @Override
    public void setPresenter(DeploymentListPresenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void updateDeploymentInfo(List<DeploymentRecord> deployments) {
      deploymentProvider.setList(deployments);
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

        TextColumn<DeploymentRecord> enabledDisabledColumn = new TextColumn<DeploymentRecord>() {
             @Override
             public String getValue(DeploymentRecord record) {
               return Boolean.toString(record.isEnabled());
             }
        };

        deploymentTable.addColumn(dplNameColumn, "Name");
        deploymentTable.addColumn(dplRuntimeColumn, "Runtime Name");
        deploymentTable.addColumn(enabledDisabledColumn, "Enabled?");
        
        List<Column> actions = ActionColumnFactory.makeActionColumns(presenter, deploymentProvider, DeploymentCommand.ENABLE_DISABLE, DeploymentCommand.REMOVE_CONTENT);
        deploymentTable.addColumn(actions.get(0), "Action");
        deploymentTable.addColumn(actions.get(1), "Action");
        
        vpanel.add(deploymentTable);


        ScrollPanel scroll = new ScrollPanel();
        scroll.add(vpanel);

        layout.add(scroll);
        layout.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 65, Style.Unit.PCT);
        
        return layout;
    }
    
}
