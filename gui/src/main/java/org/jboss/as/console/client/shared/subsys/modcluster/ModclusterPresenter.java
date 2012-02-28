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
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.shared.subsys.modcluster.model.SSLConfig;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Pavel Slegr
 * @date 02/21/12
 */
public class ModclusterPresenter extends Presenter<ModclusterPresenter.MyView, ModclusterPresenter.MyProxy>
    implements ModclusterManagement {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<Modcluster> adapter;
    private BeanMetaData beanMetaData;
    private DefaultWindow window;
    private EntityAdapter<SSLConfig> sslAdapter;
    private BeanFactory factory;

    public void onSaveSsl(SSLConfig editedEntity, Map<String, Object> changeset) {
        //To change body of created methods use File | Settings | File Templates.
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.ModclusterPresenter)
    public interface MyProxy extends Proxy<ModclusterPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(ModclusterPresenter presenter);
        void updateFrom(Modcluster modcluster);
    }

    @Inject
    public ModclusterPresenter(
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
        this.beanMetaData = metaData.getBeanMetaData(Modcluster.class);
        this.adapter = new EntityAdapter<Modcluster>(Modcluster.class, metaData);
        this.sslAdapter = new EntityAdapter<SSLConfig>(SSLConfig.class, metaData);
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
        loadModcluster();
    }

    private void loadModcluster() {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "modcluster");
        operation.get(ADDRESS).add("mod-cluster-config", "configuration");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error("Failed to load Modcluster subsystem", response.getFailureDescription());
                }
                else
                {
                    ModelNode payload = response.get(RESULT).asObject();

                    Modcluster modcluster = adapter.fromDMR(payload);

                    if(payload.hasDefined("ssl"))
                    {
                        SSLConfig ssl = sslAdapter.fromDMR(payload.get("ssl").asObject());
                        modcluster.setSSLConfig(ssl);
                    }
                    else
                    {
                        // provide an empty entity
                        modcluster.setSSLConfig(factory.SSLConfig().as());
                    }

                    getView().updateFrom(modcluster);
                }

            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onSave(final Modcluster editedEntity, Map<String, Object> changeset) {

        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "modcluster");
        address.get(ADDRESS).add("mod-cluster-config", "configuration");

        ModelNode operation = adapter.fromChangeset(changeset, address);

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
