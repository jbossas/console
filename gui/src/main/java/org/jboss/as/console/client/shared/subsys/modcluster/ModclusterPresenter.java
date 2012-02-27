package org.jboss.as.console.client.shared.subsys.modcluster;

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
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Pavel Slegr
 * @date 02/21/12
 */
public class ModclusterPresenter extends Presenter<ModclusterPresenter.MyView, ModclusterPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<Modcluster> adapter;
    private BeanMetaData beanMetaData;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.ModclusterPresenter)
    public interface MyProxy extends Proxy<ModclusterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ModclusterPresenter presenter);
        void updateFrom(List<Modcluster> list);
    }

    @Inject
    public ModclusterPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            ApplicationMetaData metaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;

        this.revealStrategy = revealStrategy;
        this.metaData = metaData;
        this.dispatcher = dispatcher;
        this.beanMetaData = metaData.getBeanMetaData(Modcluster.class);
        this.adapter = new EntityAdapter<Modcluster>(Modcluster.class, metaData);

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadModcluster();
    }

    public void launchNewSessionWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Modcluster"));
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
                new NewModclusterWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    private void loadModcluster() {

        ModelNode operation = beanMetaData.getAddress().asSubresource(Baseadress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);
        System.out.println("Operation JSON: " + operation.toJSONString());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error("Failed to load Modcluster");
                }
                else
                {
                    List<Property> items = response.get(RESULT).asPropertyList();
                    List<Modcluster> modclusterList = new ArrayList<Modcluster>(items.size());
                    for(Property item : items)
                    {
                        ModelNode model = item.getValue();
                        System.out.println("Model: " + model.toString());
                        Modcluster modcluster = adapter.fromDMR(model.get("configuration").asObject());
                        modclusterList.add(modcluster);
//                        System.out.println("Modcluster value advertise-socket: " + modcluster.getAdvertiseSocket());
//                        System.out.println("Modcluster value excluded-contaxt: " + modcluster.getExcludedContexts());
//                        System.out.println("Modcluster value balancer: " + modcluster.getBalancer());
                    }

                    getView().updateFrom(modclusterList);
                }

            }
            @Override
            public void onFailure(Throwable caught) {
            	// TODO Auto-generated method stub
            	super.onFailure(caught);
            	System.err.println(caught);
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void closeDialoge() {
        window.hide();
    }


    public void onCreateModcluster(final Modcluster entity) {

        closeDialoge();

        ModelNode address = beanMetaData.getAddress().asResource(Baseadress.get(), "configuration");

        ModelNode operation = adapter.fromEntity(entity);
        operation.get(ADDRESS).set(address.get(ADDRESS));
        operation.get(OP).set(ADD);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                System.out.println("Response: " + response.toString());

                if(response.isFailure())
                {
                    Console.error("Failed to create modcluster");
                }
                else
                {
                    Console.info("Success: Added modcluster "+" record");
                }

                loadModcluster();
            }
        });
    }

    public void onDelete(final Modcluster entity) {
        ModelNode operation = beanMetaData.getAddress().asResource(Baseadress.get(), "configuration");
        operation.get(OP).set(REMOVE);
        System.out.println(operation);
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                System.out.println("Response: " + response.toString());

                if(response.isFailure())
                {
                    Console.error("Failed to remove modcluster", response.get("failure-description").asString());
                }
                else
                {
                    Console.info("Success: Removed modcluster record");
                }

                loadModcluster();
            }
        });
    }

    public void onSave(final Modcluster editedEntity, Map<String, Object> changeset) {
        ModelNode operation = adapter.fromChangeset(changeset, beanMetaData.getAddress().asResource(Baseadress.get(), "configuration"));
        System.out.println(operation);
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                System.out.println("Response: " + response.toString());

                if(response.isFailure())
                {
                    Console.error("Failed to update modcluster subsystem");
                }
                else
                {
                    Console.info("Success: Update modcluster record");
                }

                loadModcluster();
            }
        });
    }
}
