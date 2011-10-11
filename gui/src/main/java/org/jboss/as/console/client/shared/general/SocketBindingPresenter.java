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
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.general.model.LoadSocketBindingsCmd;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;

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
    private PropertyMetaData metaData;
    private EntityAdapter<SocketBinding> entityAdapter;

    @ProxyCodeSplit
    @NameToken(NameTokens.SocketBindingPresenter)
    public interface MyProxy extends Proxy<SocketBindingPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SocketBindingPresenter presenter);
        void updateGroups(List<String> groups);
        void setBindings(String groupName, List<SocketBinding> bindings);
        void setEnabled(boolean b);
    }

    @Inject
    public SocketBindingPresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            PropertyMetaData propertyMetaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.metaData = propertyMetaData;

        this.entityAdapter = new EntityAdapter<SocketBinding>(SocketBinding.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
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
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<String> groups = new ArrayList<String>();
                for (ModelNode group : payload) {
                    groups.add(group.asString());
                }

                bindingGroups = groups;
                getView().updateGroups(groups);
            }
        });
    }

    public void onFilterGroup(String groupName) {
        loadBindings(groupName);
    }

    private void loadBindings(final String groupName) {

        LoadSocketBindingsCmd cmd = new LoadSocketBindingsCmd(dispatcher, factory, metaData, groupName);
        cmd.execute(new SimpleCallback<List<SocketBinding>>() {
            @Override
            public void onSuccess(List<SocketBinding> result) {
                getView().setBindings(groupName, result);
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
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if(ModelAdapter.wasSuccess(response))
                    Console.info("Success: Updated socket binding "+name);
                else
                    Console.error("Failed: Update socket binding "+name, response.toString());

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
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if(ModelAdapter.wasSuccess(response))
                    Console.info("Success: remove socket binding " + editedEntity.getName());
                else
                    Console.error("Error: Failed to remove socket binding", response.toString());

                reload();
            }
        });
    }

    public void launchNewSocketDialogue() {
        window = new DefaultWindow("Create Socket Binding");
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
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

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if(ModelAdapter.wasSuccess(response))
                    Console.info("Success: Created socket binding "+socketBinding.getName());
                else
                    Console.error("Error: Failed to created socket binding " + socketBinding.getName(), response.toString());

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
}
