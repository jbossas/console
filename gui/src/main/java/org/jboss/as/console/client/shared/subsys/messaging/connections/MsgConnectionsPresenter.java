package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.messaging.CommonMsgPresenter;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.spi.Subsystem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * @author Heiko Braun
 * @date 4/2/12
 */
public class MsgConnectionsPresenter extends Presenter<MsgConnectionsPresenter.MyView, MsgConnectionsPresenter.MyProxy>
    implements CommonMsgPresenter {


    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private MessagingProvider providerEntity;
    private DefaultWindow window = null;
    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;

    @Override
    public PlaceManager getPlaceManager() {
        return placeManager;
    }



    @ProxyCodeSplit
    @NameToken(NameTokens.MsgConnectionsPresenter)
    @Subsystem(name="Connections", group = "Messaging", key="messaging")
    public interface MyProxy extends Proxy<MsgConnectionsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(MsgConnectionsPresenter presenter);
        void setSelectedProvider(String selectedProvider);
    }

    @Inject
    public MsgConnectionsPresenter( EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
