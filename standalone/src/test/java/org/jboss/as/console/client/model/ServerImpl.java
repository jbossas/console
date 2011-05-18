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

package org.jboss.as.console.client.model;

import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.domain.model.Server;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class ServerImpl implements Server {

    String name;
    String group;
    boolean isAutoStart;
    boolean isStarted;
    String socketBinding;
    int portOffset;
    Jvm jvm;
    private List<PropertyRecord> props;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean isAutoStart() {
        return isAutoStart;
    }

    @Override
    public void setAutoStart(boolean b) {
        this.isAutoStart = b;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void setStarted(boolean b) {
        this.isStarted =b;
    }

    @Override
    public String getSocketBinding() {
        return socketBinding;
    }

    @Override
    public void setSocketBinding(String socketBindingRef) {
        this.socketBinding = socketBindingRef;
    }

    @Override
    public int getPortOffset() {
        return portOffset;
    }

    @Override
    public void setPortOffset(int offset) {
        this.portOffset = offset;
    }

    @Override
    public Jvm getJvm() {
        return jvm;
    }

    @Override
    public void setJvm(Jvm jvm) {
        this.jvm = jvm;
    }

    @Override
    public List<PropertyRecord> getProperties() {
        return props;
    }

    @Override
    public void setProperties(List<PropertyRecord> props) {
        this.props = props;
    }
}
