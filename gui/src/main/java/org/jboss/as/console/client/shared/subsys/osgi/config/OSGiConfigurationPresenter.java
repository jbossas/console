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
package org.jboss.as.console.client.shared.subsys.osgi.config;

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
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiCapability;
import org.jboss.as.console.client.shared.subsys.osgi.config.model.OSGiSubsystem;
import org.jboss.as.console.client.shared.subsys.osgi.config.wizard.NewCapabilityWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author David Bosschaert
 */
public class OSGiConfigurationPresenter extends Presenter<OSGiConfigurationPresenter.MyView, OSGiConfigurationPresenter.MyProxy> {
    public static final String OSGI_SUBSYSTEM = "osgi";

    public static final String CAPABILITY_RESOURCE = "capability";
    public static final String FRAMEWORK_PROPERTY_RESOURCE = "property";

    public static final String ACTIVATION_ATTRIBUTE = "activation";
    public static final String STARTLEVEL_ATTRIBUTE = "startlevel";

    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private OSGiSubsystem providerEntity;
    private final RevealStrategy revealStrategy;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.OSGiConfigurationPresenter)
    public interface MyProxy extends Proxy<OSGiConfigurationPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(OSGiConfigurationPresenter presenter);
        void setProviderDetails(OSGiSubsystem provider);
        void updateProperties(List<PropertyRecord> properties);
        void updateCapabilities(List<OSGiCapability> capabilities);
    }

    @Inject
    public OSGiConfigurationPresenter(EventBus eventBus, MyView view, MyProxy proxy,
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
                ModelNode response = result.get();
                final ModelNode model = response.get(RESULT);

                providerEntity = factory.osgiSubsystem().as();

                boolean lazy = "lazy".equalsIgnoreCase(model.get(ACTIVATION_ATTRIBUTE).asString());
                providerEntity.setLazyActivation(lazy);

                // Load sub-elements asynchronously
                Console.schedule(new Command() {
                    @Override
                    public void execute() {
                        if (model.hasDefined(FRAMEWORK_PROPERTY_RESOURCE))
                            loadOSGiPropertyDetails();

                        Console.schedule(new Command() {
                            @Override
                            public void execute() {
                                if (model.hasDefined(CAPABILITY_RESOURCE))
                                    loadOSGiCapabilityDetails();
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
        operation.get(CHILD_TYPE).set(FRAMEWORK_PROPERTY_RESOURCE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
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

    private void loadOSGiCapabilityDetails() {
        ModelNode operation = createOperation(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set(CAPABILITY_RESOURCE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                ModelNode model = response.get(RESULT);

                List<OSGiCapability> capabilities = new ArrayList<OSGiCapability>();
                for (String identifier : model.keys()) {
                    OSGiCapability pm = factory.osgiCapability().as();
                    pm.setIdentifier(identifier);

                    ModelNode val = model.get(identifier);
                    if (val.has(STARTLEVEL_ATTRIBUTE)) {
                        pm.setStartLevel(val.get(STARTLEVEL_ATTRIBUTE).asString());
                    }
                    capabilities.add(pm);
                }
                getView().updateCapabilities(capabilities);
            }
        });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchCapabilityWizard(OSGiCapability capability) {
        String title;
        if (capability == null)
            title = Console.CONSTANTS.subsys_osgi_capabilityAdd();
        else
            title = Console.CONSTANTS.subsys_osgi_capabilityEdit();

        window = new DefaultWindow(title);
        window.setWidth(320);
        window.setHeight(240);
        window.trapWidget(new NewCapabilityWizard(this, capability).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        if (window != null)
            window.hide();
    }

    void onActivationChange(final boolean isLazy) {
        ModelNode operation = createOperation(WRITE_ATTRIBUTE_OPERATION);
        final String stringValue = isLazy ? "lazy" : "eager";
        operation.get(NAME).set(ACTIVATION_ATTRIBUTE);
        operation.get(VALUE).set(stringValue);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(WRITE_ATTRIBUTE_OPERATION, ACTIVATION_ATTRIBUTE, stringValue,
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiDetails();
                    }
                }));
    }

    public void onAddProperty(final PropertyRecord prop) {
        onAddChangeProperty(prop, ADD, false);
    }

    public void onChangeProperty(PropertyRecord prop) {
        onAddChangeProperty(prop, WRITE_ATTRIBUTE_OPERATION, true);
    }

    private void onAddChangeProperty(PropertyRecord prop, String opName, boolean addAttrName) {
        ModelNode operation = createOperation(opName);
        operation.get(ADDRESS).add(FRAMEWORK_PROPERTY_RESOURCE, prop.getKey());
        if (addAttrName)
            operation.get(NAME).set("value");
        operation.get("value").set(prop.getValue());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(opName, Console.CONSTANTS.subsys_osgi_frameworkProperty(), prop.getKey(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiPropertyDetails();
                    }
                }));
    }

    public void onDeleteProperty(final PropertyRecord property) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add(FRAMEWORK_PROPERTY_RESOURCE, property.getKey());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_osgi_frameworkProperty(), property.getKey(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiPropertyDetails();
                    }
                }));
    }

    public void onAddCapability(final OSGiCapability entity) {
        closeDialogue();

        ModelNode operation = createOperation(ADD);
        operation.get(ADDRESS).add(CAPABILITY_RESOURCE, entity.getIdentifier());
        if (entity.getStartLevel() != null && entity.getStartLevel().length() > 0)
            operation.get(STARTLEVEL_ATTRIBUTE).set(entity.getStartLevel());

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(ADD, Console.CONSTANTS.subsys_osgi_capability(), entity.getIdentifier(),
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiCapabilityDetails();
                    }
                }));
    }

    public void onDeleteCapability(final String identifier) {
        ModelNode operation = createOperation(REMOVE);
        operation.get(ADDRESS).add(CAPABILITY_RESOURCE, identifier);

        dispatcher.execute(new DMRAction(operation),
            new SimpleDMRResponseHandler(REMOVE, Console.CONSTANTS.subsys_osgi_capability(), identifier,
                new Command() {
                    @Override
                    public void execute() {
                        loadOSGiCapabilityDetails();
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
