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

import java.util.ArrayList;

/**
 * The GWT implementation of the {@link DebugPanelFilterModel}.
 */
public class GwtDebugPanelFilterModel implements DebugPanelFilterModel {
  private Filter[] filters;
  private Listeners listeners;

  public GwtDebugPanelFilterModel(DebugPanelFilter[] filters) {
    this.listeners = new Listeners();
    this.filters = new Filter[filters.length];
    for (int i = 0; i < filters.length; i++) {
      this.filters[i] = new Filter(filters[i]);
    }
  }

  //@Override
  public int getCountOfAvailableFilters() {
    return filters.length;
  }

  //@Override
  public DebugPanelFilter getFilter(int idx) {
    return filters[idx].filter;
  }

  //@Override
  public boolean isFilterActive(int idx) {
    return filters[idx].active;
  }

  //@Override
  public void setFilterActive(int idx, boolean active) {
    Filter filter = filters[idx];
    if (active != filter.active) {
      filter.active = active;
      listeners.filterStatusChanged(filter.filter, idx, active);
    }
  }

  //@Override
  public DebugPanelFilter.Config getFilterConfig(int idx) {
    return filters[idx].filter.getConfig();
  }

  //@Override
  public void addListener(DebugPanelFilterModelListener listener) {
    listeners.add(listener);
  }

  //@Override
  public void removeListener(DebugPanelFilterModelListener listener) {
    listeners.remove(listener);
  }

  private static class Filter {
    public final DebugPanelFilter filter;
    public boolean active;

    public Filter(DebugPanelFilter filter) {
      this.filter = filter;
      this.active = false;
    }
  }

  private static class Listeners
      extends ArrayList<DebugPanelFilterModelListener> implements DebugPanelFilterModelListener {
    public Listeners() {
    }

    //@Override
    public void filterStatusChanged(DebugPanelFilter filter, int idx, boolean active) {
      for (DebugPanelFilterModelListener l : this) {
        l.filterStatusChanged(filter, idx, active);
      }
    }
  }
}
