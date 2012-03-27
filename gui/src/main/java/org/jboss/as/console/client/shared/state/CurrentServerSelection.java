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

package org.jboss.as.console.client.shared.state;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerInstance;

import javax.inject.Singleton;

/**
 * @see ServerSelectionEvent
 *
 * @author Heiko Braun
 * @date 5/17/11
 */
@Singleton
public class CurrentServerSelection {

    private ServerInstance server;
    private String host;

    public ServerInstance getServer() {
        return server;
    }

    public void setServer(ServerInstance server) {
        this.server = server;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isSet() {
        return this.server!=null
                && this.host!=null;
    }

    public boolean hasSetHost() {
        return this.host!=null;
    }

    public boolean hasSetServer() {
        return this.server!=null;
    }

    public boolean isActive() {
        boolean standalone = Console.getBootstrapContext().isStandalone();
        return standalone || (hasSetServer() && server.isRunning());
    }
}
