/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.deployment.DeployCommandExecutor;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.NewDeploymentWizard;
import org.jboss.as.console.client.shared.deployment.model.ContentRepository;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @author Harald Pehl
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class DomainDeploymentPresenter extends Presenter<DomainDeploymentPresenter.MyView, DomainDeploymentPresenter.MyProxy>
        implements DeployCommandExecutor
{
    private List<ServerGroupRecord> serverGroups;
    private DeploymentStore deploymentStore;
    private DefaultWindow window;
    private DispatchAsync dispatcher;
    private ContentRepository contentRepository;


    @Inject
    public DomainDeploymentPresenter(EventBus eventBus, MyView view, MyProxy proxy, DeploymentStore deploymentStore,
            DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.deploymentStore = deploymentStore;
        this.dispatcher = dispatcher;
        this.serverGroups = new ArrayList<ServerGroupRecord>();
    }

    @Override
    protected void onBind()
    {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void revealInParent()
    {
        RevealContentEvent.fire(this, DomainRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    protected void onReset()
    {
        super.onReset();
        loadContentRepository();
    }

    @Override
    public void refreshDeployments()
    {
        loadContentRepository();
    }

    private void loadContentRepository()
    {
        deploymentStore.loadContentRepository(new SimpleCallback<ContentRepository>()
        {
            @Override
            public void onSuccess(final ContentRepository result)
            {
                DomainDeploymentPresenter.this.contentRepository = result;
                getView().reset(result);
            }
        });
    }


    // ------------------------------------------------------ TODO Refactor

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
    public void removeDeploymentFromGroup(final DeploymentRecord deployment)
    {
        deploymentStore.removeDeploymentFromGroup(deployment, new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse response)
            {
                refreshDeployments();
                DeploymentCommand.REMOVE_FROM_GROUP.displaySuccessMessage(DomainDeploymentPresenter.this, deployment);
            }

            @Override
            public void onFailure(Throwable t)
            {
                super.onFailure(t);
                refreshDeployments();
                DeploymentCommand.REMOVE_FROM_GROUP.displayFailureMessage(DomainDeploymentPresenter.this, deployment, t);
            }
        });
    }

    @Override
    public void onAssignToServerGroup(final DeploymentRecord deployment, final boolean enable,
            Set<ServerGroupSelection> selection)
    {
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

        Set<String> names = new HashSet<String>();
        for (ServerGroupSelection group : selection)
        { names.add(group.getName()); }

        deploymentStore.addToServerGroups(names, enable, deployment, new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                loading.hide();
                Console.error(Console.MESSAGES.addingFailed("Deployment " + deployment.getRuntimeName()),
                        caught.getMessage());
            }

            @Override
            public void onSuccess(DMRResponse response)
            {
                loading.hide();
                ModelNode result = response.get();
                if (result.isFailure())
                {
                    Console.error(Console.MESSAGES.addingFailed("Deployment " + deployment.getRuntimeName()),
                            result.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES
                            .added("Deployment " + deployment.getRuntimeName() + " to group " + serverGroups));
                }
                DomainDeploymentPresenter.this.refreshDeployments();
            }
        });
    }

    @Override
    public void onRemoveContent(final DeploymentRecord deployment)
    {
        assert contentRepository != null : "Contentrepository must not be null!";
        List<String> assignedGroups = contentRepository.getServerGroups(deployment);

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

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new LinkedList<ModelNode>();
        for (String group : assignedGroups)
        {
            ModelNode groupOp = new ModelNode();
            groupOp.get(OP).set(REMOVE);
            groupOp.get(ADDRESS).add("server-group", group);
            groupOp.get(ADDRESS).add("deployment", deployment.getName());
            steps.add(groupOp);
        }

        ModelNode removeContentOp = new ModelNode();
        removeContentOp.get(OP).set(REMOVE);
        removeContentOp.get(ADDRESS).add("deployment", deployment.getName());
        steps.add(removeContentOp);
        operation.get(STEPS).set(steps);
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse dmrResponse)
            {
                loading.hide();

                ModelNode result = dmrResponse.get();

                if (result.isFailure())
                {
                    Console.error(Console.MESSAGES.deletionFailed(
                            "Deployment " + deployment.getRuntimeName()),
                            result.getFailureDescription()
                    );
                }
                else
                {
                    Console.info(Console.MESSAGES.deleted("Deployment " + deployment.getRuntimeName()));
                }
                refreshDeployments();
            }
        });
    }

    @Override
    public List<ServerGroupRecord> getPossibleGroupAssignments(DeploymentRecord record)
    {
        assert contentRepository!=null;
        return contentRepository.getPossibleServerGroupAssignments(record);
    }

    @Override
    public void launchGroupSelectionWizard(DeploymentRecord record)
    {
        new ServerGroupSelector(this, record);
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

    public void launchDeploymentDialoge(String title, DeploymentRecord record, boolean isUpdate)
    {
        window = new DefaultWindow(title);
        window.setWidth(480);
        window.setHeight(450);
        window.trapWidget(
                new NewDeploymentWizard(this, window, isUpdate, record).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void onDisableDeploymentInGroup(DeploymentRecord selection)
    {
        new DeploymentCommandDelegate(this, DeploymentCommand.ENABLE_DISABLE).execute(
                selection
        );
    }

    public void onRemoveDeploymentInGroup(DeploymentRecord selection)
    {
        new DeploymentCommandDelegate(this, DeploymentCommand.REMOVE_FROM_GROUP).execute(
                selection
        );
    }

    public void launchAssignDeploymentToGroupWizard(ServerGroupRecord serverGroup)
    {
        assert contentRepository != null;
        List<DeploymentRecord> available = contentRepository.getPossibleServerGroupAssignments(serverGroup);

        window = new DefaultWindow("Assign Content");
        window.setWidth(480);
        window.setHeight(360);
        window.trapWidget(
                new AssignToGroupWizard(this, available, serverGroup).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue()
    {
        window.hide();
    }

    public void onAssignDeployments(ServerGroupRecord serverGroup, Set<DeploymentRecord> selectedSet)
    {
        closeDialogue();
        for (DeploymentRecord deployment : selectedSet)
        {
            HashSet<ServerGroupSelection> groups = new HashSet<ServerGroupSelection>();
            ServerGroupSelection selection = new ServerGroupSelection(serverGroup);
            groups.add(selection);
            onAssignToServerGroup(deployment, false, groups);
        }
    }

    public void onCreateUnmanaged(final DeploymentRecord entity)
    {
        window.hide();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("deployment", entity.getName());
        operation.get("name").set(entity.getName());
        operation.get("runtime-name").set(entity.getName());
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


    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentsPresenter)
    @UseGatekeeper(DomainGateKeeper.class)
    public interface MyProxy extends Proxy<DomainDeploymentPresenter>, Place
    {
    }


    public interface MyView extends SuspendableView
    {
        void setPresenter(DomainDeploymentPresenter presenter);
        void reset(ContentRepository contentRepository);
    }
}
