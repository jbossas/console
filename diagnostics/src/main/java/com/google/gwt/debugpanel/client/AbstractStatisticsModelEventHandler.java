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
package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.common.StatisticsEvent;
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel.GwtNode;

/**
 * Base class for the {@link GwtDebugStatisticsModel.EventHandler}.
 */
public abstract class AbstractStatisticsModelEventHandler
    implements GwtDebugStatisticsModel.EventHandler {

  protected static final String BEGIN = "begin";
  protected static final String END = "end";

  public GwtNode findOrCreateChild(
      GwtDebugStatisticsModel model, GwtNode parent, String label, double start, double end) {
    GwtNode result = parent.findChild(label);
    if (result == null) {
      if (start == 0) {
        start = end;
      } else if (end == 0) {
        end = start;
      }
      result = new GwtNode(label, parent.getValue().getModuleName(), start, end);
      int idx = 0;
      for (; idx < parent.getChildCount(); idx++) {
        DebugStatisticsValue value = parent.getChild(idx).getValue();
        if (start <= value.getStartTime() && end <= value.getEndTime()) {
          break;
        }
      }
      model.addNodeAndUpdateItsParents(parent, result, idx);
    }
    return result;
  }

  public String getType(StatisticsEvent event) {
    Object type = event.getExtraParameter("type");
    return String.valueOf(type);
  }

  public String getClassName(StatisticsEvent event) {
    Object name = event.getExtraParameter("className");
    return Utils.formatClassName(String.valueOf(name));
  }
}
