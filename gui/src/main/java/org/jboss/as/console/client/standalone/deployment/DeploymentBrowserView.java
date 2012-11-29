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

package org.jboss.as.console.client.standalone.deployment;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentBrowser;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentDataKeyProvider;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;


/**
 * @author Harald Pehl
 * @date 3/14/11
 */
public class DeploymentBrowserView extends SuspendableViewImpl implements DeploymentBrowserPresenter.MyView
{
    private DeploymentStore deploymentStore;
    private DeploymentBrowserPresenter presenter;
    private DeploymentBrowser deploymentBrowser;


    @Inject
    public DeploymentBrowserView(final DeploymentStore deploymentStore)
    {
        this.deploymentStore = deploymentStore;
    }

    @Override
    public Widget createWidget()
    {
        DeploymentDataKeyProvider<DeploymentRecord> keyProvider = new DeploymentDataKeyProvider<DeploymentRecord>();
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>(
                keyProvider);

        final ToolStrip toolStrip = new ToolStrip();
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                presenter.launchNewDeploymentDialoge(null, false);
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_deploymentListView());
        toolStrip.addToolButtonRight(addBtn);
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.REMOVE_FROM_STANDALONE).execute(
                                    selection
                            );
                        }
                    }
                }));
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_enOrDisable(), new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.ENABLE_DISABLE).execute(
                                    selection
                            );
                        }
                    }
                }));
        toolStrip.addToolButtonRight(new ToolButton("Update", new
                ClickHandler()
                {
                    @Override
                    public void onClick(ClickEvent clickEvent)
                    {
                        DeploymentRecord selection = selectionModel.getSelectedObject();
                        if (selection != null)
                        {
                            new DeploymentCommandDelegate(
                                    DeploymentBrowserView.this.presenter,
                                    DeploymentCommand.UPDATE_CONTENT).execute(
                                    selection
                            );
                        }
                    }
                }));

        deploymentBrowser = new DeploymentBrowser(deploymentStore, selectionModel);
        SimpleLayout layout = new SimpleLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .addContent("title", new ContentGroupLabel(Console.MESSAGES.available("Deployments")))
                .addContent("tools", toolStrip)
                .addContent("browser", deploymentBrowser.getCellBrowser().asWidget())
                .addContent("breadcrumb", deploymentBrowser.getBreadcrumb())
                .addContent("context", deploymentBrowser.getContextPanel());
        return layout.build();
    }

    @Override
    public void setPresenter(DeploymentBrowserPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateDeployments(List<DeploymentRecord> deployments)
    {
        deploymentBrowser.updateDeployments(deployments);
    }
}
