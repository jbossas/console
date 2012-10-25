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
import com.google.gwt.debugpanel.models.DebugStatisticsModel;
import com.google.gwt.debugpanel.models.DebugStatisticsModelListener;
import com.google.gwt.debugpanel.models.DebugStatisticsValue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View that shows the statistics from a {@link DebugStatisticsModel}.
 */
public abstract class DebugStatisticsView<T extends DebugStatisticsValue> extends Composite
    implements TreeTableModel, DebugStatisticsModelListener<T> {
  private DebugPanelFilterModel filters;
  private Node<T> rootNode;
  private Map<DebugStatisticsModel.Node<T>, Node<T>> nodes;
  private Listeners listeners;
  private Filter currentFilter;

  public DebugStatisticsView(DebugStatisticsModel.Node<T> root, DebugPanelFilterModel filters) {
    this.filters = filters;
    this.rootNode = new Node<T>(null);
    this.nodes = new HashMap<DebugStatisticsModel.Node<T>, Node<T>>();
    this.listeners = new Listeners();

    build(root, rootNode);

    updateFilter();
    filters.addListener(new DebugPanelFilterModelListener() {
      //@Override
      public void filterStatusChanged(DebugPanelFilter filter, int idx, boolean active) {
        updateFilter();
        refilter(getRoot(), true);
      }
    });
    for (int i = 0; i < filters.getCountOfAvailableFilters(); i++) {
      filters.getFilterConfig(i).addValueChangeHandler(
          new ValueChangeHandler<DebugPanelFilter.Config>() {
        public void onValueChange(ValueChangeEvent<DebugPanelFilter.Config> event) {
          refilter(getRoot(), true);
        }
      });
    }

    VerticalPanel panel = new VerticalPanel();
    //panel.add(new DebugPanelFilterWidget(filters));
    panel.add(new TreeTable(this, null, new TreeTable.CellFormatter() {
          //@Override
          public String getCellStyleName(Object node, int columnIndex) {
            if (columnIndex == 0) {
              return Utils.style() + "-tree";
            } else if (columnIndex == 1) {
              return Utils.style() + "-time";
            } else if (columnIndex >= 5) {
              return getExtraColumnStyleName(columnIndex - 5);
            } else {
              return null;
            }
          }
        }, false));
    initWidget(panel);
  }

  private void build(DebugStatisticsModel.Node<T> dnode, Node<T> node) {
    nodes.put(dnode, node);

    for (int i = 0; i < dnode.getChildCount(); i++) {
      DebugStatisticsModel.Node<T> dchild = dnode.getChild(i);
      Node<T> child = new Node<T>(dchild.getValue());
      node.addChild(child, i);
      build(dchild, child);
    }
  }

  //@Override
  public Object getRoot() {
    return rootNode;
  }

  //@Override
  public int getChildCount(Object parentNode) {
    return ((Node<T>) parentNode).getChildCount(false);
  }

  //@Override
  public Object getChild(Object parentNode, int index) {
    return ((Node<T>) parentNode).getChild(index, false);
  }

  //@Override
  public int getColumnCount() {
    return 5 + getExtraColumnCount();
  }

  protected abstract int getExtraColumnCount();
  protected abstract String getExtraColumnStyleName(int extraColumn);
  protected abstract String getExtraColumnName(int columnIndex);
  protected abstract Object getExtraColumnValue(T value, int columnIndex);

  //@Override
  public String getColumnName(int columnIndex) {
    if (columnIndex >= 0 && columnIndex < 5) {
      switch (columnIndex) {
        case 0: return "Event";
        case 1: return "Time";
        case 2: return "Start";
        case 3: return "End";
        case 4: return "Module";
      }
    } else {
      int extra = getExtraColumnCount();
      if (columnIndex >= 5 && columnIndex < 5 + extra) {
        return getExtraColumnName(columnIndex - extra);
      }
    }
    throw new IllegalArgumentException("Invalid column index: " + columnIndex);
  }

  //@Override
  public Object getValueAt(Object node, int columnIndex) {
    Node<T> n = (Node<T>) node;
    if (columnIndex >= 0 && columnIndex < 5) {
      switch (columnIndex) {
        case 0: return n.getLabel();
        case 1: return n.getDuration();
        case 2: return n.getStart();
        case 3: return n.getEnd();
        case 4: return n.getModule();
      }
    } else {
      int extra = getExtraColumnCount();
      if (columnIndex >= 5 && columnIndex < 5 + extra) {
        return getExtraColumnValue(n.value, columnIndex);
      }
    }
    throw new IllegalArgumentException("Invalid column index: " + columnIndex);
  }

  //@Override
  public void addTreeTableModelListener(TreeTableModelListener listener) {
    listeners.add(listener);
  }

  //@Override
  public void removeTreeTableModelListener(TreeTableModelListener listener) {
    listeners.remove(listener);
  }

  //@Override
  public void nodeAdded(
      DebugStatisticsModel.Node<T> parent, DebugStatisticsModel.Node<T> node, int idx) {
    Node<T> parentNode = nodes.get(parent);
    Node<T> child = new Node<T>(node.getValue());
    parentNode.addChild(child, idx);
    nodes.put(node, child);

    if (parentNode.isFiltered() || child.shouldFilter(currentFilter, parentNode == rootNode)) {
      child.setFiltered(true);
    } else {
      listeners.nodeAdded(parentNode, child, idx);
    }
  }

  //@Override
  public void nodeChanged(DebugStatisticsModel.Node<T> key, T value) {
    Node<T> node = nodes.get(key);
    node.setValue(value);

    refilter(rootNode, true);
    if (!node.isFiltered()) {
      for (int i = 0; i < getColumnCount(); i++) {
        listeners.valueChanged(node, i);
      }
    }
  }

  protected void updateFilter() {
    currentFilter = Filter.NOOP;
    for (int i = 0; i < filters.getCountOfAvailableFilters(); i++) {
      if (filters.isFilterActive(i)) {
        currentFilter = new Filter(currentFilter, filters.getFilter(i));
      }
    }
  }

  protected void refilter(Object parent, boolean root) {
    Node<T> node = (Node<T>) parent;
    for (int i = 0, j = 0; i < node.getChildCount(true); i++) {
      Node<T> child = node.getChild(i, true);
      boolean filtered = child.shouldFilter(currentFilter, root);
      updateFilterStatus(node, child, j, filtered);
      if (!filtered) {
        j++;
        refilter(child, false);
      }
    }
  }

  private void updateFilterStatus(Node<T> parent, Node<T> child, int idx, boolean filtered) {
    if (child.setFiltered(filtered)) {
      if (!filtered) {
        listeners.nodeAdded(parent, child, idx);
      }

      for (int i = 0; i < child.getChildCount(true); i++) {
        updateFilterStatus(child, child.getChild(i, true), filtered ? 0 : i, filtered);
      }

      if (filtered) {
        listeners.nodeRemoved(parent, child, idx);
      }
    }
  }

  private static class Node<T extends DebugStatisticsValue> {
    private T value;
    private List<Node<T>> children;
    private boolean filtered;

    public Node(T value) {
      this.value = value;
      this.children = new ArrayList<Node<T>>();
    }

    public void addChild(Node<T> node, int idx) {
      children.add(idx, node);
    }

    public void setValue(T value) {
      this.value = value;
    }

    public int getChildCount(boolean includeFiltered) {
      if (includeFiltered) {
        return children.size();
      }
      int r = 0;
      if (!filtered) {
        for (Node<T> c : children) {
          if (!c.isFiltered()) {
            r++;
          }
        }
      }
      return r;
    }

    public Node<T> getChild(int index, boolean includeFiltered) {
      if (includeFiltered) {
        return children.get(index);
      }
      for (int i = 0; i < children.size(); i++) {
        if (!children.get(i).isFiltered()) {
          if (index-- <= 0) {
            return children.get(i);
          }
        }
      }
      throw new IndexOutOfBoundsException();
    }

    public String getLabel() {
      return value.getLabel();
    }

    public String getDuration() {
      return (value.getEndTime() - value.getStartTime()) + "ms";
    }

    public String getStart() {
      return Utils.formatDate(value.getStartTime());
    }

    public String getEnd() {
      return Utils.formatDate(value.getEndTime());
    }

    public String getModule() {
      String module = value.getModuleName();
      if (module != null) {
        module = Utils.formatClassName(module);
      }
      return module;
    }

    public boolean isFiltered() {
      return filtered;
    }

    public boolean setFiltered(boolean filtered) {
      if (!this.filtered == filtered) {
        this.filtered = filtered;
        return true;
      }
      return false;
    }

    public boolean shouldFilter(Filter filter, boolean root) {
      return filter.filter(value, root);
    }
  }

  private static class Listeners extends ArrayList<TreeTableModelListener> 
      implements TreeTableModelListener {
    public Listeners() {
    }

    //@Override
    public void nodeAdded(Object parent, Object node, int index) {
      for (TreeTableModelListener l : this) {
        l.nodeAdded(parent, node, index);
      }
    }

    //@Override
    public void nodeRemoved(Object parent, Object node, int index) {
      for (TreeTableModelListener l : this) {
        l.nodeRemoved(parent, node, index);
      }
    }

    //@Override
    public void valueChanged(Object node, int columnIndex) {
      for (TreeTableModelListener l : this) {
        l.valueChanged(node, columnIndex);
      }
    }
  }

  private static class Filter {
    public static final Filter NOOP = new Filter(null, null) {
      @Override
      public boolean filter(DebugStatisticsValue value, boolean root) {
        return false;
      }
    };

    private Filter parent;
    private DebugPanelFilter filter;

    public Filter(Filter parent, DebugPanelFilter filter) {
      this.parent = parent;
      this.filter = filter;
    }

    public boolean filter(DebugStatisticsValue value, boolean root) {
      return ((root || filter.processChildren()) && !filter.include(value)) ||
          (parent != null && parent.filter(value, root));
    }
  }
}
