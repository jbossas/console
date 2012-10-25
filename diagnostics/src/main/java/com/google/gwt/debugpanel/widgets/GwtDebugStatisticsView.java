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

import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.DebugPanelFilterModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsValue;

/**
 * A view showing the {@link GwtDebugStatisticsValue} tree.
 */
public class GwtDebugStatisticsView extends DebugStatisticsView<GwtDebugStatisticsValue> {
  public GwtDebugStatisticsView(GwtDebugStatisticsModel model, DebugPanelFilterModel filters) {
    super(model.getRoot(), filters);
    model.addDebugStatisticsModelListener(this);
  }

  @Override
  protected int getExtraColumnCount() {
    return 1;
  }

  @Override
  protected String getExtraColumnName(int columnIndex) {
    return "Service / Response";
  }

  @Override
  protected String getExtraColumnStyleName(int extraColumn) {
    return Utils.style() + "-code";
  }

  @Override
  protected Object getExtraColumnValue(GwtDebugStatisticsValue value, int columnIndex) {
    if (value.hasRpcMethod()) {
      return value.getRpcMethod();
    } else if (value.hasResponse()) {
      return String.valueOf(value.getResponse());
    }
    return "";
  }
}
