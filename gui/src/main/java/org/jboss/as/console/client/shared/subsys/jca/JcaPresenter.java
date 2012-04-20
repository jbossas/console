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
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.subsys.jca.model.WorkmanagerPool;
import org.jboss.as.console.client.shared.viewframework.builder.ModalWindowLayout;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaPresenter extends Presenter<JcaPresenter.MyView, JcaPresenter.MyProxy>
    implements PropertyManagement {

    private final PlaceManager placeManager;

    private RevealStrategy revealStrategy;
    private ApplicationMetaData metaData;
    private DispatchAsync dispatcher;

    private BeanMetaData beanMetaData;
    private BeanFactory factory;

    private EntityAdapter<JcaBootstrapContext> boostrapAdapter;
    private EntityAdapter<JcaBeanValidation> beanAdapter;
    private EntityAdapter<JcaArchiveValidation> archiveAdapter;
    private EntityAdapter<JcaConnectionManager> ccmAdapter;
    private EntityAdapter<JcaWorkmanager> managerAdapter;

    private LoadWorkmanagerCmd loadWorkManager;
    private DefaultWindow window;
    private DefaultWindow propertyWindow;
    private List<JcaWorkmanager> managers;
    private EntityAdapter<WorkmanagerPool> poolAdapter;
    private String selectedWorkmanager;


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
        void setBootstrapContexts(List<JcaBootstrapContext> contexts);

        void setSelectedWorkmanager(String selectedWorkmanager);
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
        this.boostrapAdapter = new EntityAdapter<JcaBootstrapContext>(JcaBootstrapContext.class, metaData);

        this.managerAdapter= new EntityAdapter<JcaWorkmanager>(JcaWorkmanager.class, metaData);
        this.beanAdapter = new EntityAdapter<JcaBeanValidation>(JcaBeanValidation.class, metaData);
        this.archiveAdapter = new EntityAdapter<JcaArchiveValidation>(JcaArchiveValidation.class, metaData);
        this.ccmAdapter = new EntityAdapter<JcaConnectionManager>(JcaConnectionManager.class, metaData);

        this.poolAdapter = new EntityAdapter<WorkmanagerPool>(WorkmanagerPool.class, metaData);

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
        loadJcaSubsystem();
        loadWorkManager(true);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        this.selectedWorkmanager = request.getParameter("name", null);

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
                ModelNode response = result.get();

                List<Property> steps = response.get(RESULT).asPropertyList();

                JcaArchiveValidation jcaArchiveValidation = archiveAdapter.fromDMR(
                        steps.get(0).getValue().get(RESULT).asObject()
                );
                JcaBeanValidation jcaBeanValidation = beanAdapter.fromDMR(
                        steps.get(1).getValue().get(RESULT).asObject()
                );
                JcaConnectionManager jcaConnectionManager = ccmAdapter.fromDMR(
                        steps.get(2).getValue().get(RESULT).asObject()
                );

                getView().setArchiveSettings(jcaArchiveValidation);
                getView().setBeanSettings(jcaBeanValidation);
                getView().setCCMSettings(jcaConnectionManager);
            }
        });
    }

    private void loadWorkManager() {
        loadWorkManager(false);
    }

    private void loadWorkManager(final boolean retainSelection) {
        loadWorkManager.execute(new SimpleCallback<List<JcaWorkmanager>>() {
            @Override
            public void onSuccess(List<JcaWorkmanager> result) {

                JcaPresenter.this.managers = result;

                getView().setWorkManagers(result);

                // TODO: should only be invoked when called from onReset()
                if(retainSelection)
                    getView().setSelectedWorkmanager(selectedWorkmanager);
                else
                    getView().setSelectedWorkmanager(null);

                loadBootstrapContexts();
            }
        });
    }

    private void loadBootstrapContexts() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(CHILD_TYPE).set("bootstrap-context");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                List<Property> children = response.get(RESULT).asPropertyList();
                List<JcaBootstrapContext> contexts = new ArrayList<JcaBootstrapContext>(children.size());

                for(Property child : children)
                {
                    ModelNode value = child.getValue();
                    JcaBootstrapContext entity = boostrapAdapter.fromDMR(value);
                    contexts.add(entity);

                }

                getView().setBootstrapContexts(contexts);

            }
        });

    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    public void onSaveArchiveSettings(Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("archive-validation", "archive-validation");
        ModelNode operation = archiveAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error("Failed to update JCA settings", response.getFailureDescription());
                else
                    Console.info("Success: Update JCA settings");

                loadJcaSubsystem();
            }
        });
    }

    public void onSaveBeanSettings(Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("bean-validation", "bean-validation");
        ModelNode operation = beanAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error("Failed to update JCA settings", response.getFailureDescription());
                else
                    Console.info("Success: Update JCA settings");

                loadJcaSubsystem();
            }
        });
    }

    public void onSaveCCMSettings(Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("cached-connection-manager", "cached-connection-manager");
        ModelNode operation = ccmAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error("Failed to update JCA settings", response.getFailureDescription());
                else
                    Console.info("Success: Update JCA settings");

                loadJcaSubsystem();
            }
        });
    }

    public void onSaveBootstrapContext(final JcaBootstrapContext entity, Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("bootstrap-context", entity.getName());
        ModelNode operation = boostrapAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error("Failed to update JCA settings", response.getFailureDescription());
                else
                    Console.info("Success: Update JCA settings");

                loadWorkManager();
            }
        });
    }

    public void onDeleteBootstrapContext(final JcaBootstrapContext entity) {
        if(entity.getName().equals("default"))
        {
            Console.error(Console.CONSTANTS.subsys_jca_error_context_removal(), Console.CONSTANTS.subsys_jca_error_context_removal_desc());
            return;
        }

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("bootstrap-context", entity.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error("Failed to update JCA settings", response.getFailureDescription());
                else
                    Console.info("Success: Update JCA settings");

                loadWorkManager();
            }
        });
    }

    public void launchNewContextDialogue() {
        window = new ModalWindowLayout()
                .setTitle(Console.MESSAGES.createTitle("Bootstrap Context"))
                .setWidget(new NewContextWizard(this, managers).asWidget())
                .build();
    }

    public void createNewContext(final JcaBootstrapContext entity) {
        closeDialoge();

        ModelNode operation = boostrapAdapter.fromEntity(entity);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("bootstrap-context", entity.getName());
        operation.get(OP).set(ADD);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Bootstrap Context"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Bootstrap Context"));

                loadWorkManager();
            }
        });
    }

    public void closeDialoge() {
        window.hide();
    }

    public void launchNewManagerDialogue() {
        window = new ModalWindowLayout()
                .setTitle(Console.MESSAGES.createTitle("Work Manager"))
                .setWidget(new NewManagerWizard(this).asWidget())
                .build();
    }

    public void onDeleteManager(final JcaWorkmanager entity) {
        if(entity.getName().equals("default"))
        {
            Console.error(Console.CONSTANTS.subsys_jca_error_default_workmanager_deletion());
            return;
        }

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("workmanager", entity.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Work Manager"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Work Manager "+entity.getName() ));

                loadWorkManager();
            }
        });
    }

    public void createNewManager(final JcaWorkmanager entity) {
        closeDialoge();

        if(null==entity.getShortRunning() || entity.getShortRunning().isEmpty())
        {
            // provide a default short running thread pool config (mandatory)
            WorkmanagerPool pool = factory.WorkmanagerPool().as();
            pool.setShortRunning(true);
            //pool.setName("short-running-pool_" + entity.getName());
            pool.setName(entity.getName());
            pool.setMaxThreads(10);
            pool.setQueueLength(10);

            List<WorkmanagerPool> managers = new ArrayList<WorkmanagerPool>(1);
            managers.add(pool);
            entity.setShortRunning(managers);
        }

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);

        ModelNode workmanagerOp = managerAdapter.fromEntity(entity);
        workmanagerOp.get(ADDRESS).set(Baseadress.get());
        workmanagerOp.get(ADDRESS).add("subsystem", "jca");
        workmanagerOp.get(ADDRESS).add("workmanager", entity.getName());
        workmanagerOp.get(OP).set(ADD);

        WorkmanagerPool pool = entity.getShortRunning().get(0);
        ModelNode poolOp = poolAdapter.fromEntity(pool);
        poolOp.get(ADDRESS).set(Baseadress.get());
        poolOp.get(ADDRESS).add("subsystem", "jca");
        poolOp.get(ADDRESS).add("workmanager", entity.getName());
        poolOp.get(OP).set(ADD);

        if(pool.isShortRunning())
            poolOp.get(ADDRESS).add("short-running-threads", pool.getName());
        else
            poolOp.get(ADDRESS).add("long-running-threads", pool.getName());


        List<ModelNode> steps = new ArrayList<ModelNode>(2);
        steps.add(workmanagerOp);
        steps.add(poolOp);

        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.addingFailed("Work Manager"), response.getFailureDescription());
                }
                else
                    Console.info(Console.MESSAGES.added("Work Manager "+entity.getName()));

                loadWorkManager();
            }
        });
    }

    // work manager

     public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void launchNewPropertyDialoge(String reference) {

        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Pool Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.trapWidget(
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
            final WorkmanagerPool entity,
            Map<String, Object> changeset)
    {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "jca");
        address.get(ADDRESS).add("workmanager", managerName);

        if(entity.isShortRunning())
            address.get(ADDRESS).add("short-running-threads", entity.getName());
        else
            address.get(ADDRESS).add("long-running-threads", entity.getName());

        ModelNode operation = poolAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Pool Config"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Pool Config"));

                loadWorkManager(true);
            }
        });
    }

    public void onRemovePoolConfig(
            String managerName, WorkmanagerPool entity) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("workmanager", managerName);

        if(entity.isShortRunning())
            operation.get(ADDRESS).add("short-running-threads", entity.getName());
        else
            operation.get(ADDRESS).add("long-running-threads", entity.getName());

        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.deletionFailed("Pool Config"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.deleted("Pool Config"));

                loadWorkManager(true);
            }
        });
    }

    public void launchNewPoolDialoge(JcaWorkmanager entity) {

        boolean hasShortRunning = entity.getShortRunning().size()>0;
        boolean hasLongRunning = entity.getLongRunning().size()>0;

        if(hasShortRunning && hasLongRunning)
        {
            Console.error(Console.CONSTANTS.subsys_jca_error_pool_exist(),
                    Console.CONSTANTS.subsys_jca_error_pool_exist_desc());
            return;
        }

        window = new ModalWindowLayout()
                .setTitle(Console.MESSAGES.createTitle("Pool Config"))
                .setWidget(new NewPoolWizard(this, entity.getName()).asWidget())
                .build();
    }

    public void createNewPool(String workManagerName, WorkmanagerPool pool) {

        closeDialoge();

        ModelNode operation = poolAdapter.fromEntity(pool);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "jca");
        operation.get(ADDRESS).add("workmanager", workManagerName);
        operation.get(OP).set(ADD);

        if(pool.isShortRunning())
            operation.get(ADDRESS).add("short-running-threads", pool.getName());
        else
            operation.get(ADDRESS).add("long-running-threads", pool.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.addingFailed("Pool Config"), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.added("Pool Config"));

                loadWorkManager(true);
            }
        });
    }

}
