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

package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Timer;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ProfileMgmtPresenter
        extends Presenter<ProfileMgmtPresenter.MyView, ProfileMgmtPresenter.MyProxy>
        implements ProfileSelectionEvent.ProfileSelectionListener {

    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private SubsystemStore subsysStore;
    private ServerGroupStore serverGroupStore;
    private boolean hasBeenRevealed;
    private CurrentProfileSelection profileSelection;
    private BootstrapContext bootstrap;
    private String lastSubPlace;

    @ProxyCodeSplit
    @NameToken(NameTokens.ProfileMgmtPresenter)
    public interface MyProxy extends Proxy<ProfileMgmtPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setProfiles(List<ProfileRecord> profileRecords);
        void setSubsystems(List<SubsystemRecord> subsystemRecords);
        void setServerGroups(List<ServerGroupRecord> serverGroupRecords);
    }

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> TYPE_MainContent =
            new GwtEvent.Type<RevealContentHandler<?>>();

    @Inject
    public ProfileMgmtPresenter(
            EventBus eventBus,
            MyView view, MyProxy proxy,
            PlaceManager placeManager, ProfileStore profileStore,
            SubsystemStore subsysStore,
            ServerGroupStore serverGroupStore,
            CurrentProfileSelection currentProfileSelection, BootstrapContext bootstrap) {

        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.subsysStore = subsysStore;
        this.serverGroupStore = serverGroupStore;
        this.profileSelection = currentProfileSelection;
        this.bootstrap = bootstrap;
    }



    @Override
    protected void onReset() {

        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.ProfileMgmtPresenter);

        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();
        if(!currentToken.equals(getProxy().getNameToken()))
        {
           lastSubPlace = currentToken;
        }
        else if(lastSubPlace!=null)
        {
            placeManager.revealPlace(new PlaceRequest(lastSubPlace));
        }

        if(!hasBeenRevealed)
        {
            hasBeenRevealed = true;

            // load profiles
            profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {
                @Override
                public void onSuccess(List<ProfileRecord> result) {

                    // default profile
                    if(!result.isEmpty())
                    {
                        selectDefaultProfile(result);
                    }

                    getView().setProfiles(result);

                    Timer t = new Timer() {
                        @Override
                        public void run() {
                            highlightLHSNav();
                        }
                    };

                    t.schedule(150);
                }
            });
        }

        // load server groups
        serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
            @Override
            public void onSuccess(List<ServerGroupRecord> result) {
                getView().setServerGroups(result);
            }
        });
    }

    @Override
    protected void onHide() {
        super.onHide();

    }

    private void selectDefaultProfile(List<ProfileRecord> result) {
        if(!profileSelection.isSet())
        {
            String name = result.get(0).getName();
            System.out.println("Default profile selection: "+name);
            profileSelection.setName(name);
            getEventBus().fireEvent(new ProfileSelectionEvent(name));
        }
    }

    private void revealDefaultSubsystem(List<SubsystemRecord> existingSubsystems) {

        final String[] defaultSubsystem = SubsystemMetaData.getDefaultSubsystem(
                NameTokens.DataSourcePresenter, existingSubsystems
        );

        placeManager.revealPlace(new PlaceRequest(defaultSubsystem[1]));
    }


    private void highlightLHSNav()
    {
        if(bootstrap.getInitialPlace()!=null)
        {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    Console.MODULES.getEventBus().fireEvent(
                            new LHSHighlightEvent(bootstrap.getInitialPlace())
                    );

                    bootstrap.setInitialPlace(null);
                }
            });
        }

    }

    @Override
    protected void revealInParent() {
        // reveal in main layout
        RevealContentEvent.fire(getEventBus(), MainLayoutPresenter.TYPE_MainContent, this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getEventBus().addHandler(ProfileSelectionEvent.TYPE, this);

    }

    @Override
    public void onProfileSelection(String profileName) {

        profileSelection.setName(profileName);

        subsysStore.loadSubsystems(profileName, new SimpleCallback<List<SubsystemRecord>>() {
            @Override
            public void onSuccess(List<SubsystemRecord> result) {
                getView().setSubsystems(result);

                if(placeManager.getCurrentPlaceRequest().getNameToken().equals(NameTokens.ProfileMgmtPresenter))
                    revealDefaultSubsystem(result);
                else
                    placeManager.revealCurrentPlace();

            }
        });

    }
}
