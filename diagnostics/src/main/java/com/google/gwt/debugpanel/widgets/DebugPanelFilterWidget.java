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
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget allowing the interactions with the {@link DebugPanelFilterModel}.
 * Shows a button which will popup a menu of the available filters to add/edit
 * the filter and a trails showing the currently active filters.
 */
public class DebugPanelFilterWidget extends Composite {
  protected ButtonBase button;
  protected FilterPopup popup;

  public DebugPanelFilterWidget(DebugPanelFilterModel model) {
    HorizontalPanel panel = new HorizontalPanel();
    panel.add(button = Utils.createMenuButton("Add/Edit Filter", null));
    panel.add(new DebugPanelFilterTrail(model));
    panel.setStyleName(Utils.style() + "-filters");
    initWidget(panel);

    popup = new FilterPopup(model);
    button.addClickHandler(new ClickHandler() {
      //@Override
      public void onClick(ClickEvent event) {
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
          //@Override
          public void setPosition(int offsetWidth, int offsetHeight) {
            popup.setPopupPosition(
                button.getAbsoluteLeft(), button.getAbsoluteTop() + button.getOffsetHeight())
            ;

          }
        });
      }
    });
  }

  /**
   * A popup that shows a menu with the available filters and the selected filters' config.
   */
  protected static class FilterPopup extends PopupPanel {
    private FilterMenu menu;
    private SimplePanel configContainer;

    public FilterPopup(final DebugPanelFilterModel model) {
      super(true, true);
      HorizontalPanel panel = new HorizontalPanel();
      panel.add(menu = new FilterMenu(model));
      panel.add(configContainer = new SimplePanel());

      add(panel);
      setStyleName(Utils.style() + "-filterPopup");

      addCloseHandler(new CloseHandler<PopupPanel>() {
        //@Override
        public void onClose(CloseEvent<PopupPanel> event) {
          menu.clearSelection();
        }
      });
      menu.addSelectionHandler(new SelectionHandler<Integer>() {
        //@Override
        public void onSelection(SelectionEvent<Integer> event) {
          show(model, event.getSelectedItem());
        }
      });
    }

    protected void show(DebugPanelFilterModel model, int idx) {
      configContainer.clear();
      if (idx >= 0) {
        configContainer.add(new DebugPanelFilterConfigWidget(model, idx));
      }
    }

    //@VisibleForTesting
    protected boolean isItemActive(int idx) {
      return menu.getWidget(idx).getStyleName().contains("active");
    }
  }

  /**
   * The Menu to be displayed in the popup.
   */
  private static class FilterMenu extends VerticalPanel implements HasSelectionHandlers<Integer> {
    private Item selected;

    public FilterMenu(final DebugPanelFilterModel model) {
      for (int i = 0; i < model.getCountOfAvailableFilters(); i++) {
        newItem(i, model.getFilter(i), model.isFilterActive(i));
      }
      model.addListener(new DebugPanelFilterModelListener() {
        //@Override
        public void filterStatusChanged(DebugPanelFilter filter, int idx, boolean active) {
          ((Item) getWidget(idx)).setActive(active);
        }
      });
    }

    private void newItem(final int idx, DebugPanelFilter filter, boolean active) {
      final Item item = new Item(filter, active);
      insert(item, idx);
      item.sinkEvents(Event.ONCLICK);
      item.addClickHandler(new ClickHandler() {
        //@Override
        public void onClick(ClickEvent event) {
          selectItem(item, idx);
        }
      });
    }

    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
      return addHandler(handler, SelectionEvent.getType());
    }

    public void clearSelection() {
      selectItem(null, -1);
    }

    protected void selectItem(Item item, int idx) {
      if (item != selected) {
        if (selected != null) {
          selected.setSelected(false);
        }
        selected = item;
        if (selected != null) {
          selected.setSelected(true);
        }
        SelectionEvent.fire(this, idx);
      }
    }

    /**
     * An item inside the menu.
     */
    private static class Item extends Label {
      public Item(DebugPanelFilter filter, boolean active) {
        super(filter.getMenuItemLabel());
        setStyleName(Utils.style() + "-filterMenuItem");
        if (active) {
          addStyleName("active");
        }
      }

      public void setActive(boolean active) {
        if (active) {
          addStyleName("active");
        } else {
          removeStyleName("active");
        }
      }

      public void setSelected(boolean selected) {
        if (selected) {
          addStyleName("selected");
        } else {
          removeStyleName("selected");
        }
      }
    }
  }
}
