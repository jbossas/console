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
