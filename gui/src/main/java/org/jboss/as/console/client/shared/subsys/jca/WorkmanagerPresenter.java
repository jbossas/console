package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.shared.EventBus;
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
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class WorkmanagerPresenter
        extends Presenter<WorkmanagerPresenter.MyView, WorkmanagerPresenter.MyProxy>
        implements PropertyManagement {

    private PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;

    private BeanMetaData beanMetaData;
    private BeanFactory factory;
    private DefaultWindow window;
    private EntityAdapter<JcaArchiveValidation> adapter;
    private String workManagerName;

    private LoadWorkmanagerCmd loadWorkManager;

    public PlaceManager getPlaceManager() {
        return this.placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.JcaWorkManager)
    public interface MyProxy extends Proxy<WorkmanagerPresenter>, Place {
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        this.workManagerName = request.getParameter("name", null);

        if(null==workManagerName)
            Console.error("name parameter is required!");
    }

    public interface MyView extends View {
        void setPresenter(WorkmanagerPresenter presenter);
        void setWorkManagerName(String workManagerName);
        void setWorkManager(JcaWorkmanager manager);
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

        this.factory = factory;
        this.loadWorkManager = new LoadWorkmanagerCmd(dispatcher, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadData();
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        getView().setWorkManagerName(this.workManagerName);
    }

    private void loadData() {

        loadWorkManager();
    }

    private void loadWorkManager() {
        loadWorkManager.execute(new SimpleCallback<List<JcaWorkmanager>>() {
            @Override
            public void onSuccess(List<JcaWorkmanager> result) {
                for(JcaWorkmanager manager : result)
                {
                    if(manager.getName().equals(workManagerName))
                        getView().setWorkManager(manager);
                    break;
                }

            }
        });
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
