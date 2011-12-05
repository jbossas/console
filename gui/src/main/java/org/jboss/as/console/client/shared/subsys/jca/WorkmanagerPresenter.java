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
import org.jboss.as.console.client.shared.properties.CreatePropertyCmd;
import org.jboss.as.console.client.shared.properties.DeletePropertyCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.builder.ModalWindowLayout;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

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
    private DispatchAsync dispatcher;

    private BeanFactory factory;
    private DefaultWindow window;
    private DefaultWindow propertyWindow;
    private EntityAdapter<WorkmanagerPool> poolAdapter;
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
        this.dispatcher = dispatcher;

        this.factory = factory;
        this.loadWorkManager = new LoadWorkmanagerCmd(dispatcher, metaData);

        this.poolAdapter = new EntityAdapter<WorkmanagerPool>(WorkmanagerPool.class, metaData);
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
                    {
                        getView().setWorkManager(manager);
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void launchNewPropertyDialoge(String reference) {

        propertyWindow = new DefaultWindow("New Pool Property");
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.setWidget(
                new NewPropertyWizard(this, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    public void onCreateProperty(final String poolName, final PropertyRecord prop)
    {
        if(propertyWindow!=null && propertyWindow.isShowing())
        {
            propertyWindow.hide();
        }

        String[] tokens = poolName.split("/");

        ModelNode address = new ModelNode();
        address.set(Baseadress.get());
        address.add("subsystem", "jca");
        address.add("workmanager", tokens[0]);
        address.add(tokens[1], tokens[2]);
        address.add("properties", prop.getKey());

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadWorkManager();
            }
        });
    }

    public void onDeleteProperty(final String poolName, final PropertyRecord prop)
    {
        String[] tokens = poolName.split("/");

        ModelNode address = new ModelNode();
        address.set(Baseadress.get());
        address.add("subsystem", "jca");
        address.add("workmanager", tokens[0]);
        address.add(tokens[1], tokens[2]);
        address.add("properties", prop.getKey());

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher,factory,address);

        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadWorkManager();
            }
        });
    }

    @Override
    public void onChangeProperty(String groupName, PropertyRecord prop) {
        // do nothing
    }

    public void onSavePoolConfig(
            String managerName,
            boolean isShortRunning, String poolName,
            Map<String, Object> changeset)
    {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("workmanager", managerName);

        if(isShortRunning)
            address.get(ADDRESS).add("short-running-threads", poolName);
        else
            address.get(ADDRESS).add("long-running-threads", poolName);

        ModelNode operation = poolAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                    Console.error("Failed to update pool config", response.getFailureDescription());
                else
                    Console.info("Success: Update pool config");

                loadWorkManager();
            }
        });
    }

    public void onRemovePoolConfig(
            String managerName,
            boolean isShortRunning, BoundedQueueThreadPool entity) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("workmanager", managerName);

        if(isShortRunning)
            operation.get(ADDRESS).add("short-running-threads", entity.getName());
        else
            operation.get(ADDRESS).add("long-running-threads", entity.getName());

        operation.get(OP).set(REMOVE);

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                    Console.error("Failed to remove pool config", response.getFailureDescription());
                else
                    Console.info("Success: Removed  pool config");

                loadWorkManager();
            }
        });
    }

    public void launchNewPoolDialoge(String contextName, boolean shortRunning) {
        window = new ModalWindowLayout()
                .setTitle("New Pool Configuration")
                .setWidget(new NewPoolWizard(this, shortRunning).asWidget())
                .build();
    }

    public void createNewPool(WorkmanagerPool pool, boolean shortRunning) {

        closeDialoge();

        ModelNode operation = poolAdapter.fromEntity(pool);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("workmanager", workManagerName);
        operation.get(OP).set(ADD);

        if(shortRunning)
            operation.get(ADDRESS).add("short-running-threads", pool.getName());
        else
            operation.get(ADDRESS).add("long-running-threads", pool.getName());

        System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                    Console.error("Failed to create pool config", response.getFailureDescription());
                else
                    Console.info("Success: Created pool config");

                loadWorkManager();
            }
        });
    }

    public void closeDialoge() {
        window.hide();
    }
}
