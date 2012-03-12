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
package org.jboss.as.console.client.shared.subsys.osgi.runtime;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.general.MessageWindow;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.CurrentServerSelection;
import org.jboss.as.console.client.shared.state.ServerSelectionEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.osgi.runtime.model.OSGiBundle;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class OSGiRuntimePresenter extends Presenter<OSGiRuntimePresenter.MyView, OSGiRuntimePresenter.MyProxy>
        implements ServerSelectionEvent.ServerSelectionListener  {
    private final BeanMetaData bundleMetaData;
    private final DispatchAsync dispatcher;
    private final RevealStrategy revealStrategy;
    private CurrentServerSelection serverSelection;

    @ProxyCodeSplit
    @NameToken(NameTokens.OSGiRuntimePresenter)
    public interface MyProxy extends Proxy<OSGiRuntimePresenter>, Place {
    }

    public interface MyView extends View {
        void initialLoad();
        void setPresenter(OSGiRuntimePresenter osGiRuntimePresenter);
    }

    @Inject
    public OSGiRuntimePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, ApplicationMetaData propertyMetaData,
            RevealStrategy revealStrategy, CurrentServerSelection serverSelection) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.bundleMetaData = propertyMetaData.getBeanMetaData(OSGiBundle.class);
        this.serverSelection = serverSelection;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ServerSelectionEvent.TYPE, this);
    }

    @Override
    protected void onReset() {
        super.onReset();

        if(serverSelection.isActive())
            getView().initialLoad();

    }

    @Override
    public void onServerSelection(String hostName, ServerInstance server) {

         Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if(isVisible()) getView().initialLoad();
            }
         });
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInRuntimeParent(this);
    }

    void startBundle(OSGiBundle bundle) {
        bundleAction(bundle, "start");
    }

    void stopBundle(OSGiBundle bundle) {
        bundleAction(bundle, "stop");
    }

    private void bundleAction(OSGiBundle bundle, String operationName) {

        AddressBinding address = bundleMetaData.getAddress();
        ModelNode operation = address.asResource(RuntimeBaseAddress.get(), bundle.getName());
        operation.get(ModelDescriptionConstants.OP).set(operationName);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {

                ModelNode response = result.get();
                if(response.isFailure())
                {
                    Console.error(Console.MESSAGES.modificationFailed("OSGi Bundle"), response.getFailureDescription());
                }
                else
                {
                    Console.info(Console.MESSAGES.modified("OSGi Bundle"));
                }
                getView().initialLoad();
            }
        });
    }

    public void askToActivateSubsystem() {
        final DefaultWindow window = new DefaultWindow(Console.CONSTANTS.subsys_osgi());
        window.setWidth(320);
        window.setHeight(140);
        window.trapWidget(new MessageWindow(Console.MESSAGES.subsys_osgi_activate(),
                new MessageWindow.Result() {
                    @Override
                    public void result(boolean result) {
                        window.hide();
                        if (result)
                            activateSubsystem();
                    }
                }).asWidget());
        window.setGlassEnabled(true);
        window.center();
    }

    protected void activateSubsystem() {
        // Since it takes a few moments for the subsystem to activate we're showing a window indicating this
        final DefaultWindow window = new DefaultWindow(Console.CONSTANTS.subsys_osgi());
        window.setWidth(320);
        window.setHeight(140);
        window.trapWidget(new HTML(Console.MESSAGES.subsys_osgi_activating()));
        window.setGlassEnabled(true);
        window.center();

        AddressBinding address = bundleMetaData.getAddress();
        ModelNode operation = address.asSubresource(RuntimeBaseAddress.get()); // get an operation on the parent address...
        operation.get(ModelDescriptionConstants.OP).set("activate");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Timer t = new Timer() {
                    @Override
                    public void run() {
                        window.hide();
                        onReset();
                    }
                };
                t.schedule(4000);
            }

            @Override
            public void onFailure(Throwable caught) {
                window.hide();
                super.onFailure(caught);
            }
        });
    }
}
