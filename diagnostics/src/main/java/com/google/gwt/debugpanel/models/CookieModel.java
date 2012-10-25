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

import java.util.Date;

/**
 * A model of the cookies of a page.
 */
public interface CookieModel {
  public String[] cookieNames();
  public String getCookie(String name);
  public void setCookie(
      String name, String value, Date expires, String domain, String path, boolean secure);
  public void removeCookie(String name);

  public void addCookieListener(CookieModelListener listener);
  public void removeCookieListener(CookieModelListener listener);
}
