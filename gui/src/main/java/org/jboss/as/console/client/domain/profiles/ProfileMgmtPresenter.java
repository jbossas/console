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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
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
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.shared.model.SubsystemStore;

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
    private boolean hasBeenRevealed;
    private CurrentProfileSelection profileSelection;

    private String lastPlace;
    private BootstrapContext bootstrap;

    @ProxyCodeSplit
    @NameToken(NameTokens.ProfileMgmtPresenter)
    public interface MyProxy extends Proxy<ProfileMgmtPresenter>, Place {
    }

    public interface MyView extends SuspendableView {
        void setProfiles(List<ProfileRecord> profileRecords);
        void setSubsystems(List<SubsystemRecord> subsystemRecords);
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
        this.profileSelection = currentProfileSelection;
        this.bootstrap = bootstrap;
    }



    @Override
    protected void onReset() {

        super.onReset();

        Console.MODULES.getHeader().highlight(NameTokens.ProfileMgmtPresenter);

        // default init when revealed the first time
        if(!hasBeenRevealed)
        {
            hasBeenRevealed = true;
            loadProfiles();
        }

        // chose sub place to reveal
        String currentToken = placeManager.getCurrentPlaceRequest().getNameToken();

        // already sub pace chosen (token in URL)
        if(!getProxy().getNameToken().equals(currentToken))
        {
            lastPlace = currentToken;
        }

        // no token in URL (top level nav)
        else
        {
            if(lastPlace!=null)
            {
                placeManager.revealPlace(new PlaceRequest(lastPlace));
            }
            else
            {
                // no token and no last place given
                subsysStore.loadSubsystems(profileSelection.getName(), new SimpleCallback<List<SubsystemRecord>>() {
                    @Override
                    public void onSuccess(List<SubsystemRecord> existingSubsystems) {
                        revealDefaultSubsystem(NameTokens.DataSourcePresenter, existingSubsystems);
                    }
                });
            }
        }

    }

    private void loadProfiles() {
         // load profiles
            profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {
                @Override
                public void onSuccess(final List<ProfileRecord> result) {

                    getView().setProfiles(result);

                    /*Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            // default profile
                            if(!result.isEmpty())
                            {
                                selectDefaultProfile(result);
                            }

                            Timer t = new Timer() {
                                @Override
                                public void run() {
                                    highlightLHSNav();
                                }
                            };

                            t.schedule(150);
                        }
                    });*/

                }
            });
    }

    @Override
    protected void onHide() {
        super.onHide();

    }

    private void revealDefaultSubsystem(String preference, List<SubsystemRecord> existingSubsystems) {

        final String[] defaultSubsystem = SubsystemMetaData.getDefaultSubsystem(
                preference, existingSubsystems
        );

        Log.debug("reveal default subsystem : pref "+ preference + "; chosen "+defaultSubsystem[1]);

        placeManager.revealPlace(new PlaceRequest(defaultSubsystem[1]));
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

        if(!isVisible()) return;

        Log.debug("onProfileSelection: "+profileName + "/ "+placeManager.getCurrentPlaceRequest().getNameToken());

        profileSelection.setName(profileName);

        subsysStore.loadSubsystems(profileName, new SimpleCallback<List<SubsystemRecord>>() {
            @Override
            public void onSuccess(List<SubsystemRecord> result) {
                getView().setSubsystems(result);

                // prefer to reveal the last place, if exists in selected profile
                String preference = lastPlace!=null ? lastPlace : NameTokens.DataSourcePresenter;
                revealDefaultSubsystem(preference, result);
            }
        });

    }
}
