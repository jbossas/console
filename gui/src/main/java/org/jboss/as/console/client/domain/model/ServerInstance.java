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

package org.jboss.as.console.client.domain.model;

import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public interface ServerInstance {


    String getName();
    void setName(String name);

    boolean isRunning();
    void setRunning(boolean b);

    String getServer();
    void setServer(String server);

    String getGroup();
    void setGroup(String group);

    ServerFlag getFlag();
    void setFlag(ServerFlag state);

    @Binding(skip = true)
    String getHost();
    void setHost(String hostName);

    // -- runtime data


    @Binding(skip = true)
    Map<String,String> getInterfaces();
    void setInterfaces(Map<String,String> interfaces);

    @Binding(skip = true)
    Map<String,String> getSocketBindings();
    void setSocketBindings(Map<String,String> sockets);

    @Binding(skip = true)
    String getProfile();
    void setProfile(String name);

}
