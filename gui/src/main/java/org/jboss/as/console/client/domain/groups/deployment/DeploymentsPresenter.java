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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
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
import org.jboss.as.console.client.shared.deployment.NewDeploymentWizard;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

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
        RevealContentEvent.fire(getEventBus(), DomainRuntimePresenter.TYPE_MainContent, this);
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
    public void addToServerGroup(final DeploymentRecord deployment, final boolean enable, final String... serverGroups) {


        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback() {
                    @Override
                    public void onCancel() {

                    }
                });

        deploymentStore.addToServerGroups(serverGroups, enable, deployment, new SimpleCallback<DMRResponse>() {

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
                    Console.info(Console.MESSAGES.added("Deployment "+deployment.getRuntimeName()));
                }

                domainDeploymentInfo.refreshView();

            }
        });
    }

    @Override
    public void removeContent(final DeploymentRecord deployment) {
        if (domainDeploymentInfo.isAssignedToAnyGroup(deployment)) {
            Exception e = new Exception(Console.CONSTANTS.common_error_contentStillAssignedToGroup());
            DeploymentCommand.REMOVE_FROM_DOMAIN.displayFailureMessage(DeploymentsPresenter.this, deployment, e);
            return;
        }

        final PopupPanel loading = Feedback.loading(
                Console.CONSTANTS.common_label_plaseWait(),
                Console.CONSTANTS.common_label_requestProcessed(),
                new Feedback.LoadingCallback() {
            @Override
            public void onCancel() {

            }
        });

        deploymentStore.removeContent(deployment, new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse response) {

                loading.hide();

                ModelNode result = response.get();

                if(result.isFailure())
                {
                    Console.error(Console.MESSAGES.deletionFailed("Deployment "+deployment.getRuntimeName()), result.getFailureDescription());
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
        window = new DefaultWindow(Console.CONSTANTS.common_label_upload());
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
            }
        });

        window.trapWidget(
                new NewDeploymentWizard(window, dispatcher, domainDeploymentInfo, isUpdate, record).asWidget());

        window.setGlassEnabled(true);
        window.center();
    }

    public List<ServerGroupRecord> getServerGroups() {
        return serverGroups;
    }

    void setServerGroups(List<ServerGroupRecord> serverGroups) {
        this.serverGroups = serverGroups;
    }
}
