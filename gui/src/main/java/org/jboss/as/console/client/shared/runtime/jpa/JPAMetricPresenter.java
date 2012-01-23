package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private PlaceManager placeManager;
    private String[] selectedUnit;
    private EntityAdapter<JPADeployment> adapter;

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.JPAMetricPresenter)
    public interface MyProxy extends Proxy<JPAMetricPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JPAMetricPresenter presenter);
        void setJpaUnits(List<JPADeployment> jpaUnits);
        void setSelectedUnit(String[] strings);
        void updateMetric(UnitMetric unitMetric);
    }

    @Inject
    public JPAMetricPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher,
            ApplicationMetaData metaData, RevealStrategy revealStrategy,
            CurrentServerSelection serverSelection, BeanFactory factory, PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.placeManager = placeManager;
        this.serverSelection = serverSelection;
        this.factory = factory;


        adapter = new EntityAdapter<JPADeployment>(JPADeployment.class, metaData);

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {


        String dpl = request.getParameter("dpl", null);
        if(dpl!=null) {
            this.selectedUnit = new String[] {
                    dpl,
                    request.getParameter("unit", null)
            };
        }
        else
        {
            this.selectedUnit = null;
        }

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
        operation.get(ADDRESS).add("hibernate-persistence-unit", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

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

                        List<Property> addressValue = deployment.get(ADDRESS).asPropertyList();

                        Property unit = addressValue.get(2);
                        JPADeployment jpaDeployment = factory.jpaDeployment().as();
                        String[] tokens = unit.getValue().asString().split("#");
                        jpaDeployment.setDeploymentName(tokens[0]);
                        jpaDeployment.setPersistenceUnit(tokens[1]);
                        jpaDeployment.setMetricEnabled(deploymentValue.get("enabled").asBoolean());

                        jpaUnits.add(jpaDeployment);

                    }

                    getView().setJpaUnits(jpaUnits);
                }


                // update selection (paging)
                getView().setSelectedUnit(selectedUnit);


            }
        });
    }

    public void loadMetrics(String[] tokens) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        operation.get(ADDRESS).add("deployment", tokens[0]);
        operation.get(ADDRESS).add("subsystem", "jpa");
        operation.get(ADDRESS).add("hibernate-persistence-unit", tokens[0]+"#"+tokens[1]);

        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(
                            Console.MESSAGES.failed("JPA Metrics"),
                            response.getFailureDescription()
                    );
                }
                else
                {

                    ModelNode payload  = response.get(RESULT).asObject();

                    boolean isEnabled = payload.get("enabled").asBoolean();

                    if(!isEnabled)
                    {

                        getView().updateMetric(
                                new UnitMetric(false)
                        );
                    }
                    else
                    {

                        Metric txMetric = new Metric(
                                payload.get("completed-transaction-count").asLong(),
                                payload.get("successful-transaction-count").asLong()
                        );

                        //  ----

                        Metric queryExecMetric = new Metric(
                                payload.get("query-execution-count").asLong(),
                                payload.get("query-execution-max-time").asLong()
                        );

                        queryExecMetric.add(
                                payload.get("query-execution-max-time-query-string").asString()
                        );


                        //  ----

                        Metric queryCacheMetric = new Metric(
                                payload.get("query-cache-put-count").asLong(),
                                payload.get("query-cache-hit-count").asLong(),
                                payload.get("query-cache-miss-count").asLong()
                        );

                        //  ----

                        Metric secondLevelCacheMetric = new Metric(
                                payload.get("second-level-cache-put-count").asLong(),
                                payload.get("second-level-cache-hit-count").asLong(),
                                payload.get("second-level-cache-miss-count").asLong()
                        );

                        getView().updateMetric(
                                new UnitMetric(
                                        txMetric,
                                        queryCacheMetric, queryExecMetric,
                                        secondLevelCacheMetric
                                )
                        );

                    }

                }

            }
        });
    }

    public void onSaveJPADeployment(JPADeployment editedEntity, Map<String, Object> changeset) {

        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(RuntimeBaseAddress.get());
        address.get(ADDRESS).add("deployment", editedEntity.getDeploymentName());
        address.get(ADDRESS).add("subsystem", "jpa");
        address.get(ADDRESS).add("hibernate-persistence-unit", editedEntity.getDeploymentName()+"#"+editedEntity.getPersistenceUnit());

        ModelNode operation = adapter.fromChangeset(changeset, address);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(
                            Console.MESSAGES.modificationFailed("JPA Deployment"),
                            response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("JPA Deployment"));

                    refresh();
                }
            }
        });
    }
}
