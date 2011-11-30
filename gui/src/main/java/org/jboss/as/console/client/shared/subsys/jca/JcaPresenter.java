package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaPresenter extends Presenter<JcaPresenter.MyView, JcaPresenter.MyProxy> {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;

    private BeanMetaData beanMetaData;
    private BeanFactory factory;
    private DefaultWindow window;

    private EntityAdapter<JcaWorkmanager> workManagerAdapter;
    private EntityAdapter<BoundedQueueThreadPool> poolAdapter;

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.JcaPresenter)
    public interface MyProxy extends Proxy<JcaPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JcaPresenter presenter);

        void setWorkManagers(List<JcaWorkmanager> managers);
    }

    @Inject
    public JcaPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            ApplicationMetaData metaData, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;

        this.revealStrategy = revealStrategy;
        this.metaData = metaData;
        this.dispatcher = dispatcher;

        this.beanMetaData = metaData.getBeanMetaData(JcaWorkmanager.class);
        this.workManagerAdapter = new EntityAdapter<JcaWorkmanager>(JcaWorkmanager.class, metaData);
        this.poolAdapter = new EntityAdapter<BoundedQueueThreadPool>(BoundedQueueThreadPool.class, metaData);

        this.factory = factory;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadData();
    }

     private void loadData() {

        loadWorkManager();
    }

    private void loadWorkManager() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(CHILD_TYPE).set("workmanager");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<Property> children = response.get(RESULT).asPropertyList();
                List<JcaWorkmanager> managers = new ArrayList<JcaWorkmanager>(children.size());

                for(Property child : children)
                {
                    ModelNode value = child.getValue();
                    System.out.println(value);

                    JcaWorkmanager entity = workManagerAdapter.fromDMR(value);

                    if(value.hasDefined("long-running-threads"))
                    {
                        List<BoundedQueueThreadPool> pools = parseThreadPool(value.get("long-running-threads").asPropertyList());
                        entity.setLongRunning(pools);
                    }

                     if(value.hasDefined("short-running-threads"))
                    {
                        List<BoundedQueueThreadPool> pools = parseThreadPool(value.get("short-running-threads").asPropertyList());
                        entity.setShortRunning(pools);
                    }

                    managers.add(entity);

                }

                getView().setWorkManagers(managers);
            }
        });
    }

    private List<BoundedQueueThreadPool> parseThreadPool(List<Property> values) {
        List<BoundedQueueThreadPool> result = new ArrayList<BoundedQueueThreadPool>();

        for(Property value : values)
        {
            result.add(poolAdapter.fromDMR(value.getValue()));
        }

        return result;
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadSubsystem() {

        ModelNode operation = beanMetaData.getAddress().asResource(Baseadress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to load EE subsystem", response.get("failed-description").asString());
                }
                else
                {
                    ModelNode payload = response.get(RESULT).asObject();
                    // TDOD
                }
            }
        });
    }
}
