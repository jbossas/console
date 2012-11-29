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
package org.jboss.as.console.client.core;

import com.google.gwt.core.client.GWT;

/**
 * @author Harald Pehl
 * @date 11/29/2012
 */
public class EnumLabelLookup
{
    public static final EnumLabels ENUM_LABELS = GWT.create(EnumLabels.class);

    /**
     * Returns a label for the specified enum constant. Assumes that there is matching constant defined. Does not work
     * with nested enums!
     *
     * <p>For the enum</p>
     * <pre>
     *     package org.jboss.as.console.client;
     *
     *     enum Color {
     *         green, red, blue
     *     }
     * </pre>
     *
     * <p>You have to specify the following constants</p>
     * <ul>
     *     <li>org_jboss_as_console_client_Color_green</li>
     *     <li>org_jboss_as_console_client_Color_red</li>
     *     <li>org_jboss_as_console_client_Color_blue</li>
     * </ul>
     *
     * @param enm
     * @param <E>
     *
     * @return
     */
    public static <E extends Enum<E>> String labelFor(Enum<E> enm)
    {
        String label = "n/a";
        if (enm != null)
        {
            String key = enm.getClass().getName() + "." + enm.name();
            label = ENUM_LABELS.getString(key.replace('.', '_'));
            if (label == null || label.length() == 0)
            {
                label = "Missing label for '" + key + "'";
            }
        }
        return label;
    }
}
