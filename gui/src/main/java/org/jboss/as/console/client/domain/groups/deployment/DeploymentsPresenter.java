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
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.runtime.DomainRuntimePresenter;
import org.jboss.as.console.client.shared.deployment.DeployCommandExecutor;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.DeploymentCommandDelegate;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.NewDeploymentWizard;
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
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 3/1/11
 */
public class DeploymentsPresenter extends Presenter<DeploymentsPresenter.MyView, DeploymentsPresenter.MyProxy>
        implements DeployCommandExecutor {

    private final PlaceManager placeManager;
    private DeploymentStore deploymentStore;
    private EntityFilter<DeploymentRecord> filter = new EntityFilter<DeploymentRecord>();
    public List<DeploymentRecord> deployments;
    public List<ServerGroupRecord> serverGroups = new ArrayList<ServerGroupRecord>();
    private DefaultWindow window;
    private DispatchAsync dispatcher;
    private DomainDeploymentInfo domainDeploymentInfo;


    @ProxyCodeSplit
    @NameToken(NameTokens.DeploymentsPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<DeploymentsPresenter>, Place {
    }

    public interface MyView extends SuspendableView {

        void setPresenter(DeploymentsPresenter presenter);

        void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo, DeploymentRecord... targets);
    }

    @Inject
    public DeploymentsPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DeploymentStore deploymentStore,
            ServerGroupStore serverGroupStore, DispatchAsync dispatcher) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.deploymentStore = deploymentStore;
        this.dispatcher = dispatcher;

        domainDeploymentInfo = new DomainDeploymentInfo(this, serverGroupStore, deploymentStore);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        domainDeploymentInfo.refreshView();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, DomainRuntimePresenter.TYPE_MainContent, this);
    }

    @Override
    public void enableDisableDeployment(final DeploymentRecord record) {


        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback() {
                    @Override
                    public void onCancel() {

                    }
                });

        deploymentStore.enableDisableDeployment(record, new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse response) {
                loading.hide();

                ModelNode result = response.get();

                if(result.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("Deployment "+record.getRuntimeName()), result.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("Deployment "+record.getRuntimeName()));
                }

                domainDeploymentInfo.refreshView();

            }

        });

    }

    @Override
    public void removeDeploymentFromGroup(final DeploymentRecord deployment) {
        deploymentStore.removeDeploymentFromGroup(deployment, new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse response) {
                domainDeploymentInfo.refreshView(deployment);
                DeploymentCommand.REMOVE_FROM_GROUP.displaySuccessMessage(DeploymentsPresenter.this, deployment);
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                domainDeploymentInfo.refreshView(deployment);
                DeploymentCommand.REMOVE_FROM_GROUP.displayFailureMessage(DeploymentsPresenter.this, deployment, t);
            }
        });

    }

    @Override
    public void addToServerGroup(
            final DeploymentRecord deployment,
            final boolean enable,
            Set<ServerGroupSelection> selection) {

        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback() {
                    @Override
                    public void onCancel() {

                    }
                });


        Set<String> names = new HashSet<String>();
        for(ServerGroupSelection group : selection)
            names.add(group.getName());

        deploymentStore.addToServerGroups(names, enable, deployment, new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse response) {

                loading.hide();

                ModelNode result = response.get();

                if(result.isFailure())
                {
                    Console.error(Console.MESSAGES.addingFailed("Deployment "+deployment.getRuntimeName()), result.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.added("Deployment "+deployment.getRuntimeName()+ " to group "+serverGroups));
                }

                domainDeploymentInfo.refreshView();

            }
        });
    }

    @Override
    public void removeContent(final DeploymentRecord deployment) {

        Set<String> assignedGroups = domainDeploymentInfo.getAssignedGroups(deployment);

        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback() {
                    @Override
                    public void onCancel() {

                    }
                });

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new LinkedList<ModelNode>();

        for(String group : assignedGroups)
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

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                loading.hide();

                ModelNode result = dmrResponse.get();

                if(result.isFailure())
                {
                    Console.error(Console.MESSAGES.deletionFailed(
                            "Deployment "+deployment.getRuntimeName()),
                            result.getFailureDescription()
                    );
                }
                else
                {
                    Console.info(Console.MESSAGES.deleted("Deployment "+deployment.getRuntimeName()));
                }


                domainDeploymentInfo.refreshView();
            }
        });

    }

    @Override
    public List<ServerGroupRecord> getPossibleGroupAssignments(DeploymentRecord record) {
        List<ServerGroupRecord> possibleGroupAssignments = new ArrayList<ServerGroupRecord>();
        for (ServerGroupRecord group : getServerGroups()) {
            if (!domainDeploymentInfo.isAssignedToGroup(group.getGroupName(), record)) {
                possibleGroupAssignments.add(group);
            }
        }

        return possibleGroupAssignments;
    }

    @Override
    public void promptForGroupSelections(DeploymentRecord record) {
        new ServerGroupSelector(this, record);
    }

    @Override
    public void updateDeployment(DeploymentRecord record) {
        launchNewDeploymentDialoge(record, true);
    }


    public void launchNewDeploymentDialoge(DeploymentRecord record, boolean isUpdate) {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Deployment"));
        window.setWidth(480);
        window.setHeight(450);

        window.trapWidget(
                new NewDeploymentWizard(this, window, domainDeploymentInfo, isUpdate, record).asWidget());

        window.setGlassEnabled(true);
        window.center();
    }

    public List<ServerGroupRecord> getServerGroups() {
        return serverGroups;
    }

    void setServerGroups(List<ServerGroupRecord> serverGroups) {
        this.serverGroups = serverGroups;
    }

    public void onDisableDeploymentInGroup(DeploymentRecord selection) {
        new DeploymentCommandDelegate(this, DeploymentCommand.ENABLE_DISABLE).execute(
                selection
        );
    }

    public void onRemoveDeploymentInGroup(DeploymentRecord selection) {
        new DeploymentCommandDelegate(this, DeploymentCommand.REMOVE_FROM_GROUP).execute(
                selection
        );
    }

    public void onAssignDeploymentToGroup(ServerGroupRecord serverGroup) {


        List<DeploymentRecord> available = new ArrayList<DeploymentRecord>();
        for(DeploymentRecord deployment : domainDeploymentInfo.getDomainDeployments())
        {
            if(!domainDeploymentInfo.isAssignedToGroup(serverGroup.getGroupName(), deployment))
                available.add(deployment);
        }

        if(available.isEmpty())
        {
            Console.warning("All contents assigned to group "+serverGroup.getGroupName());
            return;
        }

        window = new DefaultWindow("Assign Content");
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new AssignToGroupWizard(this, available, serverGroup).asWidget());

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        window.hide();
    }

    public void onAssignDeployments(ServerGroupRecord serverGroup, Set<DeploymentRecord> selectedSet) {
        closeDialogue();

        for(DeploymentRecord deployment : selectedSet)
        {
            HashSet<ServerGroupSelection> groups = new HashSet<ServerGroupSelection>();
            ServerGroupSelection selection = new ServerGroupSelection(serverGroup);
            groups.add(selection);
            addToServerGroup(deployment, false, groups);
        }

    }

    public void onCreateUnmanaged(final DeploymentRecord entity) {
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
        if(entity.getRelativeTo()!=null && !entity.getRelativeTo().equals(""))
            path.get("relative-to").set(entity.getRelativeTo());

        content.add(path);
        operation.get("content").set(content);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                if(response.isFailure())
                {
                    Console.error("Failed to create unmanaged content", response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.added("Deployment "+entity.getName()));
                }

                domainDeploymentInfo.refreshView();
            }
        });
    }
}
