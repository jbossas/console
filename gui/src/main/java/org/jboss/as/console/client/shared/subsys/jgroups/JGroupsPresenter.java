package org.jboss.as.console.client.shared.subsys.jgroups;

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
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jmx.model.JMXSubsystem;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class JGroupsPresenter extends Presenter<JGroupsPresenter.MyView, JGroupsPresenter.MyProxy> {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;
    private EntityAdapter<JMXSubsystem> adapter;
    private BeanMetaData beanMetaData;


    @ProxyCodeSplit
    @NameToken(NameTokens.JGroupsPresenter)
    public interface MyProxy extends Proxy<JGroupsPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JGroupsPresenter presenter);
    }

    @Inject
    public JGroupsPresenter(
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
        this.beanMetaData = metaData.getBeanMetaData(JMXSubsystem.class);
        this.adapter = new EntityAdapter<JMXSubsystem>(JMXSubsystem.class, metaData);
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
