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
import org.jboss.as.console.client.shared.general.model.LoadInterfacesCmd;
import org.jboss.as.console.client.shared.general.model.LoadSocketBindingsCmd;
import org.jboss.as.console.client.shared.general.model.LocalSocketBinding;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.general.model.SocketGroup;
import org.jboss.as.console.client.shared.general.wizard.NewRemoteSocketWizard;
import org.jboss.as.console.client.shared.general.wizard.NewSocketWizard;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
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
 * @date 4/6/11
 */
public class SocketBindingPresenter extends Presenter<SocketBindingPresenter.MyView, SocketBindingPresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private RevealStrategy revealStrategy;
    private DefaultWindow window;
    private List<String> bindingGroups;
    private ApplicationMetaData metaData;
    private EntityAdapter<SocketBinding> entityAdapter;
    private LoadInterfacesCmd loadInterfacesCmd;
    private EntityAdapter<SocketGroup> socketGroupAdapter;
    private EntityAdapter<RemoteSocketBinding> remoteSocketAdapter;
    private EntityAdapter<LocalSocketBinding> localSocketAdapter;
    private String selectedSocketGroup = null;

    public PlaceManager getPlaceManager() {
        return placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.SocketBindingPresenter)
    public interface MyProxy extends Proxy<SocketBindingPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SocketBindingPresenter presenter);
        void updateGroups(List<String> groups);
        void setBindings(String groupName, List<SocketBinding> bindings);
        void setEnabled(boolean b);

        void setRemoteSockets(String groupName, List<RemoteSocketBinding> entities);

        void setLocalSockets(String groupName, List<LocalSocketBinding> entities);
        void setSelectedGroup(String selectedGroup);
    }

    @Inject
    public SocketBindingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            ApplicationMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;

        this.entityAdapter = new EntityAdapter<SocketBinding>(SocketBinding.class, metaData);
        this.remoteSocketAdapter = new EntityAdapter<RemoteSocketBinding>(RemoteSocketBinding.class, metaData);
        this.localSocketAdapter = new EntityAdapter<LocalSocketBinding>(LocalSocketBinding.class, metaData);
        this.socketGroupAdapter = new EntityAdapter<SocketGroup>(SocketGroup.class, metaData);

        ModelNode address = new ModelNode();
        address.setEmptyList();
        loadInterfacesCmd = new LoadInterfacesCmd(dispatcher, address, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        selectedSocketGroup = request.getParameter("name", null);
    }

    @Override
    protected void onReset() {
        super.onReset();
        loadBindingGroups();
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    private void loadBindingGroups()
    {
        // :read-children-names(child-type=socket-binding-group)

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set("socket-binding-group");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.failed("Binding Groups"), response.getFailureDescription());
                }
                else
                {
                    List<ModelNode> payload = response.get("result").asList();

                    List<String> groups = new ArrayList<String>();
                    for (ModelNode group : payload) {
                        groups.add(group.asString());
                    }

                    bindingGroups = groups;
                    getView().updateGroups(groups);
                    getView().setSelectedGroup(selectedSocketGroup);
                }
            }
        });
    }

    public void loadDetails(String selectedGroup) {
        loadBindings(selectedGroup);
    }

    public void loadBindings(final String groupName) {

        LoadSocketBindingsCmd cmd = new LoadSocketBindingsCmd(dispatcher, factory, metaData);
        cmd.execute(groupName, new SimpleCallback<List<SocketBinding>>() {
            @Override
            public void onSuccess(List<SocketBinding> result) {
                getView().setBindings(groupName, result);
            }
        });


        loadRemoteSockets(groupName);
        loadLocalSockets(groupName);
    }

    private void loadRemoteSockets(final String groupName) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("socket-binding-group", groupName);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("remote-destination-outbound-socket-binding");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                List<Property> items = response.get(RESULT).asPropertyList();
                List<RemoteSocketBinding> entities = new ArrayList<RemoteSocketBinding>();
                for(Property item : items)
                {
                    RemoteSocketBinding remoteSocketBinding = remoteSocketAdapter.fromDMR(item.getValue());
                    remoteSocketBinding.setName(item.getName());
                    entities.add(remoteSocketBinding);
                }

                getView().setRemoteSockets(groupName, entities);
            }
        });
    }

    private void loadLocalSockets(final String groupName) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("socket-binding-group", groupName);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("local-destination-outbound-socket-binding");
        operation.get(RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                List<Property> items = response.get(RESULT).asPropertyList();
                List<LocalSocketBinding> entities = new ArrayList<LocalSocketBinding>();
                for(Property item : items)
                {
                    LocalSocketBinding LocalSocketBinding = localSocketAdapter.fromDMR(item.getValue());
                    LocalSocketBinding.setName(item.getName());
                    entities.add(LocalSocketBinding);
                }

                getView().setLocalSockets(groupName, entities);
            }
        });
    }

    public void saveSocketBinding(final String name, final String group, Map<String, Object> changeset) {

        ModelNode address = new ModelNode();
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        address.get(ADDRESS).add("socket-binding-group", group);
        address.get(ADDRESS).add("socket-binding", name);

        ModelNode operation = entityAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.modified("Socket Binding "+name));
                else
                    Console.error(Console.MESSAGES.modificationFailed("Socket Binding " + name), response.getFailureDescription());

                loadBindings(group);
            }

            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadBindings(group);
            }
        });
    }

    public void onDelete(final SocketBinding editedEntity) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("socket-binding-group", editedEntity.getGroup());
        operation.get(ADDRESS).add("socket-binding", editedEntity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.deleted("Socket binding " + editedEntity.getName()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Socket binding " + editedEntity.getName()), response.getFailureDescription());

                reload();
            }
        });
    }

    public void launchNewSocketDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Socket Binding"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewSocketWizard(this, bindingGroups).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void createNewSocketBinding(final SocketBinding socketBinding) {
        closeDialoge();

        ModelNode operation = entityAdapter.fromEntity(socketBinding);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("socket-binding-group", socketBinding.getGroup());
        operation.get(ADDRESS).add("socket-binding", socketBinding.getName());


        // TODO: Workaround for https://issues.jboss.org/browse/AS7-2215
        operation.remove("multicast-port");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.added("Socket Binding "+socketBinding.getName()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Socket Binding " + socketBinding.getName()), response.getFailureDescription());

                reload();
            }
        });


    }

    private void reload() {
        loadBindingGroups();
        loadBindings(bindingGroups.get(0));
    }

    public void closeDialoge() {
        window.hide();
    }

    /*public void launchNewGroupDialogue() {


        loadInterfacesCmd.execute(new SimpleCallback<List<Interface>>() {
            @Override
            public void onSuccess(List<Interface> result) {
                window = new DefaultWindow(Console.MESSAGES.createTitle("Datasource"));
                window.setWidth(480);
                window.setHeight(400);

                window.trapWidget(
                        new NewSocketGroupWizard(SocketBindingPresenter.this, result).asWidget()
                );

                window.setGlassEnabled(true);
                window.center();
            }
        });
    }*/

    public void createNewSocketGroup(SocketGroup newGroup) {
        /*closeDialoge();

        ModelNode operation = socketGroupAdapter.fromEntity(newGroup);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("socket-binding-group", newGroup.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info("Success: Created socket binding "+socketBinding.getName());
                else
                    Console.error("Error: Failed to created socket binding " + socketBinding.getName(), response.toString());

                reload();
            }
        });*/


    }

    public void saveRemoteSocketBinding(final String name, Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.add("socket-binding-group", selectedSocketGroup);
        address.add("remote-destination-outbound-socket-binding", name);

        ModelNode addressNode = new ModelNode();
        addressNode.get(ADDRESS).set(address);

        ModelNode operation = remoteSocketAdapter.fromChangeset(changeset, addressNode);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                    Console.error(Console.MESSAGES.modificationFailed("Remote Socket Binding" + name), response.getFailureDescription());
                else
                    Console.info(Console.MESSAGES.modified("Remote Socket Binding " + name));

                loadRemoteSockets(selectedSocketGroup);
            }
        });
    }

    public void launchNewRemoteSocketBindingWizard() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Remote Socket Binding"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewRemoteSocketWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void onDeleteRemoteSocketBinding(final String name) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("socket-binding-group", selectedSocketGroup);
        operation.get(ADDRESS).add("remote-destination-outbound-socket-binding", name);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.deleted("Remote Socket Binding " + name));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Remote Socket Binding " + name), response.getFailureDescription());

                loadRemoteSockets(selectedSocketGroup);
            }
        });
    }

    public void onCreateRemoteSocketBinding(final RemoteSocketBinding entity) {

        closeDialoge();

        ModelNode operation = remoteSocketAdapter.fromEntity(entity);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("socket-binding-group", selectedSocketGroup);
        operation.get(ADDRESS).add("remote-destination-outbound-socket-binding", entity.getName());

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if(ModelAdapter.wasSuccess(response))
                    Console.info(Console.MESSAGES.added("Remote Socket Binding "+entity.getName()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Remote Socket Binding " + entity.getName()), response.getFailureDescription());

                loadRemoteSockets(selectedSocketGroup);
            }
        });
    }

    public void closeDialogue() {
        window.hide();
    }

    public void saveLocalSocketBinding(final String name, Map<String, Object> changeset) {

    }

    public void launchNewLocalSocketBindingWizard() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void onDeleteLocalSocketBinding(String name) {
        //To change body of created methods use File | Settings | File Templates.
    }
}
