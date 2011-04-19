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
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.LHSHighlightEvent;

import java.util.List;


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
    private DataSourceStore dataSourceStore;

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
            CurrentSelectedProfile currentProfile,
            DataSourceStore dataSourceStore) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.currentProfile = currentProfile;
        this.dataSourceStore = dataSourceStore;
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

        String profile = currentProfile.getName() == null ? "default" : currentProfile.getName();

        dataSourceStore.loadDataSources(profile, new AsyncCallback<List<DataSource>>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Failed to load datasource", caught);
            }

            @Override
            public void onSuccess(List<DataSource> result) {
                getView().updateDataSources(result);
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

        dataSourceStore.createDataSource(currentProfile.getName(), datasource, new SimpleCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {
                if(success)
                    loadDataSources();
                else
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to create datasource", Message.Severity.Error)
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

    public void onDelete(final DataSource entity) {

        dataSourceStore.deleteDataSource(currentProfile.getName(), entity, new SimpleCallback<Boolean>(){
            @Override
            public void onSuccess(Boolean success) {

                if(success)
                {
                    loadDataSources();
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Successfully removed datasource " + entity.getName())
                    );
                }
                else
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to remove datasource " + entity.getName())
                    );
                }
            }
        });
    }

    // TODO: https://issues.jboss.org/browse/JBAS-9341
    public void onDisable(final DataSource entity, boolean isEnabled) {
        dataSourceStore.enableDataSource(currentProfile.getName(), entity, isEnabled, new SimpleCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean success) {

                if(success)
                {
                    loadDataSources();
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Successfully modified datasource " + entity.getName())
                    );
                }
                else
                {
                    Console.MODULES.getMessageCenter().notify(
                            new Message("Failed to modify datasource" + entity.getName())
                    );
                }
            }
        });
    }

    public void closeDialogue() {
        window.hide();
    }
}
