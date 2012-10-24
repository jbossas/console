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

package org.jboss.as.console.client.shared;

import com.google.gwt.user.client.Cookies;

import java.util.Date;

/**
 * Cookie based workspace preferences
 */
public class Preferences
{

    private static final String AS7_UI = "as7_ui_";

    public enum Key {

        LOCALE("locale", "Locale", "en"),
        USE_CACHE("useCache", "Use Cache", "false"),
        DISBALE_ANALYTICS("disableAnalytics", "Disable Analytics", "false");

        private String token;
        private String title;
        private Object defaultValue;

        private Key(String token, String title, Object defaultValue) {
            this.token = token;
            this.title = title;
            this.defaultValue = defaultValue;
        }

        public String getToken() {
            return token;
        }

        public String getTitle() {
            return title;
        }

        public static Key match(String token) {

            Key match = null;
            for(Key key : values())
            {
                if(token.equals(key.getToken()))
                {
                    match = key;
                    break;
                }
            }

            return match;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static boolean has(Key key)
    {
        return get(key)!=null;
    }

    public static String get(Key key, String defaultValue)
    {
        String cookie = get(key);
        if(null==cookie) cookie = defaultValue;
        return cookie;
    }

    public static String get(Key key)
    {
        return Cookies.getCookie(AS7_UI +key.getToken());
    }

    public static void set(Key key, String value)
    {
        Date twoWeeks = new Date(System.currentTimeMillis()+(2*604800*1000));
        Cookies.setCookie(AS7_UI +key.getToken(), value, twoWeeks);
    }

    public static void clear(Key key)
    {
        Cookies.removeCookie(AS7_UI +key.getToken());
    }
}
