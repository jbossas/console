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

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Widget;

/**
 * Filters nodes in the debug panel's table.
 */
public interface DebugPanelFilter {
  public String getMenuItemLabel();
  public String getSettingsTitle();
  public String getDescription();
  public Config getConfig();
  public boolean include(DebugStatisticsValue value);
  public boolean processChildren();

  /**
   * A {@link DebugPanelFilter filter's} configuration.
   */
  public abstract static class Config implements HasValueChangeHandlers<Config>, HasHandlers {
    private HandlerManager handlers;

    public Config() {
      this.handlers = new HandlerManager(this);
    }

    //@Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Config> handler) {
      return handlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    //@Override
    public void fireEvent(GwtEvent<?> event) {
      handlers.fireEvent(event);
    }

    public abstract View getView();

    /**
     * The view of the {@link DebugPanelFilter.Config filter configuration}.
     */
    public static interface View {
      public Widget getWidget();

      /**
       * Called when the user wishes to apply the settings in the view to the filter.
       * 
       * @return @{code false} if the user entered values effectively clear/disable the filter.
       */
      public boolean onApply();

      /**
       * Called when the users wishes to disable the current filter.
       */
      public void onRemove();
    }
  }
}
