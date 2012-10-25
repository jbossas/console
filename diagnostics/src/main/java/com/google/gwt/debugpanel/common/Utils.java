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
package com.google.gwt.debugpanel.common;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;

/**
 * Utilities.
 */
public class Utils {
  private static Util instance = GWT.isClient() ? (Util) GWT.create(Util.class) : null;

  private Utils() {
  }

  public static void setInstance(Util override) {
    instance = override;
  }

  public static String style() {
    return instance.getStylePrefix();
  }

  public static double currentTimeMillis() {
    return instance.currentTimeMillis();
  }

  public static String formatDate(double time) {
    return instance.formatDate(time);
  }

  public static String formatClassName(String className) {
    return instance.formatClassName(className);
  }

  public static ButtonBase createTextButton(String text, ClickHandler handler) {
    return instance.createTextButton(text, handler);
  }

  public static ButtonBase createMenuButton(String text, ClickHandler handler) {
    return instance.createMenuButton(text, handler);
  }

  /**
   * Utilities implementation.
   */
  public static interface Util {
    public String getStylePrefix();
    public double currentTimeMillis();
    public String formatDate(double time);
    public String formatClassName(String className);
    public ButtonBase createTextButton(String text, ClickHandler handler);
    public ButtonBase createMenuButton(String text, ClickHandler handler);
  }

  /**
   * Default implementation of {@link Util}.
   */
  public static class DefaultUtil implements Util {
    //@Override
    public String getStylePrefix() {
      return "DebugPanel";
    }

    //@Override
    public double currentTimeMillis() {
      return Duration.currentTimeMillis();
    }

    //@Override
    public native String formatDate(double time) /*-{
      var d=new Date(time),h=d.getHours(),m=d.getMinutes(),s=d.getSeconds(),l=d.getMilliseconds();
      return (h<10?"0"+h:h)+":"+(m<10?"0"+m:m)+":"+(s<10?"0"+s:s)+"."+(l<10?"00"+l:l<100?"0"+l:l);
    }-*/;

    //@Override
    public String formatClassName(String className) {
      return className;
    }

    //@Override
    public ButtonBase createTextButton(String text, ClickHandler handler) {
      return handler == null ? new Button(text) : new Button(text, handler);
    }

    //@Override
    public ButtonBase createMenuButton(String text, ClickHandler handler) {
      return handler == null ? new Button(text) : new Button(text, handler);
    }
  }
}
