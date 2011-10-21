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
import org.jboss.as.console.client.core.MainLayoutPresenter;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableView;
import org.jboss.as.console.client.domain.events.ProfileSelectionEvent;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
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

    private static final ApplicationHeader PROFILE_HEADER = new ApplicationHeader(Console.CONSTANTS.common_label_profileManagement());
    private final PlaceManager placeManager;
    private ProfileStore profileStore;
    private SubsystemStore subsysStore;
    private ServerGroupStore serverGroupStore;
    private boolean hasBeenRevealed;
    private CurrentProfileSelection currentProfileSelection;

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
            CurrentProfileSelection currentProfileSelection) {

        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.profileStore = profileStore;
        this.subsysStore = subsysStore;
        this.serverGroupStore = serverGroupStore;
        this.currentProfileSelection = currentProfileSelection;
    }


    @Override
    protected void onReveal() {

        super.onReveal();
        if(!hasBeenRevealed)
        {
            hasBeenRevealed = true;

            // load profiles
            profileStore.loadProfiles(new SimpleCallback<List<ProfileRecord>>() {
                @Override
                public void onSuccess(List<ProfileRecord> result) {
                    getView().setProfiles(result);
                }
            });

            // load server groups
            serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {
                @Override
                public void onSuccess(List<ServerGroupRecord> result) {
                    getView().setServerGroups(result);
                }
            });

            if(NameTokens.ProfileMgmtPresenter.equals(placeManager.getCurrentPlaceRequest().getNameToken()))
            {
                Timer t = new Timer() {
                    @Override
                    public void run() {
                        revealDefaultSubsystem();
                    }
                };

                t.schedule(250);
            }
        }
    }

    private void revealDefaultSubsystem() {
        placeManager.revealRelativePlace(new PlaceRequest(NameTokens.DataSourcePresenter));
    }

    @Override
    protected void onReset() {
        super.onReset();
        Console.MODULES.getHeader().highlight(NameTokens.ProfileMgmtPresenter);
        Console.MODULES.getHeader().setContent(PROFILE_HEADER);

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

        currentProfileSelection.setName(profileName);
        subsysStore.loadSubsystems(profileName, new SimpleCallback<List<SubsystemRecord>>() {
            @Override
            public void onSuccess(List<SubsystemRecord> result) {
                getView().setSubsystems(result);
                revealDefaultSubsystem();
            }
        });
    }
}
