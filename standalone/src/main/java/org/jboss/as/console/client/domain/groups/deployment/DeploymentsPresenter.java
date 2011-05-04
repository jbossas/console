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
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.deployment.DeployCommandExecutor;
import org.jboss.as.console.client.shared.deployment.DeploymentCommand;
import org.jboss.as.console.client.shared.deployment.NewDeploymentWizard;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.widgets.DefaultWindow;

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
    public interface MyProxy extends Proxy<DeploymentsPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DeploymentsPresenter presenter);
        void updateDeploymentInfo(DomainDeploymentInfo domainDeploymentInfo);
        String getSelectedServerGroup();
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
        RevealContentEvent.fire(getEventBus(), ServerGroupMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void enableDisableDeployment(final DeploymentRecord deployment) {
      deploymentStore.enableDisableDeployment(deployment, new SimpleCallback<DMRResponse>() {
        @Override
        public void onSuccess(DMRResponse response) {
          domainDeploymentInfo.refreshView();
          DeploymentCommand.ENABLE_DISABLE.displaySuccessMessage(deployment);
        }
        @Override
        public void onFailure(Throwable t) {
          super.onFailure(t);
          domainDeploymentInfo.refreshView();
          DeploymentCommand.ENABLE_DISABLE.displayFailureMessage(deployment, t);
        }
      });
      
    }
    
    @Override
    public void removeDeploymentFromGroup(final DeploymentRecord deployment) {
      deploymentStore.removeDeploymentFromGroup(deployment, new SimpleCallback<DMRResponse>() {
        @Override
        public void onSuccess(DMRResponse response) {
          domainDeploymentInfo.refreshView();
          DeploymentCommand.REMOVE_FROM_GROUP.displaySuccessMessage(deployment);
        }
        @Override
        public void onFailure(Throwable t) {
          super.onFailure(t);
          domainDeploymentInfo.refreshView();
          DeploymentCommand.REMOVE_FROM_GROUP.displayFailureMessage(deployment, t);
        }
      });
      
    }
    
    @Override
    public void addToServerGroup(final String serverGroup, final DeploymentRecord deployment) {
      deploymentStore.addToServerGroup(serverGroup, deployment, new SimpleCallback<DMRResponse>() {
        @Override
        public void onSuccess(DMRResponse response) {
          domainDeploymentInfo.refreshView();
          DeploymentCommand.ADD_TO_GROUP.displaySuccessMessage(deployment);
        }
        @Override
        public void onFailure(Throwable t) {
          super.onFailure(t);
          domainDeploymentInfo.refreshView();
          DeploymentCommand.ADD_TO_GROUP.displayFailureMessage(deployment, t);
        }
      });
    }

    @Override
    public void removeContent(final DeploymentRecord deployment) {
      deploymentStore.removeContent(deployment, new SimpleCallback<DMRResponse>() {
        @Override
        public void onSuccess(DMRResponse response) {
          domainDeploymentInfo.refreshView();
          DeploymentCommand.REMOVE_FROM_DOMAIN.displaySuccessMessage(deployment);
        }
        @Override
        public void onFailure(Throwable t) {
          super.onFailure(t);
          domainDeploymentInfo.refreshView();
          DeploymentCommand.REMOVE_FROM_DOMAIN.displayFailureMessage(deployment, t);
        }
      });
    }
    
    @Override
    public String getSelectedServerGroup() {
      return getView().getSelectedServerGroup();
    }

    public void launchNewDeploymentDialoge() {

        window = new DefaultWindow("Create Deployment");
        window.setWidth(320);
        window.setHeight(240);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewDeploymentWizard(window, dispatcher, domainDeploymentInfo, domainDeploymentInfo.getServerGroupNames()).asWidget()
        );

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
