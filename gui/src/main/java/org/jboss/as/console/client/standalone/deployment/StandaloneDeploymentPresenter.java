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

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.StandaloneGateKeeper;
import org.jboss.as.console.client.domain.groups.deployment.ServerGroupSelection;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.deployment.DeployCommandExecutor;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.NewDeploymentWizard;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.standalone.runtime.StandaloneRuntimePresenter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 * @date 3/14/11
 */
public class StandaloneDeploymentPresenter
        extends Presenter<StandaloneDeploymentPresenter.MyView, StandaloneDeploymentPresenter.MyProxy>
        implements DeployCommandExecutor
{
    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentBrowserPresenter)
    @UseGatekeeper(StandaloneGateKeeper.class)
    public interface MyProxy extends Proxy<StandaloneDeploymentPresenter>, Place
    {
    }

    public interface MyView extends View
    {
        void setPresenter(StandaloneDeploymentPresenter presenter);
        void updateDeployments(List<DeploymentRecord> deployments);
    }

    private DeploymentStore deploymentStore;
    private DefaultWindow window;
    private DispatchAsync dispatcher;

    @Inject
    public StandaloneDeploymentPresenter(EventBus eventBus, MyView view, MyProxy proxy, DeploymentStore deploymentStore,
            DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.deploymentStore = deploymentStore;
        this.dispatcher = dispatcher;
    }


    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset()
    {
        super.onReset();
        loadDeployments();
    }

    @Override
    public void refreshDeployments()
    {
        loadDeployments();
    }

    private void loadDeployments()
    {
        deploymentStore.loadDeployments(new SimpleCallback<List<DeploymentRecord>>()
        {
            @Override
            public void onSuccess(List<DeploymentRecord> result)
            {
                getView().updateDeployments(result);
            }
        });
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, StandaloneRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void onRemoveContent(final DeploymentRecord record)
    {
        deploymentStore.removeContent(record, new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse response)
            {
                refreshDeployments();
                DeploymentCommand.REMOVE_FROM_STANDALONE.displaySuccessMessage(StandaloneDeploymentPresenter.this, record);
            }

            @Override
            public void onFailure(Throwable t)
            {
                super.onFailure(t);
                refreshDeployments();
                DeploymentCommand.REMOVE_FROM_STANDALONE
                        .displayFailureMessage(StandaloneDeploymentPresenter.this, record, t);
            }
        });
    }

    @Override
    public void enableDisableDeployment(final DeploymentRecord record)
    {
        final String success;
        final String failed;
        if (record.isEnabled())
        {
            success = Console.MESSAGES.successDisabled(record.getRuntimeName());
            failed = Console.MESSAGES.failedToDisable(record.getRuntimeName());
        }
        else
        {
            success = Console.MESSAGES.successEnabled(record.getRuntimeName());
            failed = Console.MESSAGES.failedToEnable(record.getRuntimeName());
        }
        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback()
                {
                    @Override
                    public void onCancel()
                    {

                    }
                });

        deploymentStore.enableDisableDeployment(record, new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                loading.hide();
                Console.error(failed, caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse response)
            {
                loading.hide();
                ModelNode result = response.get();
                if (result.isFailure())
                {
                    Console.error(failed, result.getFailureDescription());
                }
                else
                {
                    Console.info(success);
                }
                refreshDeployments();
            }
        });
    }

    @Override
    public void onAssignToServerGroup(DeploymentRecord record, boolean enable, Set<ServerGroupSelection> selectedGroups)
    {
        throw new UnsupportedOperationException("Not supported in standalone mode.");
    }

    @Override
    public List<ServerGroupRecord> getPossibleGroupAssignments(DeploymentRecord record)
    {
        return Collections.emptyList();
    }

    @Override
    public void launchGroupSelectionWizard(DeploymentRecord record)
    {
        throw new UnsupportedOperationException("Not supported in standalone mode.");
    }

    @Override
    public void removeDeploymentFromGroup(DeploymentRecord record)
    {
        throw new UnsupportedOperationException("Not supported in standalone mode.");
    }

    @Override
    public void updateDeployment(DeploymentRecord record)
    {
        launchDeploymentDialoge("Update Deployment", record, true);
    }

    public void launchNewDeploymentDialoge(DeploymentRecord record, boolean isUpdate)
    {
        launchDeploymentDialoge(Console.MESSAGES.createTitle("Deployment"), record, isUpdate);
    }

    private void launchDeploymentDialoge(String title, DeploymentRecord record, boolean isUpdate)
    {
        window = new DefaultWindow(title);
        window.setWidth(480);
        window.setHeight(450);
        window.trapWidget(new NewDeploymentWizard(this, window, isUpdate, record).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void onCreateUnmanaged(final DeploymentRecord entity)
    {
        window.hide();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("deployment", entity.getName());
        operation.get("name").set(entity.getName());
        operation.get("runtime-name").set(entity.getRuntimeName());
        List<ModelNode> content = new ArrayList<ModelNode>(1);
        ModelNode path = new ModelNode();
        path.get("path").set(entity.getPath());
        path.get("archive").set(entity.isArchive());
        if (entity.getRelativeTo() != null && !entity.getRelativeTo().equals(""))
        { path.get("relative-to").set(entity.getRelativeTo()); }

        content.add(path);
        operation.get("content").set(content);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse dmrResponse)
            {
                ModelNode response = dmrResponse.get();
                if (response.isFailure())
                {
                    Console.error("Failed to create unmanaged content", response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.added("Deployment " + entity.getName()));
                }
                refreshDeployments();
            }
        });
    }
}
