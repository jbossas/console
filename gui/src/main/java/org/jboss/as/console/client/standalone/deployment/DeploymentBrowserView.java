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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystem;
import org.jboss.as.console.client.shared.viewframework.builder.OneToOneLayout;
import org.jboss.as.console.client.widgets.browser.DefaultCellBrowser;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;


/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/14/11
 */
public class DeploymentBrowserView extends SuspendableViewImpl implements DeploymentBrowserPresenter.MyView
{
    private DeploymentFilter filter;
    private DeploymentStore deploymentStore;
    private DeploymentBrowserPresenter presenter;
    private DeploymentTreeModel deploymentTreeModel;
    private DeckPanel contextPanel;
    private Form<DeploymentRecord> deploymentForm;


    @Inject
    public DeploymentBrowserView(final DeploymentStore deploymentStore)
    {
        this.deploymentStore = deploymentStore;
    }

    @Override
    public Widget createWidget()
    {
        DeploymentKeyProvider keyProvider = new DeploymentKeyProvider();
        final DeploymentSelectionModel selectionModel = new DeploymentSelectionModel(keyProvider, presenter);

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

        ListDataProvider<DeploymentRecord> dataProvider = new ListDataProvider<DeploymentRecord>(keyProvider);
        filter = new DeploymentFilter(dataProvider);
        toolStrip.addToolWidget(filter.asWidget());

        contextPanel = new DeckPanel();
        deploymentForm = new Form<DeploymentRecord>(DeploymentRecord.class);
        deploymentForm.setNumColumns(2);
        deploymentForm.setEnabled(false);
        TextAreaItem name = new TextAreaItem("name", "Name");
        TextAreaItem runtimeName = new TextAreaItem("runtimeName", "Runtime Name");
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextBoxItem relative = new TextBoxItem("relativeTo", "Relative To");
        deploymentForm.setFields(name, path, runtimeName, relative);
        contextPanel.add(new Label("No information available."));
        contextPanel.add(deploymentForm.asWidget());
        contextPanel.showWidget(0);

        deploymentTreeModel = new DeploymentTreeModel(presenter, deploymentStore, dataProvider, selectionModel);
        DefaultCellBrowser cellBrowser = new DefaultCellBrowser.Builder(deploymentTreeModel, null).build();

        OneToOneLayout layout = new OneToOneLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .setMaster(Console.MESSAGES.available("Deployments"), cellBrowser)
                .setMasterTools(toolStrip)
                .setDetail("Properties", contextPanel);
        return layout.build();
    }

    @Override
    public void setPresenter(DeploymentBrowserPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateDeploymentInfo(List<DeploymentRecord> deployments)
    {
        deploymentTreeModel.updateDeployments(deployments);
    }

    @Override
    public <T> void updateContext(final T selectedContext)
    {
        if (selectedContext instanceof DeploymentRecord)
        {
            DeploymentRecord deployment = (DeploymentRecord) selectedContext;
            deploymentForm.edit(deployment);
            contextPanel.showWidget(1);
        }
        else if (selectedContext instanceof DeploymentSubsystem)
        {
            DeploymentSubsystem subsystem = (DeploymentSubsystem) selectedContext;
            switch (subsystem.getType())
            {
                case ejb3:
                    break;
                case jpa:
                    break;
                case web:
                    break;
                case webservices:
                    break;
            }
        }
        else
        {
            contextPanel.showWidget(0);
        }
    }
}
