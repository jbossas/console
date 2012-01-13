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

package org.jboss.as.console.client.standalone;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * A collection of tools to manage a standalone server instance.
 *
 * @author Heiko Braun
 * @date 1/28/11
 */
public class ServerMgmtApplicationPresenter extends Presenter<ServerMgmtApplicationPresenter.ServerManagementView,
        ServerMgmtApplicationPresenter.ServerManagementProxy> {

    private PlaceManager placeManager;
    private SubsystemStore subsysStore;
    private boolean hasBeenRevealed;
    private boolean matchCurrent;

    public interface ServerManagementView extends View {

        void updateFrom(List<SubsystemRecord> subsystemRecords);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.serverConfig)
    public interface ServerManagementProxy extends ProxyPlace<ServerMgmtApplicationPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent = new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ServerMgmtApplicationPresenter(
            EventBus eventBus, ServerManagementView view,
            ServerManagementProxy proxy, PlaceManager placeManager,
            SubsystemStore subsysStore) {
        super(eventBus, view, proxy);
        this.placeManager = placeManager;
        this.subsysStore = subsysStore;
    }

    /**
     * Load a default sub page upon first reveal
     * and highlight navigation sections in subsequent requests.
     *
     * @param request
     */
    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);

        // is the presenter itself requested, or nested children?
        matchCurrent = NameTokens.serverConfig.equals(request.getNameToken());
    }



    @Override
    protected void onReset() {
        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.serverConfig);

        if(!hasBeenRevealed)
        {
            Console.MODULES.getHeader().highlight(NameTokens.serverConfig);

            subsysStore.loadSubsystems("default", new SimpleCallback<List<SubsystemRecord>>() {
                @Override
                public void onSuccess(List<SubsystemRecord> existingSubsystems) {

                    getView().updateFrom(existingSubsystems);

                    // chose default view if necessary

                    if(placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.serverConfig))
                    {
                        final String[] defaultSubsystem = SubsystemMetaData.getDefaultSubsystem(
                                NameTokens.DataSourcePresenter, existingSubsystems
                        );

                        placeManager.revealRelativePlace(new PlaceRequest(defaultSubsystem[1]));

                        Timer t = new Timer() {
                            @Override
                            public void run() {

                                //link.setSelected(true);

                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand(){
                                    @Override
                                    public void execute() {
                                        getEventBus().fireEvent(
                                                new LHSHighlightEvent(null, defaultSubsystem[0], "profiles")
                                        );
                                    }
                                });
                            }
                        };
                        t.schedule(500);
                    }
                }
            });

            hasBeenRevealed = true;
        }
    }

    @Override
    protected void onBind() {
        super.onBind();
    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }
}
