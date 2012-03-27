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

package org.jboss.as.console.client.core;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.ballroom.client.layout.LHSHighlightEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class DefaultPlaceManager extends PlaceManagerImpl {

    private MessageCenter messageCenter;
    private BootstrapContext bootstrap;
    private EventBus eventBus;

    @Inject
    public DefaultPlaceManager(
            EventBus eventBus,
            TokenFormatter tokenFormatter, MessageCenter messageCenter, BootstrapContext bootstrap ) {
        super(eventBus, tokenFormatter);
        this.messageCenter = messageCenter;
        this.bootstrap = bootstrap;
        this.eventBus = eventBus;
    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {

        Log.debug("Discard \"" + invalidHistoryToken + "\". Fallback to default place");
        revealDefaultPlace();
    }

    public void revealDefaultPlace() {

        List<PlaceRequest> places = new ArrayList<PlaceRequest>();
        places.add(bootstrap.getDefaultPlace());

        revealPlaceHierarchy(places);
    }

    @Override
    protected void doRevealPlace(final PlaceRequest request, boolean updateBrowserUrl) {
        super.doRevealPlace(request, updateBrowserUrl);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                eventBus.fireEvent(
                        new LHSHighlightEvent(request.getNameToken())
                );
            }
        });

    }

    @Override
    public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {

        // for now this is only used to prevent access to screen that don't work in either execution mode
        // i.e. domain screens accessed on a standalone server instance (linked from external URL)
        // hence we can safely redirect to a default place.
        // Once we move to actual authorization concept this practice needs to be reconsidered
        Log.debug("Unauthorized place: "+unauthorizedHistoryToken+". Fallback to default place.");

        revealDefaultPlace();

    }
}
