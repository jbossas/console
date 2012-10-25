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
import com.google.gwt.debugpanel.models.DebugPanelFilter;
import com.google.gwt.debugpanel.models.DebugPanelFilterModel;
import com.google.gwt.debugpanel.models.DebugPanelFilterModelListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that allows the editing of a {@link DebugPanelFilter filter's} 
 * {@link DebugPanelFilter.Config configuration}.
 */
public class DebugPanelFilterConfigWidget extends Composite {
  protected ButtonBase addButton;
  protected ButtonBase applyButton;
  protected ButtonBase removeButton;

  public DebugPanelFilterConfigWidget(DebugPanelFilterModel model, int filter) {
    VerticalPanel panel = new VerticalPanel();
    panel.add(getTitle(model.getFilter(filter)));
    panel.add(getDescription(model.getFilter(filter)));
    DebugPanelFilter.Config config = model.getFilterConfig(filter);
    panel.add(config.getView().getWidget());
    panel.add(getButtons(model, filter, config));
    initWidget(panel);
    setStyleName(Utils.style() + "-filterSettings");
  }

  private Widget getTitle(DebugPanelFilter filter) {
    Label label = new Label(filter.getSettingsTitle());
    label.setStyleName(Utils.style() + "-filterSettingsTitle");
    return label;
  }

  private Widget getDescription(DebugPanelFilter filter) {
    Label label = new Label(filter.getDescription());
    label.setStyleName(Utils.style() + "-filterSettingsDescription");
    return label;
  }

  private Widget getButtons(
      final DebugPanelFilterModel model, final int filter, final DebugPanelFilter.Config config) {
    final HorizontalPanel panel = new HorizontalPanel();
    removeButton = Utils.createTextButton("Remove", new ClickHandler() {
      //@Override
      public void onClick(ClickEvent event) {
        model.setFilterActive(filter, false);
        config.getView().onRemove();
      }
    });
    applyButton = Utils.createTextButton("Apply", new ClickHandler() {
      //@Override
      public void onClick(ClickEvent event) {
        if (!config.getView().onApply()) {
          model.setFilterActive(filter, false);
        }
      }
    });
    addButton = Utils.createTextButton("Add", new ClickHandler() {
      //@Override
      public void onClick(ClickEvent event) {
        if (config.getView().onApply()) {
          model.setFilterActive(filter, true);
        }
      }
    });
    if (model.isFilterActive(filter)) {
      panel.add(removeButton);
      panel.add(applyButton);
    } else {
      panel.add(addButton);
    }
    model.addListener(new DebugPanelFilterModelListener() {
      //@Override
      public void filterStatusChanged(DebugPanelFilter f, int idx, boolean active) {
        if (idx == filter) {
          panel.clear();
          if (active) {
            panel.add(removeButton);
            panel.add(applyButton);
          } else {
            panel.add(addButton);
          }
        }
      }
    });
    panel.setStyleName(Utils.style() + "-filterSettingsButtons");
    return panel;
  }
}
