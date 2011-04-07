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

package org.jboss.as.console.client.shared;

import com.google.gwt.user.client.Cookies;

import java.util.Date;

/**
 * Cookie based workspace preferences
 */
public class Preferences
{
    public static boolean has(String key)
    {
        return get(key)!=null;
    }

    public static String get(String key, String defaultValue)
    {
        String cookie = get(key);
        if(null==cookie) cookie = defaultValue;
        return cookie;
    }

    public static String get(String key)
    {
        return Cookies.getCookie(key);
    }

    public static void set(String key, String value)
    {
        Date twoWeeks = new Date(System.currentTimeMillis()+(2*604800*1000));
        Cookies.setCookie(key, value, twoWeeks);
    }

    public static void clear(String key)
    {
        Cookies.removeCookie(key);
    }
}
