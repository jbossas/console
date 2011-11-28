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
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
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
    private DefaultWindow window;
    private EESubsystem currentEntity;


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

    public void onAddModule(String moduleName) {
        closeDialoge();

        boolean isAlreadyAssigned = false;
        List<Module> modules = currentEntity.getModules() !=null ?
                currentEntity.getModules() : new ArrayList<Module>(1);

        for(Module m : modules)
        {
            if(m.getName().equals(moduleName))
            {
                isAlreadyAssigned = true;
                break;
            }
        }

        if(!isAlreadyAssigned)
        {
            Module newModule = factory.eeModuleRef().as();
            newModule.setName(moduleName);
            newModule.setSlot("main");

            modules.add(newModule);

            currentEntity.setModules(modules);

            onPersistModules(currentEntity);
        }
    }

    private void onPersistModules(EESubsystem updatedEntity) {

        ModelNode operation = beanMetaData.getAddress().asResource();
        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(NAME).set("global-modules");

        List<ModelNode> modules = new ArrayList<ModelNode>();
        for(Module m : updatedEntity.getModules())
        {
            ModelNode moduleRef = new ModelNode();
            moduleRef.get("name").set(m.getName());
            moduleRef.get("slot").set(m.getSlot());

            modules.add(moduleRef);
        }

        operation.get(VALUE).set(modules);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to update modules subsystem");
                }
                else
                {
                    Console.info("Success: Update modules ");
                }

                loadSubsystem();
            }
        });

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

                    EEPresenter.this.currentEntity = eeSubsystem;
                    getView().updateFrom(eeSubsystem);
                }
            }
        });
    }

    public void launchNewModuleDialogue() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(ADDRESS).setEmptyList();
        operation.get(CHILD_TYPE).set("extension");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if(response.isFailure())
                {
                    Console.error("Failed to load extensions");
                }
                else
                {
                    List<ModelNode> modelNodes = response.get(RESULT).asList();
                    List<String> names = new ArrayList<String>(modelNodes.size());
                    for(ModelNode model : modelNodes)
                        names.add(model.asString());

                    launchDialogue(names);
                }

            }
        });
    }

    private void launchDialogue(List<String> names) {
        window = new DefaultWindow("Add Module");
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
                new NewModuleWizard(this, names).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
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

    public void closeDialoge() {
        window.hide();
    }

    public void onRemoveModule(EESubsystem editedEntity, Module module) {

        List<Module> modules = new ArrayList<Module>();

        for(Module m : editedEntity.getModules())
        {
            if(!m.getName().equals(module.getName()))
            {
                modules.add(m);
            }
        }

        currentEntity.setModules(modules);
        onPersistModules(currentEntity);
    }
}
