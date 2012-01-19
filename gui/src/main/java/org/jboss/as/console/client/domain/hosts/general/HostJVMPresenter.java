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

package org.jboss.as.console.client.domain.hosts.general;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmManagement;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class HostJVMPresenter extends Presenter<HostJVMPresenter.MyView, HostJVMPresenter.MyProxy>
        implements JvmManagement , HostSelectionEvent.HostSelectionListener{

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private DefaultWindow propertyWindow;
    private CurrentHostSelection currentHost;
    private BeanFactory factory;
    private ApplicationMetaData propertyMetaData;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostJVMPresenter)
    public interface MyProxy extends Proxy<HostJVMPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HostJVMPresenter presenter);
        void setJvms(List<Jvm> jvms);
    }

    @Inject
    public HostJVMPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, CurrentHostSelection currentHost,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.currentHost = currentHost;
        this.factory = factory;
        this.propertyMetaData = propertyMetaData;
    }

    @Override
    public void onHostSelection(String hostName) {
        if(isVisible())
            loadJVMConfig();
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(HostSelectionEvent.TYPE, this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadJVMConfig();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onCreateJvm(String reference, Jvm jvm) {

        closeDialogue();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get(ADDRESS).add(JVM, jvm.getName());

        operation.get("heap-size").set(jvm.getHeapSize());
        operation.get("max-heap-size").set(jvm.getMaxHeapSize());
        operation.get("debug-enabled").set(jvm.isDebugEnabled());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Created JVM settings");
                loadJVMConfig();
            }
        });
    }

    private void loadJVMConfig() {

        if(!currentHost.isSet())
            throw new RuntimeException("Host selection not set!");

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get(CHILD_TYPE).set(JVM);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                List<Property> payload = response.get(RESULT).asPropertyList();
                List<Jvm> jvms = new ArrayList<Jvm>(payload.size());

                for(Property prop : payload) {
                    String jvmName = prop.getName();
                    Jvm jvm = factory.jvm().as();
                    jvm.setName(jvmName);

                    ModelNode jvmPropValue = prop.getValue();

                    if(jvmPropValue.hasDefined("heap-size"))
                        jvm.setHeapSize(jvmPropValue.get("heap-size").asString());

                    if(jvmPropValue.hasDefined("max-heap-size"))
                        jvm.setMaxHeapSize(jvmPropValue.get("max-heap-size").asString());

                    if(jvmPropValue.hasDefined("debug-enabled"))
                        jvm.setDebugEnabled(jvmPropValue.get("debug-enabled").asBoolean());

                    jvms.add(jvm);
                }

                getView().setJvms(jvms);
            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, Jvm jvm) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get(ADDRESS).add(JVM, jvm.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Removed JVM settings");
                loadJVMConfig();
            }
        });
    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {
        if(changedValues.size()>0)
        {
            ModelNode proto = new ModelNode();
            proto.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
            proto.get(ADDRESS).add("host", currentHost.getName());
            proto.get(ADDRESS).add(JVM, jvmName);

            List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(Jvm.class);
            ModelNode operation  = ModelAdapter.detypedFromChangeset(proto, changedValues, bindings);

            dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

                @Override
                public void onSuccess(DMRResponse result) {
                    Console.info("Success: Updated JVM settings");
                    loadJVMConfig();
                }
            });
        }
        else
        {
            Console.warning("No changes applied!");
        }
    }

    public void launchNewJVMDialogue() {
        window = new DefaultWindow("Create JVM Declaration");
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewHostJvmWizard(this, currentHost).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void closeDialogue() {
        if(window!=null && window.isShowing())
            window.hide();
    }
}
