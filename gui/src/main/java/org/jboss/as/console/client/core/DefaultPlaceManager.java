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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.core.message.MessageCenter;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class DefaultPlaceManager extends PlaceManagerImpl {

    private MessageCenter messageCenter;
    private boolean discardPlaceRequest = true;

    @Inject
    public DefaultPlaceManager(
            EventBus eventBus,
            TokenFormatter tokenFormatter, MessageCenter messageCenter ) {
        super(eventBus, tokenFormatter);
        this.messageCenter = messageCenter;
    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {

        // TODO: beware of XSS
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendEscaped(invalidHistoryToken);


        messageCenter.notify(
                new Message("Could not reveal: "+builder.toSafeHtml().asString(),
                        Message.Severity.Fatal)
        );

        if(discardPlaceRequest)
        {
            Log.debug("Discard \"" + invalidHistoryToken + "\". Fallback to default place");
            revealUnauthorizedPlace(null);
        }
    }

    public void revealDefaultPlace() {
        discardPlaceRequest = false;
        revealPlace( new PlaceRequest(NameTokens.mainLayout) );
    }

    @Override
    public void revealUnauthorizedPlace(String unauthorizedHistoryToken) {

        // TODO: beware of XSS

        discardPlaceRequest = false;
        revealPlace( new PlaceRequest(NameTokens.signInPage) );
    }
}
