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
import org.jboss.as.console.client.core.DomainGateKeeper;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.events.HostSelectionEvent;
import org.jboss.as.console.client.domain.hosts.HostMgmtPresenter;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.properties.CreatePropertyCmd;
import org.jboss.as.console.client.shared.properties.DeletePropertyCmd;
import org.jboss.as.console.client.shared.properties.LoadPropertiesCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.state.CurrentHostSelection;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class HostPropertiesPresenter extends Presenter<HostPropertiesPresenter.MyView, HostPropertiesPresenter.MyProxy>
        implements PropertyManagement, HostSelectionEvent.HostSelectionListener  {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;
    private DefaultWindow propertyWindow;
    private CurrentHostSelection currentHost;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostPropertiesPresenter)
    @UseGatekeeper( DomainGateKeeper.class )
    public interface MyProxy extends Proxy<HostPropertiesPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(HostPropertiesPresenter presenter);
        void setProperties(List<PropertyRecord> properties);
    }

    @Inject
    public HostPropertiesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, CurrentHostSelection currentHost) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.currentHost = currentHost;
        this.factory = factory;
    }

    @Override
    public void onHostSelection(String hostName) {
        if(isVisible())
            loadProperties();
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
        loadProperties();
    }

    private void loadProperties() {

        if(!currentHost.isSet())
            throw new RuntimeException("Host selection not set!");

        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());

        LoadPropertiesCmd loadPropCmd = new LoadPropertiesCmd(dispatcher, factory, address);
        loadPropCmd.execute(new SimpleCallback<List<PropertyRecord>>() {
            @Override
            public void onSuccess(List<PropertyRecord> result) {
                getView().setProperties(result);
            }
        });
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), HostMgmtPresenter.TYPE_MainContent, this);
    }

    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void launchNewPropertyDialoge(String group) {

        propertyWindow = new DefaultWindow("New Host Property");
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, group).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    public void onCreateProperty(final String groupName, final PropertyRecord prop)
    {

        if(propertyWindow!=null && propertyWindow.isShowing())
        {
            propertyWindow.hide();
        }

        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());
        address.add("system-property", prop.getKey());

        CreatePropertyCmd cmd = new CreatePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadProperties();
            }
        });


    }

    public void onDeleteProperty(final String groupName, final PropertyRecord prop)
    {
        ModelNode address = new ModelNode();
        address.add("host", currentHost.getName());
        address.add("system-property", prop.getKey());

        DeletePropertyCmd cmd = new DeletePropertyCmd(dispatcher, factory, address);
        cmd.execute(prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                loadProperties();
            }
        });
    }

    @Override
    public void onChangeProperty(String groupName, PropertyRecord prop) {
        // do nothing
    }
}
