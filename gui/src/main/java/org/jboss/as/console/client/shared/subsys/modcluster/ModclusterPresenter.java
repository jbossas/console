package org.jboss.as.console.client.shared.subsys.modcluster;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Pavel Slegr
 * @author Heiko Braun
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
                    Console.error(Console.MESSAGES.failed("Modcluster Subsystem"), response.getFailureDescription());
                }
                else
                {
                    ModelNode payload = response.get(RESULT).asObject();

                    Modcluster modcluster = adapter.fromDMR(payload);

                    if(payload.hasDefined("ssl") && payload.get("ssl").hasDefined("configuration"))
                    {
                        SSLConfig ssl = sslAdapter.fromDMR(payload.get("ssl").get("configuration").asObject());
                        modcluster.setSSLConfig(ssl);
                    }
                    else
                    {
                        // provide an empty entity
                        AutoBean<SSLConfig> autoBean = factory.SSLConfig();
                        autoBean.setTag("state", "transient");
                        modcluster.setSSLConfig(autoBean.as());
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

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("Modcluster Subsystem"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("Modcluster Subsystem"));
                }

                loadModcluster();
            }
        });
    }

    public void onSaveSsl(SSLConfig entity, Map<String, Object> changeset) {

        // TODO: https://issues.jboss.org/browse/AS7-3933

        // check transient: requires creation of ssl subresource. otherwise we can simply update it
        boolean isTransient = false;
        String state = (String)AutoBeanUtils.getAutoBean(entity).getTag("state");
        if(state!=null && state.equals("transient"))
            isTransient = true;

        ModelNode composite = new ModelNode();
        composite.get(ADDRESS).setEmptyList();
        composite.set(OP).set(COMPOSITE);

        List<ModelNode> steps  = new ArrayList<ModelNode>();

        // the create op, if necessary
        if(isTransient)
        {
            ModelNode createOp = new ModelNode();
            createOp.get(ADDRESS).set(Baseadress.get());
            createOp.get(ADDRESS).add("subsystem", "modcluster");
            createOp.get(ADDRESS).add("mod-cluster-config", "configuration");
            createOp.get(ADDRESS).add("ssl", "configuration");
            createOp.get(OP).set(ADD);

            steps.add(createOp);
        }

        // the updated values

        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "modcluster");
        address.get(ADDRESS).add("mod-cluster-config", "configuration");
        address.get(ADDRESS).add("ssl", "configuration");

        ModelNode updateOp = sslAdapter.fromChangeset(changeset, address);
        steps.add(updateOp);

        // provide steps
        composite.set(STEPS).set(steps);

        System.out.println(composite);

        dispatcher.execute(new DMRAction(composite), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("SSL Config"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("SSL Config"));
                }

                loadModcluster();
            }
        });
    }

}
