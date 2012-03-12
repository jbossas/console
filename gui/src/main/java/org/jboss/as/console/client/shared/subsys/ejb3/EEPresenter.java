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
import java.util.Collections;
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

    public void onAddModule(Module module) {
        closeDialoge();

        boolean isAlreadyAssigned = false;
        List<Module> modules = new ArrayList<Module>();

        if(currentEntity.getModules()!=null)
            modules.addAll(currentEntity.getModules());

        for(Module m : modules)
        {
            if(m.getName().equals(module.getName()))
            {
                isAlreadyAssigned = true;
                break;
            }
        }

        if(!isAlreadyAssigned)
        {
            Module newModule = factory.eeModuleRef().as();
            newModule.setName(module.getName());
            newModule.setSlot(module.getSlot());

            modules.add(newModule);

            currentEntity.setModules(modules);

            onPersistModules(currentEntity);
        }
    }

    private void onPersistModules(EESubsystem updatedEntity) {

        ModelNode operation= new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "ee");
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
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("Modules"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("Modules"));
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
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Loading EE Subsystem"), response.getFailureDescription());
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
                    else
                    {
                        eeSubsystem.setModules(Collections.EMPTY_LIST);
                    }

                    EEPresenter.this.currentEntity = eeSubsystem;
                    getView().updateFrom(eeSubsystem);
                }
            }
        });
    }

    public void launchNewModuleDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Module"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewModuleWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    private void launchDialogue(List<String> names) {

    }


    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void onSave(final EESubsystem editedEntity, Map<String, Object> changeset) {

        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "ee");

        ModelNode operation = adapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("EE Subsystem"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("EE Subsystem"));
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
