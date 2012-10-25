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

import com.google.gwt.debugpanel.models.GwtDebugPanelFilterModel;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.widgets.DebugPanelWidget;
import com.google.gwt.debugpanel.widgets.GwtDebugPanelFilters;
import com.google.gwt.debugpanel.widgets.GwtDebugStatisticsView;
import com.google.gwt.debugpanel.widgets.XmlDebugPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * {@link DebugPanelWidget} component that shows the statistics tree.
 */
public class DefaultDebugStatisticsDebugPanelComponent implements DebugPanelWidget.Component {
  private GwtDebugStatisticsModel model;
  private GwtDebugStatisticsView view;

  public DefaultDebugStatisticsDebugPanelComponent(GwtDebugStatisticsModel model) {
    this.model = model;
    this.view = null;
  }

  //@Override
  public String getName() {
    return "Debug Panel";
  }

  //@Override
  public Widget getWidget() {
    return getView();
  }

  protected GwtDebugStatisticsView getView() {
    if (view == null) {
      view = createView();
    }
    return view;
  }

  protected GwtDebugStatisticsView createView() {
    return new GwtDebugStatisticsView(model,
        new GwtDebugPanelFilterModel(GwtDebugPanelFilters.getFilters()));
  }

  //@Override
  public boolean isVisibleOnStartup() {
    return true;
  }

  public void reset(GwtDebugStatisticsModel newModel) {
    this.model = newModel;
    this.view = null;
  }

  public DelayedDebugPanelComponent xmlComponent() {
    return new DelayedDebugPanelComponent() {
      //@Override
      public String getName() {
        return "XML";
      }

      @Override
      protected Widget createWidget() {
        return new XmlDebugPanel(getView());
      }
    };
  }
}
