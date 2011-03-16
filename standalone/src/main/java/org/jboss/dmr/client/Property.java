/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.dmr.client;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Property implements Cloneable {
    private final String name;
    private final ModelNode value;

    public Property(final String name, final ModelNode value) {
        this(name, value, true);
    }

    Property(final String name, final ModelNode value, final boolean copy) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        this.name = name;
        this.value = copy ? value.clone() : value;
    }

    public String getName() {
        return name;
    }

    public ModelNode getValue() {
        return value;
    }

    public Property clone() {
        return new Property(name, value.clone());
    }
}
