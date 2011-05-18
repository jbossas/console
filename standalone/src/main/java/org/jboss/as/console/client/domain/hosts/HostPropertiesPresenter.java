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

package org.jboss.as.console.client.domain.hosts;

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
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class HostPropertiesPresenter extends Presenter<HostPropertiesPresenter.MyView, HostPropertiesPresenter.MyProxy>
        implements PropertyManagement {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;
    private DefaultWindow propertyWindow;
    private CurrentHostSelection currentHost;

    @ProxyCodeSplit
    @NameToken(NameTokens.HostPropertiesPresenter)
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
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get(NAME).set("system-properties");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
                if(response.hasDefined(RESULT)) {
                    List<Property> payload = response.get(RESULT).asPropertyList();

                    for(Property prop : payload)
                    {
                        String key = prop.getName();
                        ModelNode item = prop.getValue();
                        PropertyRecord propertyRecord = factory.property().as();
                        propertyRecord.setKey(key);
                        propertyRecord.setValue(item.get("value").asString());
                        propertyRecord.setBootTime(item.get("boot-time").asBoolean());

                        properties.add(propertyRecord);

                    }

                }

                getView().setProperties(properties);
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

        propertyWindow = new DefaultWindow("New System Property");
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.setWidget(
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

        ModelNode operation = new ModelNode();
        operation.get(OP).set("add-system-property");
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get("name").set(prop.getKey());
        operation.get("value").set(prop.getValue());
        operation.get("boot-time").set(prop.isBootTime());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Created property " + prop.getKey());
                loadProperties();
            }
        });

    }

    public void onDeleteProperty(final String groupName, final PropertyRecord prop)
    {
        ModelNode operation = new ModelNode();
        operation.get(OP).set("remove-system-property");
        operation.get(ADDRESS).add("host", currentHost.getName());
        operation.get("name").set(prop.getKey());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info("Success: Removed property "+prop.getKey());
                loadProperties();
            }
        });
    }

}
