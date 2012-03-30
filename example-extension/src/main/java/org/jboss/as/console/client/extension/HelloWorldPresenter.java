package org.jboss.as.console.client.extension;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.extension.model.DataModel;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.spi.Subsystem;

/**
 * @author Heiko Braun
 * @date 3/29/12
 */
public class HelloWorldPresenter extends Presenter<HelloWorldPresenter.MyView, HelloWorldPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private RevealStrategy revealStrategy;

    @ProxyCodeSplit
    @NameToken("helloworld")
    @Subsystem(name="HelloWorld", group = "Examples", key="logging")
    public interface MyProxy extends Proxy<HelloWorldPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HelloWorldPresenter presenter);
        void setData(DataModel data);
    }

    @Inject
    public HelloWorldPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, BeanFactory factory,
            RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();


        // populate data model, i.e. from remote call
        DataModel model = factory.getDataModel().as();
        model.setGreeting("Hello World");

        // update view
        getView().setData(model);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
