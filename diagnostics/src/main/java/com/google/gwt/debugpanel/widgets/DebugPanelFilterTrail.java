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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Displays a bread crumb like trail of the active filters.
 */
public class DebugPanelFilterTrail extends Composite {
  protected HorizontalPanel panel;
  private int activeCount;

  public DebugPanelFilterTrail(DebugPanelFilterModel model) {
    initWidget(panel = new HorizontalPanel());
    build(model);
    model.addListener(new DebugPanelFilterModelListener() {
      //@Override
      public void filterStatusChanged(DebugPanelFilter filter, int idx, boolean active) {
        changeStatus(idx, active);
      }
    });
  }

  private void build(DebugPanelFilterModel model) {
    Label label = new Label("Currently filtering");
    label.setVisible(false);
    label.setStyleName(Utils.style() + "-filterTrailLabel");
    panel.add(label);
    for (int i = 0; i < model.getCountOfAvailableFilters(); i++) {
      panel.add(new Item(model, i));
      if (model.isFilterActive(i)) {
        activeCount++;
        panel.getWidget(0).setVisible(true);
      }
    }
  }

  //@VisibleForTesting
  public boolean isPopupVisible(int idx) {
    return ((Item) panel.getWidget(idx + 1)).isPopupVisible();
  }

  protected void changeStatus(int idx, boolean active) {
    if (((Item) panel.getWidget(idx + 1)).setActive(active)) {
      activeCount += active ? 1 : -1;
      panel.getWidget(0).setVisible(activeCount != 0);
    }
  }

  /**
   * A Crumb in the trail.
   */
  protected static class Item extends CommandLink {
    private PopupPanel popup;

    public Item(final DebugPanelFilterModel model, final int filter) {
      super(model.getFilter(filter).getMenuItemLabel());
      setActive(model.isFilterActive(filter));
      setStyleName(Utils.style() + "-filterTrail-item");
      setCommand(new Command() {
        //@Override
        public void execute() {
          showPopup(model, filter);
        }
      });
    }

    protected void showPopup(final DebugPanelFilterModel model, final int filter) {
      popup = new FilterPopup(model, filter);
      popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
        //@Override
        public void setPosition(int offsetWidth, int offsetHeight) {
          popup.setPopupPosition(Item.this.getAbsoluteLeft(),
              Item.this.getAbsoluteTop() + Item.this.getOffsetHeight());
        }
      });
    }

    public boolean isPopupVisible() {
      return popup != null && popup.isVisible();
    }

    public boolean setActive(boolean active) {
      if (active != isVisible()) {
        setVisible(active);
        if (!active && isPopupVisible()) {
          popup.hide();
          popup = null;
        }
        return true;
      }
      return false;
    }

    /**
     * The popup to be shown when clicking on the crumb, showing the filter's config.
     */
    protected static class FilterPopup extends PopupPanel {
      public FilterPopup(final DebugPanelFilterModel model, final int filter) {
        super(true, true);
        setStyleName(Utils.style() + "-filterPopup");

        add(new DebugPanelFilterConfigWidget(model, filter));
      }
    }
  }
}
