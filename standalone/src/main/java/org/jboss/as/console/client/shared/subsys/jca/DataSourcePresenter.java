/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.shared.subsys.jca;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.CurrentSelectedProfile;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.LHSHighlightEvent;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;


/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DataSourcePresenter extends Presenter<DataSourcePresenter.MyView, DataSourcePresenter.MyProxy> {

    private final PlaceManager placeManager;
    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private boolean hasBeenRevealed = false;
    private DefaultWindow window;
    private CurrentSelectedProfile currentProfile;

    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourcePresenter)
    public interface MyProxy extends Proxy<DataSourcePresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DataSourcePresenter presenter);

        void updateDataSources(List<DataSource> datasources);

        void setEnabled(boolean b);
    }

    @Inject
    public DataSourcePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory,
            CurrentSelectedProfile currentProfile) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.currentProfile = currentProfile;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadDataSources();

        if(!hasBeenRevealed)
        {
            hasBeenRevealed=true;


            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    getEventBus().fireEvent(
                            new LHSHighlightEvent(null, "datasources", "profiles")
                    );
                }
            });

        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    void loadDataSources() {

        // /profile=default/subsystem=datasources:read-children-resources(child-type=data-source)

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).add("profile", "default"); // TODO: selected profile
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(CHILD_TYPE).set("data-source");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to load datasource", caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = ModelNode.fromBase64(result.getResponseText());
                List<ModelNode> payload = response.get("result").asList();

                List<DataSource> datasources = new ArrayList<DataSource>(payload.size());
                for(ModelNode item : payload)
                {
                    // returned as type property (key=ds name)
                    Property property = item.asProperty();
                    ModelNode ds = property.getValue().asObject();
                    String name = property.getName();
                    //System.out.println(ds.toJSONString(false));

                    try {
                        DataSource model = factory.dataSource().as();
                        model.setName(name);
                        model.setConnectionUrl(ds.get("connection-url").asString());
                        model.setJndiName(ds.get("jndi-name").asString());
                        model.setDriverClass(ds.get("driver-class").asString());
                        model.setDriverName(ds.get("driver").asString());
                        model.setEnabled(ds.get("enabled").asBoolean());
                        model.setUsername(ds.get("user-name").asString());
                        model.setPassword(ds.get("password").asString());
                        model.setPoolName(ds.get("pool-name").asString());

                        datasources.add(model);

                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                // finally update view
                getView().updateDataSources(datasources);
            }
        });

    }

    public void launchNewDatasourceWizard() {

        window = new DefaultWindow("Create Datasource");
        window.setWidth(480);
        window.setHeight(320);
        window.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        window.setWidget(
                new NewDatasourceWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }


    public void onCreateNewDatasource(final DataSource datasource) {
        window.hide();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).add("profile", currentProfile.getName());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", datasource.getName());


        operation.get("name").set(datasource.getName());
        operation.get("jndi-name").set(datasource.getJndiName());
        operation.get("enabled").set(datasource.isEnabled());

        operation.get("driver").set(datasource.getDriverName());
        operation.get("driver-class").set(datasource.getDriverClass());
        operation.get("pool-name").set(datasource.getName()+"_Pool");

        operation.get("connection-url").set(datasource.getConnectionUrl());
        operation.get("user-name").set(datasource.getUsername());

        String pw = datasource.getPassword() != null ? datasource.getPassword() : "";
        operation.get("password").set(pw);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Error creating new datasource: "+  caught.getMessage(), Message.Severity.Error)
                );
            }

            @Override
            public void onSuccess(DMRResponse result) {
                loadDataSources();
                Console.MODULES.getMessageCenter().notify(
                        new Message("Successfully created DataSource " + datasource.getName())
                );
            }
        });

    }

    public void onEdit(DataSource entity) {
        getView().setEnabled(true);
    }

    public void onSave(DataSource entity) {
        getView().setEnabled(false);
        Console.MODULES.getMessageCenter().notify(
                new Message("'Save' operation not implemented", Message.Severity.Warning)
        );

    }

    public void onDelete(DataSource entity) {

        final String dataSourceName = entity.getName();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(REMOVE);
        operation.get(ADDRESS).add("profile", currentProfile.getName());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", dataSourceName);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Failed to remove datasource: " + caught.getMessage(), Message.Severity.Error)
                );
            }

            @Override
            public void onSuccess(DMRResponse result) {
                loadDataSources();
                Console.MODULES.getMessageCenter().notify(
                        new Message("Successfully removed DataSource " + dataSourceName)
                );
            }
        });


    }

    // TODO: https://issues.jboss.org/browse/JBAS-9341
    public void onDisable(DataSource entity, boolean isEnabled) {
        final String dataSourceName = entity.getName();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        operation.get(ADDRESS).add("profile", currentProfile.getName());
        operation.get(ADDRESS).add("subsystem", "datasources");
        operation.get(ADDRESS).add("data-source", dataSourceName);
        operation.get("name").set("enabled");
        operation.get("value").set(isEnabled);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Failed to modify datasource" , caught.getMessage(), Message.Severity.Error)
                );
            }

            @Override
            public void onSuccess(DMRResponse result) {
                loadDataSources();
                Console.MODULES.getMessageCenter().notify(
                        new Message("Successfully modified DataSource " + dataSourceName)
                );
            }
        });
    }

    public void closeDialogue() {
        window.hide();
    }
}
