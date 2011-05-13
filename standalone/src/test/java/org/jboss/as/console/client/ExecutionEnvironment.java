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

package org.jboss.as.console.client;

import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.core.ApplicationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class ExecutionEnvironment implements ApplicationProperties {
    private Map<String,String> ctx = new HashMap<String,String>();

    public ExecutionEnvironment() {
        setProperty(ApplicationProperties.STANDALONE, "false");
    }

    @Override
    public void setProperty(String key, String value)
    {
        ctx.put(key, value);
    }

    @Override
    public String getProperty(String key)
    {
        return ctx.get(key);
    }

    @Override
    public boolean hasProperty(String key)
    {
        return getProperty(key)!=null;
    }

    public PlaceRequest getDefaultPlace() {

        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeProperty(String key) {

        ctx.remove(key);
    }

}
