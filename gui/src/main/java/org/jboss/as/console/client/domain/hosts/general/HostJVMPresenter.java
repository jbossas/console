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
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DomainGateKeeper;
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
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
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
    private EntityAdapter<Jvm> adapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostJVMPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
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
            ApplicationMetaData metaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.currentHost = currentHost;
        this.factory = factory;
        this.propertyMetaData = metaData;


        adapter = new EntityAdapter<Jvm>(Jvm.class, metaData);
    }

    @Override
    public void onHostSelection(String hostName) {
        if(isVisible() && currentHost.isSet())
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

        if(currentHost.isSet())
            loadJVMConfig();
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    public void onCreateJvm(String reference, Jvm jvm) {

        closeDialogue();

        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());
        address.add(JVM, jvm.getName());

        ModelNode operation = adapter.fromEntity(jvm);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.addingFailed("JVM Configurations"), response.getFailureDescription());
                }
                else
                {
                    Console.MESSAGES.added("JVM Configurations");
                }

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
                List<Jvm> jvms = new ArrayList<Jvm>();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("JVM Configurations"), response.getFailureDescription());
                }
                else
                {
                    List<Property> payload = response.get(RESULT).asPropertyList();

                    for(Property prop : payload) {
                        String jvmName = prop.getName();
                        ModelNode jvmPropValue = prop.getValue();
                        Jvm jvm = adapter.fromDMR(jvmPropValue);
                        jvm.setName(jvmName);
                        jvms.add(jvm);
                    }

                }

                getView().setJvms(jvms);

            }
        });
    }

    @Override
    public void onDeleteJvm(String reference, Jvm jvm) {

        if(jvm.getName().equals("default"))
        {
            Console.error(Console.MESSAGES.deletionFailed("JVM Configurations"),
                    Console.CONSTANTS.hosts_jvm_err_deleteDefault());
            return;
        }

        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get(ADDRESS).add(JVM, jvm.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.deletionFailed("JVM Configurations"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.deleted("JVM Configuration"));
                }

                loadJVMConfig();
            }
        });
    }

    @Override
    public void onUpdateJvm(String reference, String jvmName, Map<String, Object> changedValues) {

        ModelNode address = new ModelNode();
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        address.get(ADDRESS).add("host", currentHost.getName());
        address.get(ADDRESS).add(JVM, jvmName);

        ModelNode operation = adapter.fromChangeset(changedValues, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("JVM Configuration"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("JVM Configuration"));
                }

                loadJVMConfig();
            }
        });

    }

    public void launchNewJVMDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("JVM Configuration"));
        window.setWidth(480);
        window.setHeight(360);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.trapWidget(
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
