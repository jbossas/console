/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.widgets.forms;

/**
 * Represents the {@link Binding} meta data
 *
 * @author Heiko Braun
 * @date 4/19/11
 */
public class PropertyBinding {
    private String detypedName;
    private String javaName;

    public PropertyBinding(String javaName, String detypedName) {
        this.detypedName = detypedName;
        this.javaName = javaName;
    }

    public String getDetypedName() {
        return detypedName;
    }

    public void setDetypedName(String detypedName) {
        this.detypedName = detypedName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    @Override
    public String toString() {
        return javaName+">"+detypedName;
    }
}
