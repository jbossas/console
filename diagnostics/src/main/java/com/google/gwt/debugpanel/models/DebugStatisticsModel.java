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

import com.google.gwt.debugpanel.common.StatisticsEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A model of a tree of {@link DebugStatisticsValue statistics values}.
 */
public abstract class DebugStatisticsModel<T extends DebugStatisticsValue>
    implements StatisticsEventListener {
  private ModelListeners<T> listeners;
  private Node<T> root;

  public DebugStatisticsModel() {
    this.listeners = new ModelListeners<T>();
    this.root = new Node<T>(null);
  }

  public Node<T> getRoot() {
    return root;
  }

  public void addNode(Node<T> parent, Node<T> child, int idx) {
    if (parent == null || parent == root) {
      idx = idx < 0 ? 0 : idx;
      root.addChild(child, idx);
      parent = root;
    } else {
      idx = idx < 0 ? parent.getChildCount() : idx;
      parent.addChild(child, idx);
    }
    child.setParent(parent);
    listeners.nodeAdded(parent, child, idx);
  }

  public void updateNode(Node<T> node, T value) {
    if (value != node.getValue()) {
      node.setValue(value);
      listeners.nodeChanged(node, value);
    }
  }

  public void addDebugStatisticsModelListener(DebugStatisticsModelListener<T> listener) {
    listeners.add(listener);
  }

  public void removeDebugStatisticsModelListener(DebugStatisticsModelListener<T> listener) {
    listeners.remove(listener);
  }

  /**
   * A node in the tree of this model.
   */
  public static class Node<T extends DebugStatisticsValue> {
    private Node<T> parent;
    private T value;
    private List<Node<T>> children;

    public Node(T value) {
      this.value = value;
      this.children = new ArrayList<Node<T>>();
    }

    public T getValue() {
      return value;
    }

    public Node<T> getParent() {
      return parent;
    }

    public int getChildCount() {
      return children.size();
    }

    public Node<T> getChild(int index) {
      return children.get(index);
    }

    protected void addChild(Node<T> node, int idx) {
      children.add(idx, node);
    }

    protected void setValue(T value) {
      this.value = value;
    }

    protected void setParent(Node<T> parent) {
      this.parent = parent;
    }
  }

  private static class ModelListeners<T extends DebugStatisticsValue> 
      extends ArrayList<DebugStatisticsModelListener<T>> 
      implements DebugStatisticsModelListener<T> {

    public ModelListeners() {
    }

    //@Override
    public void nodeAdded(Node<T> parent, Node<T> node, int idx) {
      for (DebugStatisticsModelListener<T> l : this) {
        l.nodeAdded(parent, node, idx);
      }
    }

    //@Override
    public void nodeChanged(Node<T> node, T value) {
      for (DebugStatisticsModelListener<T> l : this) {
        l.nodeChanged(node, value);
      }
    }
  }
}
