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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.groups.ServerGroupMgmtPresenter;
import org.jboss.as.console.client.domain.model.EntityFilter;
import org.jboss.as.console.client.domain.model.Predicate;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.dmr.client.Base64;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/1/11
 */
public class DeploymentsPresenter extends Presenter<DeploymentsPresenter.MyView, DeploymentsPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DeploymentStore deploymentStore;
    private ServerGroupStore serverGroupStore;

    private String groupFilter = "";
    private String typeFilter= "";

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
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        
        domainDeploymentInfo = new DomainDeploymentInfo(getView(), serverGroupStore, deploymentStore);
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

    public void deleteDeployment(DeploymentRecord deploymentRecord) {
        deploymentStore.deleteDeployment(deploymentRecord, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }
        });
    }


    public void onFilterType(final String type) {

        this.typeFilter = type;
/*        getView().updateDeployments(
                filter.apply(new TypeAndGroupPredicate(), deployments)
        ); */
    }

    class TypeAndGroupPredicate implements Predicate<DeploymentRecord>
    {
        @Override
        public boolean appliesTo(DeploymentRecord candidate) {


            boolean groupMatch = groupFilter.equals("") ?
                    true : candidate.getServerGroup().equals(groupFilter);

            boolean typeMatch = typeFilter.equals("") ?
                    true : candidate.getName().endsWith(typeFilter);

            return groupMatch && typeMatch;
        }
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
                new NewDeploymentWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();

    }

    public void closeDialoge() {
        window.hide();
    }

    public List<ServerGroupRecord> getServerGroups() {
        return serverGroups;
    }


    public void onDeployToGroup(final DeploymentReference deployment) {

        window.hide();


        /*ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("deployment", deployment.getName());

        try {
            byte[] decoded = Base64.decode(deployment.getHash());
            operation.get(HASH).set(decoded);

            dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                @Override
                public void onSuccess(DMRResponse result) {
                    ModelNode response = ModelNode.fromBase64(result.getResponseText());
                    System.out.println(response.toJSONString());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } */

        /*{"address":[{"deployment":"test.war"}],
            "operation":"add","hash":{"BYTES_VALUE":"7jgpMVmynfxpqp8UDleKLmtgbrA="},
            "name":"test.war"}
         */

        assignDeploymentName(deployment);

    }

    private void assignDeploymentName(final DeploymentReference deployment) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"address\":[").append("{\"deployment\":\"").append(deployment.getName()).append("\"}],");
        sb.append("\"operation\":\"add\",\"hash\":");
        sb.append("{");
        sb.append("\"BYTES_VALUE\":\"").append(deployment.getHash()).append("\"");
        sb.append("},");
        sb.append("\"name\":\"").append(deployment.getName()).append("\"");
        sb.append("}");

        String requestJSO = sb.toString();
        //System.out.println(requestJSO);

        RequestBuilder rb = new RequestBuilder(
                RequestBuilder.POST,
                Console.MODULES.getBootstrapContext().getProperty(BootstrapContext.DOMAIN_API)
        );

        try {
            rb.sendRequest(requestJSO, new RequestCallback(){
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if(200 == response.getStatusCode())
                        assignToGroup(deployment);
                    else
                        onDeploymentFailed(deployment);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Log.error("Deployment failed", exception);
                }
            });
        } catch (RequestException e) {
            Log.error("Unknown error", e);
        }
    }

    private void assignToGroup(final DeploymentReference deployment) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("server-group", deployment.getGroup());
        operation.get(ADDRESS).add("deployment", deployment.getName());
        operation.get("enabled").set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Deployment failed", caught);
                onDeploymentFailed(deployment);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                //refreshModel();
            }
        });
    }

    private void onDeploymentFailed(DeploymentReference deployment) {
        Console.MODULES.getMessageCenter().notify(
                new Message("Deployment failed: "+deployment.getName(), Message.Severity.Error)
        );
    }

}
