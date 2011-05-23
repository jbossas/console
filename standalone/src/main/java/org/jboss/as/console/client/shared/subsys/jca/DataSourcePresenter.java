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

package org.jboss.as.console.client.shared.subsys.jca;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.domain.profiles.CurrentProfileSelection;
import org.jboss.as.console.client.domain.profiles.ProfileMgmtPresenter;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverRegistry;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewDatasourceWizard;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewXADatasourceWizard;
import org.jboss.as.console.client.widgets.DefaultWindow;

import java.util.List;
import java.util.Map;


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
    private CurrentProfileSelection currentProfileSelection;
    private DataSourceStore dataSourceStore;
    private DriverRegistry driverRegistry;

    @ProxyCodeSplit
    @NameToken(NameTokens.DataSourcePresenter)
    public interface MyProxy extends Proxy<DataSourcePresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setPresenter(DataSourcePresenter presenter);
        void updateDataSources(List<DataSource> datasources);
        void updateXADataSources(List<XADataSource> result);
        void enableDSDetails(boolean b);
        void enableXADetails(boolean b);
    }

    @Inject
    public DataSourcePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager, DispatchAsync dispatcher,
            BeanFactory factory,
            CurrentProfileSelection currentProfileSelection,
            DataSourceStore dataSourceStore,
            DriverRegistry driverRegistry) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.currentProfileSelection = currentProfileSelection;
        this.dataSourceStore = dataSourceStore;
        this.driverRegistry = driverRegistry;
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
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(getEventBus(), ProfileMgmtPresenter.TYPE_MainContent, this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
    }

    void loadDataSources() {

        String profile = currentProfileSelection.getName() == null ? "default" : currentProfileSelection.getName();

        dataSourceStore.loadDataSources(profile, new AsyncCallback<List<DataSource>>() {
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to load datasource");
            }

            @Override
            public void onSuccess(List<DataSource> result) {
                getView().updateDataSources(result);
            }
        });


        //  xa datasources

        dataSourceStore.loadXADataSources(profile, new AsyncCallback<List<XADataSource>>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to load datasource", caught);
            }

            @Override
            public void onSuccess(List<XADataSource> result) {
                getView().updateXADataSources(result);
            }
        });

    }

    public void launchNewDatasourceWizard() {

        driverRegistry.refreshDrivers(new SimpleCallback<List<JDBCDriver>>() {
            @Override
            public void onSuccess(List<JDBCDriver> drivers) {

                window = new DefaultWindow("Create Datasource");
                window.setWidth(480);
                window.setHeight(320);
                window.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {

                    }
                });

                window.setWidget(
                        new NewDatasourceWizard(DataSourcePresenter.this, drivers).asWidget()
                );

                window.setGlassEnabled(true);
                window.center();

            }
        });

    }

    public void launchNewXADatasourceWizard() {


        driverRegistry.refreshDrivers(new SimpleCallback<List<JDBCDriver>>() {
            @Override
            public void onSuccess(List<JDBCDriver> drivers) {
                window = new DefaultWindow("Create XA Datasource");
                window.setWidth(480);
                window.setHeight(320);
                window.addCloseHandler(new CloseHandler<PopupPanel>() {
                    @Override
                    public void onClose(CloseEvent<PopupPanel> event) {

                    }
                });

                window.setWidget(
                        new NewXADatasourceWizard(DataSourcePresenter.this, drivers).asWidget()
                );

                window.setGlassEnabled(true);
                window.center();
            }
        });

    }


    public void onCreateNewDatasource(final DataSource datasource) {
        window.hide();

        dataSourceStore.createDataSource(currentProfileSelection.getName(), datasource, new SimpleCallback<ResponseWrapper<Boolean>>() {

            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if (result.getUnderlying()) {
                    Console.info("Success: Create datasource " + datasource.getName());
                    loadDataSources();
                }
                else
                    Console.error("Failed to create datasource", result.getResponse().toString());
            }
        });

    }

    public void onEditDS(DataSource entity) {
        getView().enableDSDetails(true);
    }

    public void onEditXA(DataSource entity) {
        getView().enableXADetails(true);
    }

    public void onDelete(final DataSource entity) {

        dataSourceStore.deleteDataSource(currentProfileSelection.getName(), entity, new SimpleCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean success) {

                if(success)
                {
                    loadDataSources();
                    Console.info("Successfully removed datasource " + entity.getName());
                }
                else
                {
                    Console.error("Failed to remove datasource " + entity.getName());
                }

                loadDataSources();
            }
        });
    }

    public void onDisable(final DataSource entity, boolean doEnable) {
        dataSourceStore.enableDataSource(currentProfileSelection.getName(), entity, doEnable, new SimpleCallback<ResponseWrapper<Boolean>>() {

            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if (result.getUnderlying()) {
                    Console.info("Successfully modified datasource " + entity.getName());
                } else {
                    Console.error("Failed to modify datasource" + entity.getName(), result.getResponse().toString());
                }

                loadDataSources();
            }
        });
    }

    public void closeDialogue() {
        window.hide();
    }

    public void onSaveDSDetails(final String name, Map<String, Object> changedValues) {
        getView().enableDSDetails(false);
        if(changedValues.size()>0)
        {
            dataSourceStore.updateDataSource(currentProfileSelection.getName(), name, changedValues, new SimpleCallback<Boolean> (){

                @Override
                public void onSuccess(Boolean successful) {
                    if(successful)
                        Console.info("Success: Updated Datasource");
                    else
                        Console.error("Failed: Update datasource " + name);
                }
            });
        }
    }

    public void onSaveXADetails(String name, Map<String, Object> changedValues) {

        getView().enableXADetails(false);

        Console.error("Not implemented");
    }


    public void onCreateNewXADatasource(final XADataSource updatedEntity) {
        window.hide();
        dataSourceStore.createXADataSource(currentProfileSelection.getName(), updatedEntity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean wasSuccessful) {
                if(wasSuccessful)
                    Console.info("Succes: Created XA Datasource "+updatedEntity.getName());

                loadDataSources();
            }
        });
    }

    public void onDisableXA(final XADataSource entity, boolean doEnable) {
        dataSourceStore.enableXADataSource(currentProfileSelection.getName(), entity, doEnable, new SimpleCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {

                if (success) {
                    Console.info("Successfully modified datasource " + entity.getName());
                } else {
                    Console.error("Failed to modify datasource " + entity.getName());
                }

                loadDataSources();
            }
        });
    }

    public void onDeleteXA(final XADataSource entity) {
        dataSourceStore.deleteXADataSource(currentProfileSelection.getName(), entity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {

                if (success) {
                    Console.info("Successfully removed datasource " + entity.getName());
                } else {
                    Console.error("Failed to remove datasource " + entity.getName());
                }

                loadDataSources();
            }
        });
    }
}
