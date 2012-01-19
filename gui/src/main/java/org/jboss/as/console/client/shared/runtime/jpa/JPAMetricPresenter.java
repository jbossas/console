package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class JPAMetricPresenter extends Presenter<JPAMetricPresenter.MyView, JPAMetricPresenter.MyProxy>
    implements ServerSelectionEvent.ServerSelectionListener {

    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.JPAMetricPresenter)
    public interface MyProxy extends Proxy<JPAMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JPAMetricPresenter presenter);

        void setJpaUnits(List<JPADeployment> jpaUnits);
    }

    @Inject
    public JPAMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.serverSelection = serverSelection;
        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();

        if(serverSelection.isActive())
            refresh();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {

        //getView().clearSamples();
        if(isVisible()) refresh();
    }

    public void refresh() {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("deployment", "*");
        operation.get(ADDRESS).add("subsystem", "jpa");
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("JPA Deployments"), response.getFailureDescription());
                }
                else
                {
                    List<JPADeployment> jpaUnits = new ArrayList<JPADeployment>();
                    List<ModelNode> deployments = response.get(RESULT).asList();
                    for(ModelNode deployment : deployments)
                    {
                        ModelNode deploymentValue = deployment.get(RESULT).asObject();
                        if(deploymentValue.hasDefined("hibernate-persistence-unit"))
                        {
                            List<Property> units = deploymentValue.get("hibernate-persistence-unit").asPropertyList();
                            for(Property unit : units)
                            {
                                JPADeployment jpaDeployment = factory.jpaDeployment().as();
                                String[] tokens = unit.getName().split("#");
                                jpaDeployment.setDeploymentName(tokens[0]);
                                jpaDeployment.setPersistenceUnit(tokens[1]);

                                jpaUnits.add(jpaDeployment);
                            }
                        }

                    }

                    getView().setJpaUnits(jpaUnits);
                }



            }
        });
    }
}
