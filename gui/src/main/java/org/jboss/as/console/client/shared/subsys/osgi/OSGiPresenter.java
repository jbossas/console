/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.osgi;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADD;
import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.REMOVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.dmr.client.ModelDescriptionConstants.VALUE;
import static org.jboss.dmr.client.ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
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
import org.jboss.as.console.client.shared.dispatch.impl.SimpleDMRResponseHandler;
import org.jboss.as.console.client.shared.general.MessageWindow;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiConfigAdminData;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiPreloadedModule;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.osgi.wizard.NewConfigAdminDataWizard;
import org.jboss.as.console.client.shared.subsys.osgi.wizard.NewModuleWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * @author David Bosschaert
 */
public class OSGiPresenter extends Presenter<OSGiPresenter.MyView, OSGiPresenter.MyProxy> {
    public static final String OSGI_SUBSYSTEM = "osgi";

    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private OSGiSubsystem providerEntity;
    private final RevealStrategy revealStrategy;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.OSGiPresenter)
    public interface MyProxy extends Proxy<OSGiPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(OSGiPresenter presenter);
        void setProviderDetails(OSGiSubsystem provider);
        void updateProperties(List<PropertyRecord> properties);
        void updatePreloadedModules(List<OSGiPreloadedModule> modules);
        void updateConfigurationAdmin(List<OSGiConfigAdminData> casDataList, String selectPid);
    }

    @Inject
    public OSGiPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
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
        loadOSGiDetails();
    }

    private void loadOSGiDetails() {
        ModelNode operation = createOperation(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                final ModelNode model = response.get(RESULT);

                providerEntity = factory.osgiSubsystem().as();

                boolean lazy = "lazy".equalsIgnoreCase(model.get("activation").asString());
                providerEntity.setLazyActivation(lazy);

                // Load sub-elements asynchronously
                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        if (model.hasDefined("property"))
                            loadOSGiPropertyDetails();

                        Console.schedule(new Command() {
                            @Override
                            public void execute() {
                                if (model.hasDefined("module"))
                                    loadOSGiModuleDetails();

                                Console.schedule(new Command() {
                                    @Override
                                    public void execute() {
                                        if (model.hasDefined("configuration"))
                                            loadOSGiConfigAdminDetails(null);
                                    }
                                });
                            }
                        });
                    }
                });

                getView().setProviderDetails(providerEntity);
            }
        });
    }

    private void loadOSGiPropertyDetails() {
        ModelNode operation = createOperation(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("property");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode model = response.get(RESULT);

                List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
                for (String key : model.keys()) {
                    PropertyRecord property = factory.property().as();
                    property.setKey(key);
                    property.setValue(model.get(key).get("value").asString());
                    properties.add(property);
                }
                getView().updateProperties(properties);
            }
        });
    }

    private void loadOSGiModuleDetails() {
        ModelNode operation = createOperation(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("module");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode model = response.get(RESULT);

                List<OSGiPreloadedModule> modules = new ArrayList<OSGiPreloadedModule>();
                for (String moduleName : model.keys()) {
                    OSGiPreloadedModule pm = factory.osgiPreloadedModule().as();
                    pm.setIdentifier(moduleName);

                    ModelNode val = model.get(moduleName);
                    if (val.has("start")) {
                        pm.setStartLevel(val.get("start").asString());
                    }
                    modules.add(pm);
                }
                getView().updatePreloadedModules(modules);
            }
        });
    }

    private void loadOSGiConfigAdminDetails(final String selectPid) {
        ModelNode operation = createOperation(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("configuration");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                ModelNode model = response.get(RESULT);

                List<OSGiConfigAdminData> casDataList = new ArrayList<OSGiConfigAdminData>();
                for (String pid : model.keys()) {
                    OSGiConfigAdminData data = factory.osgiConfigAdminData().as();
                    data.setPid(pid);

                    List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
                    for(Property property : model.get(pid).get("entries").asPropertyList()) {
                        PropertyRecord record = factory.property().as();
                        record.setKey(property.getName());
                        record.setValue(property.getValue().asString());
                        properties.add(record);
                    }
                    data.setProperties(properties);
                    casDataList.add(data);
                }
                getView().updateConfigurationAdmin(casDataList, selectPid);
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchModuleWizard(OSGiPreloadedModule module) {
        String title;
        if (module == null)
            title = Console.CONSTANTS.subsys_osgi_preloadedModuleAdd();
        else
            title = Console.CONSTANTS.subsys_osgi_preloadedModuleEdit();

        window = new DefaultWindow(title);
        window.setWidth(320);
        window.setHeight(240);
        window.setWidget(new NewModuleWizard(this, module).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }


    public void launchNewCASPropertyWizard() {
        window = new DefaultWindow(Console.CONSTANTS.subsys_osgi_configAdminAdd());
        window.setWidth(480);
        window.setHeight(360);
        window.setWidget(new NewConfigAdminDataWizard(this).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        if (window != null)
            window.hide();
    }

    void onActivationChange(final boolean isLazy) {
        if (!isLazy) {
            window = new DefaultWindow(Console.CONSTANTS.common_label_changeActivation());
            window.setWidth(320);
            window.setHeight(140);
            window.setWidget(new MessageWindow(Console.MESSAGES.subsys_osgi_activationWarning(),
                new MessageWindow.Result() {
                    @Override
                    public void result(boolean result) {
                        closeDialogue();
                        if (result)
                            applyActivationChange(isLazy);
                        else
                            loadOSGiDetails();
                    }
                }).asWidget());
            window.setGlassEnabled(true);
            window.center();
        } else {
            applyActivationChange(isLazy);
        }
    }

    private void applyActivationChange(boolean isLazy) {
        ModelNode operation = createOperation(WRITE_ATTRIBUTE_OPERATION);
        final String stringValue = isLazy ? "lazy" : "eager";
        operation.get(NAME).set("activation");
        operation.get(VALUE).set(stringValue);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(WRITE_ATTRIBUTE_OPERATION, "activation", stringValue,
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiDetails();
                    }
                }));
    }

    public void onAddProperty(final PropertyRecord prop) {
        ModelNode operation = createOperation(ADD);
        operation.get(ADDRESS).add("property", prop.getKey());
        operation.get("value").set(prop.getValue());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ADD, Console.CONSTANTS.subsys_osgi_frameworkProperty(), prop.getKey(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiPropertyDetails();
                    }
                }));
    }

    public void onDeleteProperty(final PropertyRecord property) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add("property", property.getKey());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_osgi_frameworkProperty(), property.getKey(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiPropertyDetails();
                    }
                }));
    }

    public void onAddPreloadedModule(final OSGiPreloadedModule entity) {
        closeDialogue();

        ModelNode operation = createOperation(ADD);
        operation.get(ADDRESS).add("module", entity.getIdentifier());
        if (entity.getStartLevel() != null && entity.getStartLevel().length() > 0)
            operation.get("start").set(entity.getStartLevel());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ADD, Console.CONSTANTS.subsys_osgi_preloadedModule(), entity.getIdentifier(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiModuleDetails();
                    }
                }));
    }

    public void onDeletePreloadedModule(final String identifier) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add("module", identifier);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_osgi_preloadedModule(), identifier,
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiModuleDetails();
                    }
                }));
    }

    public void onAddConfigurationAdminData(final OSGiConfigAdminData data) {
        closeDialogue();

        ModelNode operation = createOperation(ADD);
        operation.get(ADDRESS).add("configuration", data.getPid());
        ModelNode modelData = operation.get("entries");
        for (PropertyRecord record : data.getProperties()) {
            modelData.get(record.getKey()).set(record.getValue());
        }

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ADD, Console.CONSTANTS.subsys_osgi_configAdminPID(), data.getPid(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiConfigAdminDetails(data.getPid());
                    }
                }));
    }

    public void onDeleteConfigurationAdminData(final String pid) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add("configuration", pid);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_osgi_configAdminPID(), pid,
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiConfigAdminDetails(null);
                    }
                }));
    }

    private ModelNode createOperation(String operator) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(operator);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add(SUBSYSTEM, OSGI_SUBSYSTEM);
        return operation;
    }
}
