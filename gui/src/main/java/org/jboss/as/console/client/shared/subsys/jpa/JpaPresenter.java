package org.jboss.as.console.client.shared.subsys.jpa;

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
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jpa.model.JpaSubsystem;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class JpaPresenter extends Presenter<JpaPresenter.MyView, JpaPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<JpaSubsystem> adapter;
    private BeanMetaData beanMetaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.JpaPresenter)
    public interface MyProxy extends Proxy<JpaPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JpaPresenter presenter);

        void updateFrom(JpaSubsystem jpaSubsystem);
    }

    @Inject
    public JpaPresenter(
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

        this.beanMetaData = metaData.getBeanMetaData(JpaSubsystem.class);

        this.adapter = new EntityAdapter<JpaSubsystem>(
                JpaSubsystem.class, metaData);
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

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to load JPA subsystem");
                }
                else
                {
                    JpaSubsystem jpaSubsystem = adapter.fromDMR(response);
                    getView().updateFrom(jpaSubsystem);
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onSave(JpaSubsystem editedEntity, Map<String, Object> changeset) {

        System.out.println(changeset);

        ModelNode operation = adapter.fromChangeset(changeset, beanMetaData.getAddress().asResource());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to update JPA subsystem");
                }
                else
                {
                    Console.info("Success: Update JPA subsystem");

                    JpaSubsystem jpaSubsystem = adapter.fromDMR(response);
                    getView().updateFrom(jpaSubsystem);
                }
            }
        });
    }
}
