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
package com.google.gwt.debugpanel.widgets;

import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.StatisticsEventListener;
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.user.client.ui.FlexTable;

import java.util.Iterator;

/**
 * Displays the {@link StatisticsEvent statistics events} in raw format in a
 * list. This is useful to debug the debug panel and also to get to the raw
 * data provided by the event originators.
 */
public class DebugPanelRawLogWidget extends FlexTable implements StatisticsEventListener {
  public DebugPanelRawLogWidget() {
    setText(0, 0, "Module");
    setText(0, 1, "Sub System");
    setText(0, 2, "Group Key");
    setText(0, 3, "Millis");
    setText(0, 4, "Extra Parameters");

    setStyleName(Utils.style() + "-log");
    getRowFormatter().setStyleName(0, Utils.style() + "-logHeader");
  }

  //@Override
  public void onStatisticsEvent(StatisticsEvent event) {
    int row = getRowCount();
    setText(row, 0, event.getModuleName());
    setText(row, 1, event.getSubSystem());
    setText(row, 2, event.getEventGroupKey());
    setText(row, 3, toString(event.getMillis()));
    setText(row, 4, getExtraParameters(event));
  }

  private static native String toString(double millis) /*-{
    return "" + millis;
  }-*/;

  private String getExtraParameters(StatisticsEvent event) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    Iterator<String> names = event.getExtraParameterNames();
    if (names.hasNext()) {
      String n = names.next();
      sb.append(n).append(" = ").append(event.getExtraParameter(n));
      while (names.hasNext()) {
        sb.append(", ").append(n = names.next()).append(" = ").append(event.getExtraParameter(n));
      }
    }
    sb.append("}");
    return sb.toString();
  }
}
