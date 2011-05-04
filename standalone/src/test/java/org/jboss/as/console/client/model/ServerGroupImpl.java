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

import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class ServerGroupImpl implements ServerGroupRecord {

    String groupName;
    String profileName;
    Jvm jvm;
    String socketBinding;
    private List<PropertyRecord> props = Collections.EMPTY_LIST;

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void setGroupName(String name) {
        this.groupName = name;
    }

    @Override
    public void setProfileName(String name) {
        this.profileName = name;
    }

    @Override
    public String getProfileName() {
        return profileName;
    }

    @Override
    public void setProperties(List<PropertyRecord> props) {
        this.props = props;
    }

    @Override
    public List<PropertyRecord>  getProperties() {
        return props;
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
    public String getSocketBinding() {
        return socketBinding;
    }

    @Override
    public void setSocketBinding(String socketBinding) {
        this.socketBinding = socketBinding;
    }
}
