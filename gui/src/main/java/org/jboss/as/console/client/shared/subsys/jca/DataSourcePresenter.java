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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.ResponseWrapper;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSourceStore;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverRegistry;
import org.jboss.as.console.client.shared.subsys.jca.model.DriverStrategy;
import org.jboss.as.console.client.shared.subsys.jca.model.JDBCDriver;
import org.jboss.as.console.client.shared.subsys.jca.model.PoolConfig;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewDatasourceWizard;
import org.jboss.as.console.client.shared.subsys.jca.wizard.NewXADatasourceWizard;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;


/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DataSourcePresenter extends Presenter<DataSourcePresenter.MyView, DataSourcePresenter.MyProxy>
        implements PropertyManagement {

    private boolean hasBeenRevealed = false;
    private DefaultWindow window;

    private DataSourceStore dataSourceStore;
    private DriverStrategy driverRegistry;
    private RevealStrategy revealStrategy;
    private ApplicationProperties bootstrap;
    private DefaultWindow propertyWindow;

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
        void setPoolConfig(String name, PoolConfig poolConfig);
        void setXAPoolConfig(String dsName, PoolConfig underlying);
        void setXAProperties(String dataSourceName, List<PropertyRecord> result);
        void setConnectionVerified(boolean b, String dataSourceName);
        void setConnectionProperties(String reference, List<PropertyRecord> properties);

    }

    @Inject
    public DataSourcePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DataSourceStore dataSourceStore, DriverRegistry driverRegistry,
            RevealStrategy revealStrategy, ApplicationProperties bootstrap) {
        super(eventBus, view, proxy);

        this.dataSourceStore = new DataSourceStoreInterceptor(dataSourceStore);
        this.driverRegistry = driverRegistry.create();
        this.revealStrategy = revealStrategy;
        this.bootstrap = bootstrap;
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
        loadXADataSources();

        if(!hasBeenRevealed)
        {
            hasBeenRevealed=true;
        }
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
    }

    private void loadDataSources() {
        dataSourceStore.loadDataSources(new SimpleCallback<List<DataSource>>() {
            @Override
            public void onSuccess(List<DataSource> result) {
                getView().updateDataSources(result);
            }
        });

    }

    private void loadXADataSources() {
        dataSourceStore.loadXADataSources(new SimpleCallback<List<XADataSource>>() {

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

                if(drivers.size()>0)
                {
                    window = new DefaultWindow(Console.MESSAGES.createTitle("Datasource"));
                    window.setWidth(480);
                    window.setHeight(450);

                    window.setWidget(
                            new NewDatasourceWizard(DataSourcePresenter.this, drivers, bootstrap).asWidget()
                    );

                    window.setGlassEnabled(true);
                    window.center();
                }
                else {

                    SafeHtmlBuilder html = new SafeHtmlBuilder();
                    html.appendHtmlConstant(Console.CONSTANTS.subsys_jca_datasource_error_loadDriver_desc());
                    Feedback.alert(Console.CONSTANTS.subsys_jca_datasource_error_loadDriver(), html.toSafeHtml());
                }
            }
        });
    }

    public void loadDriver(final AsyncCallback<List<JDBCDriver>> callback) {
        driverRegistry.refreshDrivers(new SimpleCallback<List<JDBCDriver>>() {
            @Override
            public void onSuccess(List<JDBCDriver> drivers) {

                callback.onSuccess(drivers);
            }
        });
    }

    public void launchNewXADatasourceWizard() {

        driverRegistry.refreshDrivers(new SimpleCallback<List<JDBCDriver>>() {
            @Override
            public void onSuccess(List<JDBCDriver> drivers) {

                if(drivers.size()>0)
                {
                    window = new DefaultWindow(Console.MESSAGES.createTitle("XA Datasource"));
                    window.setWidth(480);
                    window.setHeight(450);
                    window.addCloseHandler(new CloseHandler<PopupPanel>() {
                        @Override
                        public void onClose(CloseEvent<PopupPanel> event) {

                        }
                    });

                    window.setWidget(
                            new NewXADatasourceWizard(DataSourcePresenter.this, drivers, bootstrap).asWidget()
                    );

                    window.setGlassEnabled(true);
                    window.center();
                }
                else {
                    SafeHtmlBuilder html = new SafeHtmlBuilder();
                    html.appendHtmlConstant(Console.CONSTANTS.subsys_jca_datasource_error_loadDriver_desc());
                    Feedback.alert(Console.CONSTANTS.subsys_jca_datasource_error_loadDriver(), html.toSafeHtml());
                }
            }
        });

    }


    public void onCreateDatasource(final DataSource datasource) {
        window.hide();

        datasource.setEnabled(false);

        // TODO find a proper way to deal with this
        if("".equals(datasource.getUsername()))
            datasource.setUsername(null);
        if("".equals(datasource.getPassword()))
            datasource.setPassword(null);
        if("".equals(datasource.getSecurityDomain()))
            datasource.setSecurityDomain(null);

        dataSourceStore.createDataSource(datasource, new SimpleCallback<ResponseWrapper<Boolean>>() {

            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if (result.getUnderlying()) {
                    Console.info(Console.MESSAGES.added("Datasource ")+ datasource.getName());
                    loadDataSources();
                }
                else
                    Console.error(Console.MESSAGES.addingFailed("Datasource " + datasource.getName()), result.getResponse().toString());
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

        dataSourceStore.deleteDataSource(entity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {

                if (success) {
                    Console.info(Console.MESSAGES.deleted("Datasource ") + entity.getName());
                } else {
                    Console.error(Console.MESSAGES.deletionFailed("Datasource ") + entity.getName());
                }

                loadDataSources();
            }
        });
    }

    public void onDisable(final DataSource entity, boolean doEnable) {
        dataSourceStore.enableDataSource(entity, doEnable, new SimpleCallback<ResponseWrapper<Boolean>>() {

            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if (result.getUnderlying()) {
                    Console.info(Console.MESSAGES.modified("Datasource ") + entity.getName());
                } else {
                    Console.error(Console.MESSAGES.modificationFailed("Datasource ") + entity.getName(), result.getResponse().toString());
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
            dataSourceStore.updateDataSource(name, changedValues, new SimpleCallback<ResponseWrapper<Boolean>> (){

                @Override
                public void onSuccess(ResponseWrapper<Boolean> response) {
                    if(response.getUnderlying())
                        Console.info(Console.MESSAGES.saved("Datasource "+name));
                    else
                        Console.error(Console.MESSAGES.saveFailed("Datasource ") + name, response.getResponse().toString());

                    loadDataSources();
                }

            });
        }
    }

    public void onSaveXADetails(final String name, Map<String, Object> changedValues) {

        getView().enableXADetails(false);
        if(changedValues.size()>0)
        {
            dataSourceStore.updateXADataSource(name, changedValues, new SimpleCallback<ResponseWrapper<Boolean>> (){

                @Override
                public void onSuccess(ResponseWrapper<Boolean> response) {
                    if(response.getUnderlying())
                        Console.info(Console.MESSAGES.saved("XA Datasource "+name));
                    else
                        Console.error(Console.MESSAGES.saveFailed("XA Datasource " + name), response.getResponse().toString());

                    loadXADataSources();
                }
            });
        }
    }


    public void onCreateXADatasource(final XADataSource updatedEntity) {
        window.hide();

        updatedEntity.setEnabled(false);

        if("".equals(updatedEntity.getUsername()))
            updatedEntity.setUsername(null);
        if("".equals(updatedEntity.getPassword()))
            updatedEntity.setPassword(null);
        if("".equals(updatedEntity.getSecurityDomain()))
            updatedEntity.setSecurityDomain(null);

        dataSourceStore.createXADataSource(updatedEntity, new SimpleCallback<ResponseWrapper<Boolean>>() {
            @Override
            public void onSuccess(ResponseWrapper<Boolean> response) {
                if (response.getUnderlying())
                    Console.info(Console.MESSAGES.added("XA Datasource " + updatedEntity.getName()));
                else
                    Console.error(Console.MESSAGES.addingFailed("XA Datasource " + updatedEntity.getName()), response.getResponse().toString());

                loadXADataSources();
            }
        });
    }

    public void onDisableXA(final XADataSource entity, boolean doEnable) {
        dataSourceStore.enableXADataSource(entity, doEnable, new SimpleCallback<ResponseWrapper<Boolean>>()
        {

            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {

                if (result.getUnderlying()) {
                    Console.info(Console.MESSAGES.modified("Datasource " + entity.getName()));
                } else {
                    Console.error(Console.MESSAGES.modificationFailed("Datasource " + entity.getName()), result.getResponse().toString());
                }

                loadXADataSources();
            }
        });
    }

    public void onDeleteXA(final XADataSource entity) {
        dataSourceStore.deleteXADataSource(entity, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {

                if (success) {
                    Console.info(Console.MESSAGES.deleted("Datasource "+ entity.getName()));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed("Datasource " + entity.getName()));
                }

                loadXADataSources();
            }
        });
    }

    public void loadPoolConfig(final boolean isXA, final String dsName) {

        dataSourceStore.loadPoolConfig(isXA, dsName,
                new SimpleCallback<ResponseWrapper<PoolConfig>>() {
                    @Override
                    public void onSuccess(ResponseWrapper<PoolConfig> result) {
                        if(isXA)
                            getView().setXAPoolConfig(dsName, result.getUnderlying());
                        else
                            getView().setPoolConfig(dsName, result.getUnderlying());
                    }
                });
    }

    public void onSavePoolConfig(final String editedName, Map<String, Object> changeset, final boolean isXA) {
        dataSourceStore.savePoolConfig(isXA, editedName, changeset, new SimpleCallback<ResponseWrapper<Boolean>>(){
            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if(result.getUnderlying())
                    Console.info(Console.MESSAGES.saved("Pool Settings "+editedName));
                else
                    Console.error(Console.MESSAGES.saveFailed("Pool Settings "+ editedName), result.getResponse().toString());

                loadPoolConfig(isXA, editedName);
            }
        });
    }

    public void onDeletePoolConfig(final String editedName, PoolConfig entity, final boolean isXA) {

        dataSourceStore.deletePoolConfig(isXA, editedName, new SimpleCallback<ResponseWrapper<Boolean>>(){
            @Override
            public void onSuccess(ResponseWrapper<Boolean> result) {
                if(result.getUnderlying())
                    Console.info(Console.MESSAGES.modified("pool setting "+editedName));
                else
                    Console.error(Console.MESSAGES.modificationFailed("pool setting " + editedName), result.getResponse().toString());

                loadPoolConfig(isXA, editedName);
            }
        });
    }


    public void loadXAProperties(final String dataSourceName) {
        dataSourceStore.loadXAProperties(dataSourceName, new SimpleCallback<List<PropertyRecord>>()
        {
            @Override
            public void onSuccess(List<PropertyRecord> result) {
                getView().setXAProperties(dataSourceName, result);
            }
        });
    }

    public void onCreateXAProperty(final String reference, final PropertyRecord prop) {

        closePropertyDialoge();

        dataSourceStore.createXAConnectionProperty(reference, prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success)
                    Console.info(Console.MESSAGES.added("XA property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.addingFailed("XA property " + prop.getKey()));

                loadXAProperties(reference);
            }
        });
    }

    public void onDeleteXAProperty(final String reference, final PropertyRecord prop) {
        dataSourceStore.deleteXAConnectionProperty(reference, prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if (success)
                    Console.info(Console.MESSAGES.deleted("XA property " + prop.getKey()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("XA property " + prop.getKey()));

                loadXAProperties(reference);
            }
        });
    }

    public void launchNewXAPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("XA property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.trapWidget(
                new NewPropertyWizard(new PropertyManagement() {
                    @Override
                    public void onCreateProperty(String reference, PropertyRecord prop) {
                        onCreateXAProperty(reference, prop);
                    }

                    @Override
                    public void onDeleteProperty(String reference, PropertyRecord prop) {

                    }

                    @Override
                    public void onChangeProperty(String reference, PropertyRecord prop) {

                    }

                    @Override
                    public void launchNewPropertyDialoge(String reference) {

                    }

                    @Override
                    public void closePropertyDialoge() {
                        closeXAPropertyDialoge();
                    }
                }, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    public void closeXAPropertyDialoge() {
        propertyWindow.hide();
    }

    public void verifyConnection(final String dataSourceName, boolean isXA) {

        dataSourceStore.verifyConnection(dataSourceName, isXA, new SimpleCallback<ResponseWrapper<Boolean>>() {
            @Override
            public void onSuccess(ResponseWrapper<Boolean> response) {

                if(response.getUnderlying())
                    Console.info(Console.MESSAGES.successful("Connection settings: "+ dataSourceName));
                else
                    Console.error(Console.MESSAGES.failed( "Connection settings: "+ dataSourceName), response.getResponse().toString());

                getView().setConnectionVerified(response.getUnderlying(), dataSourceName);
            }
        });
    }

    public void onLoadConnectionProperties(final String datasourceName) {
        dataSourceStore.loadConnectionProperties(datasourceName, new SimpleCallback<List<PropertyRecord>>(){

            @Override
            public void onSuccess(List<PropertyRecord> propertyRecords) {
                getView().setConnectionProperties(datasourceName, propertyRecords);
            }
        });
    }

    @Override
    public void onCreateProperty(final String reference, final PropertyRecord prop) {

        closePropertyDialoge();

        dataSourceStore.createConnectionProperty(reference, prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if(success)
                    Console.info(Console.MESSAGES.added("Connection property "+prop.getKey()));
                else
                    Console.error(Console.MESSAGES.addingFailed("Connection property "+prop.getKey()));

                onLoadConnectionProperties(reference);
            }
        });
    }

    @Override
    public void onDeleteProperty(final String reference, final PropertyRecord prop) {
        dataSourceStore.deleteConnectionProperty(reference, prop, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if(success)
                    Console.info(Console.MESSAGES.deleted("Connection property "+prop.getKey()));
                else
                    Console.error(Console.MESSAGES.deletionFailed("Connection property "+prop.getKey()));

                onLoadConnectionProperties(reference);
            }
        });
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // not possible
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Connection Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);
        propertyWindow.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {

            }
        });

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, reference, false).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }

    public void onDoFlush(boolean isXA, String editedName) {
        dataSourceStore.doFlush(isXA, editedName, new SimpleCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                if(success)
                    Console.info(Console.MESSAGES.successful("Flush Pool"));
                else
                    Console.error(Console.MESSAGES.failed("Flush Pool"));
            }
        });
    }

}
