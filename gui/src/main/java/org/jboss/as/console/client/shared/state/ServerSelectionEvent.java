package org.jboss.as.console.client.shared.state;

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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.jboss.as.console.client.domain.model.ServerInstance;


/**
 *
 * @see CurrentServerSelection
 *
 * @author Heiko Braun
 * @date 2/7/11
 */
public class ServerSelectionEvent extends GwtEvent<ServerSelectionEvent.ServerSelectionListener> {

    public static final Type TYPE = new Type<ServerSelectionListener>();

    private String hostName;
    private ServerInstance server;

    public static enum Source { Picker, Other }

    private Source source;

    public ServerSelectionEvent(String hostName, ServerInstance server) {
        super();
        this.hostName = hostName;
        this.server = server;
        this.source = Source.Other;
    }

    public ServerSelectionEvent(String hostName, ServerInstance server, Source src) {
            super();
            this.hostName = hostName;
            this.server = server;
            this.source = src;
        }

    public Source getSource() {
        return source;
    }

    @Override
    public Type<ServerSelectionListener> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ServerSelectionListener listener) {
        listener.onServerSelection(hostName, server, source);
    }

    public String getHostName() {
        return hostName;
    }

    public ServerInstance getServer() {
        return server;
    }

    public interface ServerSelectionListener extends EventHandler {
        void onServerSelection(String hostName, ServerInstance server, Source source);
    }
}

