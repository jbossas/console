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

package org.jboss.as.console.client.domain.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ServerGroupSelectionEvent extends GwtEvent<ServerGroupSelectionEvent.ServerGroupSelectionListener> {

    public static final Type TYPE = new Type<ServerGroupSelectionListener>();

    private String serverGroupName;

    public ServerGroupSelectionEvent(String ServerGroupName) {
        super();
        this.serverGroupName = ServerGroupName;
    }

    @Override
    public Type<ServerGroupSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ServerGroupSelectionListener listener) {
        listener.onServerGroupSelection(serverGroupName);
    }

    public String getServerGroupName() {
        return serverGroupName;
    }

    public interface ServerGroupSelectionListener extends EventHandler {
        void onServerGroupSelection(String ServerGroupName);
    }
}

