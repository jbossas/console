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

import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;

/**
 * @author Heiko Braun
 * @date 4/19/11
 */
public class DataSourceImpl implements DataSource {
    
    String name;
    String connectionUrl;
    String driverClass;
    String driverName;
    String jndiName;
    boolean enabled;
    String username;
    String password;
    String poolname;
    String version;
    int major, minor;
    boolean ccm;
    boolean jta;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public void setConnectionUrl(String url) {
        this.connectionUrl = url;
    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public String getDriverName() {
        return driverName;
    }

    @Override
    public void setDriverName(String driver) {
        this.driverName = driver;
    }

    @Override
    public String getJndiName() {
        return jndiName;
    }

    @Override
    public void setJndiName(String name) {
        this.jndiName = name;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String user) {
        this.username = user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPoolName() {
        return poolname;
    }

    @Override
    public void setPoolName(String name) {
        this.poolname = name;
    }

    @Override
    public int getMajorVersion() {
        return major;
    }

    @Override
    public void setMajorVersion(int major) {
        this.major = major;
    }

    @Override
    public int getMinorVersion() {
        return minor;
    }

    @Override
    public void setMinorVersion(int minor) {
        this.minor = minor;
    }

    public boolean isJta(){return false;}
    public void setJta(boolean b) {}

    public boolean isCcm() {return  false;}
    public void setCcm(boolean b) {}

}
