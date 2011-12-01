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
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaPresenter extends Presenter<JcaPresenter.MyView, JcaPresenter.MyProxy> {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;

    private BeanMetaData beanMetaData;
    private BeanFactory factory;
    private DefaultWindow window;

    private EntityAdapter<JcaWorkmanager> workManagerAdapter;
    private EntityAdapter<BoundedQueueThreadPool> poolAdapter;
    private EntityAdapter<JcaBeanValidation> beanAdapter;
    private EntityAdapter<JcaArchiveValidation> archiveAdapter;
    private EntityAdapter<JcaConnectionManager> ccmAdapter;

    private LoadWorkmanagerCmd loadWorkManager;

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.JcaPresenter)
    public interface MyProxy extends Proxy<JcaPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(JcaPresenter presenter);
        void setWorkManagers(List<JcaWorkmanager> managers);
        void setBeanSettings(JcaBeanValidation jcaBeanValidation);
        void setArchiveSettings(JcaArchiveValidation jcaArchiveValidation);
        void setCCMSettings(JcaConnectionManager jcaConnectionManager);
    }

    @Inject
    public JcaPresenter(
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

        this.beanMetaData = metaData.getBeanMetaData(JcaWorkmanager.class);
        this.workManagerAdapter = new EntityAdapter<JcaWorkmanager>(JcaWorkmanager.class, metaData);
        this.poolAdapter = new EntityAdapter<BoundedQueueThreadPool>(BoundedQueueThreadPool.class, metaData);

        this.beanAdapter = new EntityAdapter<JcaBeanValidation>(JcaBeanValidation.class, metaData);
        this.archiveAdapter = new EntityAdapter<JcaArchiveValidation>(JcaArchiveValidation.class, metaData);
        this.ccmAdapter = new EntityAdapter<JcaConnectionManager>(JcaConnectionManager.class, metaData);

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

    private void loadData() {

        loadJcaSubsystem();
        loadWorkManager();
    }

    private void loadJcaSubsystem() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        ModelNode archive = new ModelNode();
        archive.get(ADDRESS).set(Baseadress.get());
        archive.get(ADDRESS).add("subsystem", "jca");
        archive.get(ADDRESS).add("archive-validation", "archive-validation");
        archive.get(OP).set(READ_RESOURCE_OPERATION);

        ModelNode bean = new ModelNode();
        bean.get(ADDRESS).set(Baseadress.get());
        bean.get(ADDRESS).add("subsystem", "jca");
        bean.get(ADDRESS).add("bean-validation", "bean-validation");
        bean.get(OP).set(READ_RESOURCE_OPERATION);

        ModelNode ccm = new ModelNode();
        ccm.get(ADDRESS).set(Baseadress.get());
        ccm.get(ADDRESS).add("subsystem", "jca");
        ccm.get(ADDRESS).add("cached-connection-manager", "cached-connection-manager");
        ccm.get(OP).set(READ_RESOURCE_OPERATION);

        List<ModelNode> steps = new ArrayList<ModelNode>(3);
        steps.add(archive);
        steps.add(bean);
        steps.add(ccm);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<Property> steps = response.get(RESULT).asPropertyList();

                JcaArchiveValidation jcaArchiveValidation = archiveAdapter.fromDMR(steps.get(0).getValue());
                JcaBeanValidation jcaBeanValidation = beanAdapter.fromDMR(steps.get(1).getValue());
                JcaConnectionManager jcaConnectionManager = ccmAdapter.fromDMR(steps.get(2).getValue());

                getView().setArchiveSettings(jcaArchiveValidation);
                getView().setBeanSettings(jcaBeanValidation);
                getView().setCCMSettings(jcaConnectionManager);
            }
        });
    }

    private void loadWorkManager() {
        loadWorkManager.execute(new SimpleCallback<List<JcaWorkmanager>>() {
            @Override
            public void onSuccess(List<JcaWorkmanager> result) {
                getView().setWorkManagers(result);
            }
        });
    }



    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
}
