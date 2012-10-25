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

/**
 * The model for a tree table.
 */
public interface TreeTableModel {

  /**
   * Returns the root of the tree.
   */
  public Object getRoot();

  /**
   * Returns the number of children of the given node. Returns 0 if the the parent is a leaf.
   */
  public int getChildCount(Object parent);

  /**
   * Returns the child at the given index of the given node.
   */
  public Object getChild(Object parent, int index);

  /**
   * Returns the number of columns in the table.
   */
  public int getColumnCount();

  /**
   * Returns the name of the column at the given index.
   */
  public String getColumnName(int columnIndex);

  /**
   * Returns the value of the cell at the given column and the given tree node (row).
   */
  public Object getValueAt(Object node, int columnIndex);

  /**
   * Registers the given listeners for change events on the {@link TreeTableModel}.
   */
  public void addTreeTableModelListener(TreeTableModelListener listener);

  /**
   * Deregisters the given listeners for change events on the {@link TreeTableModel}.
   */
  public void removeTreeTableModelListener(TreeTableModelListener listener);
}
