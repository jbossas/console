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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentBrowser;
import org.jboss.as.console.client.shared.deployment.DeploymentDataKeyProvider;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Harald Pehl
 * @date 12/03/2012
 */
public class ServerGroupDeploymentBrowser
{
    private final DomainDeploymentPresenter presenter;
    private final DeploymentStore deploymentStore;
    private ContentHeaderLabel header;
    private ServerGroupRecord currentSelection;
    private DeploymentBrowser deploymentBrowser;


    public ServerGroupDeploymentBrowser(final DomainDeploymentPresenter presenter, final DeploymentStore deploymentStore)
    {
        this.presenter = presenter;
        this.deploymentStore = deploymentStore;
    }

    Widget asWidget()
    {
        DeploymentDataKeyProvider<DeploymentRecord> keyProvider = new DeploymentDataKeyProvider<DeploymentRecord>();
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>(
                keyProvider);

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(new ToolButton("Assign", new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        presenter.onAssignDeploymentToGroup(currentSelection);
                    }
                }));
        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            presenter.onRemoveDeploymentInGroup(selection);
                        }
                    }
                }));
        tools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            presenter.onDisableDeploymentInGroup(selection);
                        }
                    }
                }));

        deploymentBrowser = new DeploymentBrowser(deploymentStore, selectionModel);

        header = new ContentHeaderLabel();
        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setHeadlineWidget(header)
                .setDescription("Deployments assigned to this server group.")
                .addContent("tools", tools)
                .addContent("browser", deploymentBrowser.getCellBrowser().asWidget())
                .addContent("breadcrumb", deploymentBrowser.getBreadcrumb())
                .addContent("context", deploymentBrowser.getContextPanel());
        return layout.build();
    }

    public void setGroup(ServerGroupRecord selection)
    {
        this.currentSelection = selection;
        header.setText("Deployments in group: " + selection.getName());
    }

    public void setDeployments(List<DeploymentRecord> deployments)
    {
        deploymentBrowser.updateDeployments(deployments);
    }
}
