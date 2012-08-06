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
package org.jboss.as.console.client.shared.runtime.naming;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @author David Bosschaert
 */
class JndiEntry {
    private final String dataType;
    private final String name;
    private String value ="";
    private final String uri;
    private final List<JndiEntry> children;

    JndiEntry(String name, String uri, String dataType) {
        this.name = name;
        this.uri = uri;
        this.dataType = dataType;
        this.children = new ArrayList<JndiEntry>();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (!String.class.getName().equals(dataType)) {
            // Don't truncate plain String values
            int idx = value.lastIndexOf(".");

            if(value!=null && idx>0) {
                value = value.substring(idx+1, value.length());
            } else if (value.length()>50) {
                value = value.substring(0, 50 )+" ...";
            }
        }

        this.value = value;
    }

    public List<JndiEntry> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }
}