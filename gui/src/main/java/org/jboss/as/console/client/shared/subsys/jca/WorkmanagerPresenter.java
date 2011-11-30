package org.jboss.as.console.client.shared.subsys.jca;

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
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class WorkmanagerPresenter
        extends Presenter<WorkmanagerPresenter.MyView, WorkmanagerPresenter.MyProxy>
        implements PropertyManagement {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;

    private BeanMetaData beanMetaData;
    private BeanFactory factory;
    private DefaultWindow window;
    private EntityAdapter<JcaArchiveValidation> adapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.JcaWorkManager)
    public interface MyProxy extends Proxy<WorkmanagerPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(WorkmanagerPresenter presenter);
    }

    @Inject
    public WorkmanagerPresenter(
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

        this.beanMetaData = metaData.getBeanMetaData(JcaArchiveValidation.class);
        this.adapter = new EntityAdapter<JcaArchiveValidation>(JcaArchiveValidation.class, metaData);

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
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closePropertyDialoge() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
