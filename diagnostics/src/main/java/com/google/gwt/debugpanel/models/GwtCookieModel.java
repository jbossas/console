/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.google.gwt.debugpanel.models;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * GWT implementation of the {@link CookieModel}.
 */
public class GwtCookieModel implements CookieModel {
  private static final int REFRESH_TIME = 60 * 1000; // One Minute.

  private Listeners listeners;
  private Map<String, String> cookies;
  private Timer refresher;

  public GwtCookieModel(boolean refresh) {
    listeners = new Listeners();
    cookies = new HashMap<String, String>();
    for (String name : Cookies.getCookieNames()) {
      cookies.put(name, Cookies.getCookie(name));
    }
    if (refresh) {
      refresher = new Timer() {
        @Override
        public void run() {
          refresh();
        }
      };
      refresher.scheduleRepeating(REFRESH_TIME);
    }
  }

  //@Override
  public String[] cookieNames() {
    return cookies.keySet().toArray(new String[cookies.size()]);
  }

  //@Override
  public String getCookie(String name) {
    return Cookies.getCookie(name);
  }

  //@Override
  public void setCookie(
      String name, String value, Date expires, String domain, String path, boolean secure) {
    if (value == null) {
      removeCookie(name);
    } else {
      Cookies.setCookie(name, value, expires, domain, path, secure);
      if (cookies.put(name, value) == null) {
        listeners.cookieAdded(name, value);
      } else {
        listeners.cookieChanged(name, value);
      }
    }
  }

  //@Override
  public void removeCookie(String name) {
    Cookies.removeCookie(name);
    cookies.remove(name);
    listeners.cookieRemoved(name);
  }

  public void refresh() {
    Set<String> current = new HashSet<String>(Cookies.getCookieNames());
    for (Iterator<Map.Entry<String, String>> it = cookies.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<String, String> e = it.next();
      String key = e.getKey();
      String value = e.getValue();
      if (!current.remove(key)) {
        it.remove();
        listeners.cookieRemoved(key);
      } else if (!value.equals(Cookies.getCookie(key))) {
        e.setValue(value = Cookies.getCookie(key));
        listeners.cookieChanged(key, value);
      }
    }
    for (String cookie : current) {
      String value = Cookies.getCookie(cookie);
      cookies.put(cookie, value);
      listeners.cookieAdded(cookie, value);
    }
  }

  //@Override
  public void addCookieListener(CookieModelListener listener) {
    listeners.add(listener);
  }

  //@Override
  public void removeCookieListener(CookieModelListener listener) {
    listeners.remove(listener);
  }

  private static class Listeners 
      extends ArrayList<CookieModelListener> implements CookieModelListener {
    //@Override
    public void cookieAdded(String name, String value) {
      for (CookieModelListener l : this) {
        l.cookieAdded(name, value);
      }
    }

    //@Override
    public void cookieChanged(String name, String value) {
      for (CookieModelListener l : this) {
        l.cookieChanged(name, value);
      }
    }

    //@Override
    public void cookieRemoved(String name) {
      for (CookieModelListener l : this) {
        l.cookieRemoved(name);
      }
    }
  }
}
