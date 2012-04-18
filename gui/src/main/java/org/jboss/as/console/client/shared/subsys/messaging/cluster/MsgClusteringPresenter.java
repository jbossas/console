package org.jboss.as.console.client.shared.subsys.messaging.cluster;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.messaging.CommonMsgPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.LoadHornetQServersCmd;
import org.jboss.as.console.client.shared.subsys.messaging.LoadJMSCmd;
import org.jboss.as.console.client.shared.subsys.messaging.model.Bridge;
import org.jboss.as.console.client.shared.subsys.messaging.model.BroadcastGroup;
import org.jboss.as.console.client.shared.subsys.messaging.model.ClusterConnection;
import org.jboss.as.console.client.shared.subsys.messaging.model.DiscoveryGroup;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.Subsystem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

/**
 * @author Heiko Braun
 * @date 4/18/12
 */
public class MsgClusteringPresenter
        extends Presenter<MsgClusteringPresenter.MyView, MsgClusteringPresenter.MyProxy>
        implements CommonMsgPresenter {

    private final PlaceManager placeManager;

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private MessagingProvider providerEntity;
    private DefaultWindow window = null;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private String currentServer = null;
    private EntityAdapter<BroadcastGroup> bcastGroupAdapter;
    private EntityAdapter<DiscoveryGroup> discGroupAdapter;
    private EntityAdapter<ClusterConnection> clusterGroupAdapter;

    private LoadJMSCmd loadJMSCmd;
    private DefaultWindow propertyWindow;



    @ProxyCodeSplit
    @NameToken(NameTokens.MsgClusteringPresenter)
    @Subsystem(name="Clustering", group = "Messaging", key="messaging")
    public interface MyProxy extends Proxy<MsgClusteringPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(MsgClusteringPresenter presenter);

        void setProvider(List<String> result);

        void setSelectedProvider(String currentServer);

        void setBroadcastGroups(List<BroadcastGroup> groups);
    }

    @Inject
    public MsgClusteringPresenter( EventBus eventBus, MyView view, MyProxy proxy,
                                   PlaceManager placeManager, DispatchAsync dispatcher,
                                   BeanFactory factory, RevealStrategy revealStrategy,
                                   ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;

        bcastGroupAdapter = new EntityAdapter<BroadcastGroup>(BroadcastGroup.class, metaData);
        discGroupAdapter = new EntityAdapter<DiscoveryGroup>(DiscoveryGroup.class, metaData);
        clusterGroupAdapter = new EntityAdapter<ClusterConnection>(ClusterConnection.class, metaData);

        loadJMSCmd = new LoadJMSCmd(dispatcher, factory, metaData);
    }

    @Override
    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    private void loadProvider() {
        new LoadHornetQServersCmd(dispatcher).execute(
                new AsyncCallback<List<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Console.error("Failed to load messaging server names", caught.getMessage());
                    }

                    @Override
                    public void onSuccess(List<String> result) {

                        getView().setProvider(result);
                        getView().setSelectedProvider(currentServer);
                    }
                }
        );

    }

    @Override
    protected void onReset() {
        super.onReset();
        loadProvider();
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        currentServer = request.getParameter("name", null);
    }


    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void loadDetails(String selectedProvider) {
        loadBroadcastGroups();
    }

    private void loadBroadcastGroups() {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "messaging");
        operation.get(ADDRESS).add("hornetq-server", getCurrentServer());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("broadcast-group");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Loading broadcast groups " + getCurrentServer()), response.getFailureDescription());
                }
                else
                {
                    List<Property> model = response.get(RESULT).asPropertyList();
                    List<BroadcastGroup> groups = new ArrayList<BroadcastGroup>();
                    for(Property prop : model)
                    {
                        ModelNode node = prop.getValue();
                        BroadcastGroup entity = bcastGroupAdapter.fromDMR(node);
                        entity.setName(prop.getName());

                        entity.setConnectors(EntityAdapter.modelToList(node, "connectors"));
                        groups.add(entity);
                    }

                    getView().setBroadcastGroups(groups);

                }
            }
        });
    }

    public void saveBroadcastGroup(final String name, Map<String, Object> changeset) {

        ModelNode address = Baseadress.get();
        address.add("subsystem", "messaging");
        address.add("hornetq-server", getCurrentServer());
        address.add("broadcast-group", name);

        ModelNode addressNode = new ModelNode();
        addressNode.get(ADDRESS).set(address);

        ModelNode extra = null;
        List<String> items = (List<String>)changeset.get("connectors");
        if(items!=null)
        {
            extra = new ModelNode();
            extra.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            extra.get(NAME).set("connectors");
            extra.get(ADDRESS).set(address);
            extra.get(VALUE).setEmptyList();
            for(String item : items)
                extra.get(VALUE).add(item);

        }

        ModelNode operation = extra!=null ?
                bcastGroupAdapter.fromChangeset(changeset, addressNode, extra) :
                bcastGroupAdapter.fromChangeset(changeset, addressNode);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Broadcast Group " + name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Broadcast Group " + name));

                loadBroadcastGroups();
            }
        });
    }

    public void launchNewBroadcastGroupWizard() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void onDeleteBroadcastGroup(String name) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void loadExistingSocketBindings(AsyncCallback<List<String>> asyncCallback) {

    }
}
