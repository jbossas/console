package org.jboss.as.console.client.shared.subsys.ejb3;

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
import org.jboss.as.console.client.shared.subsys.ejb3.model.EESubsystem;
import org.jboss.as.console.client.shared.subsys.ejb3.model.Module;
import org.jboss.as.console.client.shared.subsys.jmx.model.JMXSubsystem;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class EEPresenter extends Presenter<EEPresenter.MyView, EEPresenter.MyProxy> {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<EESubsystem> adapter;
    private BeanMetaData beanMetaData;
    private BeanFactory factory;

    @ProxyCodeSplit
    @NameToken(NameTokens.EEPresenter)
    public interface MyProxy extends Proxy<EEPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(EEPresenter presenter);
        void updateFrom(EESubsystem eeSubsystem);
    }

    @Inject
    public EEPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            RevealStrategy revealStrategy,
            ApplicationMetaData metaData, BeanFactory factory
    ) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.revealStrategy = revealStrategy;
        this.metaData = metaData;
        this.dispatcher = dispatcher;
        this.beanMetaData = metaData.getBeanMetaData(EESubsystem.class);
        this.adapter = new EntityAdapter<EESubsystem>(EESubsystem.class, metaData);
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
        loadSubsystem();
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
                    EESubsystem eeSubsystem = adapter.fromDMR(payload);

                    if(payload.hasDefined("global-modules"))
                    {
                        List<ModelNode> modelNodes = payload.get("global-modules").asList();
                        List<Module> modules = new ArrayList<Module>(modelNodes.size());
                        for(ModelNode model : modelNodes)
                        {
                            Module module = factory.eeModuleRef().as();
                            module.setName(model.get("name").asString());
                            module.setSlot(model.get("slot").asString());

                            modules.add(module);
                        }

                        eeSubsystem.setModules(modules);

                    }
                    getView().updateFrom(eeSubsystem);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onSave(final EESubsystem editedEntity, Map<String, Object> changeset) {
        ModelNode operation = adapter.fromChangeset(changeset, beanMetaData.getAddress().asResource());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to update EE subsystem");
                }
                else
                {
                    Console.info("Success: Update EE subsystem");
                }

                loadSubsystem();
            }
        });
    }
}
