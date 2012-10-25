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

import com.google.gwt.debugpanel.common.StatisticsEvent;

/**
 * GWT implementation of the {@link DebugStatisticsModel}.
 */
public class GwtDebugStatisticsModel extends DebugStatisticsModel<GwtDebugStatisticsValue> {
  private EventHandler[] handlers;

  public GwtDebugStatisticsModel(EventHandler... handlers) {
    this.handlers = handlers;
  }

  //@Override
  public void onStatisticsEvent(StatisticsEvent event) {
    GwtNode node = null;
    for (EventHandler h : handlers) {
      if (h.handle(this, event)) {
        break;
      }
    }
  }

  public void updateNodeAndItsParents(GwtNode node, GwtDebugStatisticsValue value) {
    updateNode(node, value);
    update(node, value.getStartTime(), value.getEndTime());
  }

  public void addNodeAndUpdateItsParents(GwtNode parent, GwtNode child, int idx) {
    addNode(parent, child, idx);
    update(child, child.getValue().getStartTime(), child.getValue().getEndTime());
  }

  private void update(Node<GwtDebugStatisticsValue> node, double start, double end) {
    while ((node = node.getParent()) != null && node != getRoot()) {
      updateNode(node, node.getValue().withChildTimes(start, end));
    }
  }

  /**
   * Handles the {@link StatisticsEvent events} and builds/updates the nodes in
   * the tree of the given model.
   */
  public static interface EventHandler {

    /**
     * @return {@code true} if the event was handled and the tree updated.
     */
    public boolean handle(GwtDebugStatisticsModel model, StatisticsEvent event);
  }

  /**
   * A node that has a {@link GwtDebugStatisticsValue} as its value.
   */
  public static class GwtNode extends Node<GwtDebugStatisticsValue> {
    public GwtNode(String label, String module, double start, double end) {
      super(new GwtDebugStatisticsValue(label, module, start, end));
    }

    public GwtNode findChild(String label) {
      for (int i = 0; i < getChildCount(); i++) {
        GwtNode child = (GwtNode) getChild(i);
        if (label.equals(child.getValue().getLabel())) {
          return child;
        }
      }
      return null;
    }

    public GwtDebugStatisticsValue withTimes(double start, double end) {
      return getValue().withTimes(start, end);
    }

    public GwtDebugStatisticsValue withChildTime(double millis) {
      return getValue().withChildTime(millis);
    }

    public GwtDebugStatisticsValue withChildTimes(double start, double end) {
      return getValue().withChildTimes(start, end);
    }

    public GwtDebugStatisticsValue withEndTime(double millis) {
      return getValue().withEndTime(millis);
    }

    public GwtDebugStatisticsValue withStartTime(double millis) {
      return getValue().withStartTime(millis);
    }
  }
}
