package org.jboss.as.console.client.shared.subsys.jca;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DataSourcePresenter extends Presenter<DataSourcePresenter.MyView, DataSourcePresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourcePresenter)
    public interface MyProxy extends Proxy<DataSourcePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(DataSourcePresenter presenter);

        void updateDataSources(List<DataSource> datasources);
    }

    @Inject
    public DataSourcePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
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
        loadDataSources();
    }

    @Override
    protected void revealInParent() {
         RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    void loadDataSources() {

        // /profile=default/subsystem=datasources:read-children-resources(child-type=data-source)

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", "default"); // TODO: selected profile
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(CHILD_TYPE).set("data-source");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to load datasource", caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<DataSource> datasources = new ArrayList<DataSource>(payload.size());
                for(ModelNode item : payload)
                {
                    // returned as type property (key=ds name)
                    Property property = item.asProperty();
                    ModelNode ds = property.getValue().asObject();
                    String name = property.getName();
                    //System.out.println(ds.toJSONString(false));

                    try {
                        DataSource model = factory.dataSource().as();
                        model.setName(name);
                        model.setConnectionUrl(ds.get("connection-url").asString());
                        model.setJndiName(ds.get("jndi-name").asString());
                        model.setDriverClass(ds.get("driver-class").asString());
                        model.setEnabled(ds.get("enabled").asBoolean());
                        model.setUsername(ds.get("user-name").asString());
                        model.setPassword(ds.get("password").asString());

                        datasources.add(model);

                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                // finally update view
                getView().updateDataSources(datasources);
            }
        });

    }
}
