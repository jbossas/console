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

package org.jboss.as.console.client.shared.general;

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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.LoadPropertiesCmd;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class PropertiesPresenter extends Presenter<PropertiesPresenter.MyView, PropertiesPresenter.MyProxy>
        implements PropertyManagement {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;
    private DefaultWindow propertyWindow;
    private LoadPropertiesCmd loadPropCmd;
    private RevealStrategy revealStrategy;

    @ProxyCodeSplit
    @NameToken(NameTokens.PropertiesPresenter)
    public interface MyProxy extends Proxy<PropertiesPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(PropertiesPresenter presenter);
        void setProperties(List<PropertyRecord> properties);
    }

    @Inject
    public PropertiesPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;


        ModelNode address = new ModelNode();
        //address.get(ADDRESS).setEmptyList();

        loadPropCmd = new LoadPropertiesCmd(dispatcher, factory, address);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadProperties();
    }

    private void loadProperties() {

        loadPropCmd.execute(new SimpleCallback<List<PropertyRecord>>(){
            @Override
            public void onSuccess(List<PropertyRecord> result) {
                getView().setProperties(result);
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

    public void launchNewPropertyDialoge(String group) {

        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("System Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, group, true).asWidget()
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

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("system-property", prop.getKey());
        operation.get("value").set(prop.getValue());
        operation.get("boot-time").set(prop.isBootTime());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info(Console.MESSAGES.added("Property " + prop.getKey()));
                loadProperties();
            }
        });

    }

    public void onDeleteProperty(final String groupName, final PropertyRecord prop)
    {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("system-property", prop.getKey());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info(Console.MESSAGES.deleted("Property " + prop.getKey()));
                loadProperties();
            }
        });
    }

    @Override
    public void onChangeProperty(String groupName, PropertyRecord prop) {
        // do nothing
    }
}
