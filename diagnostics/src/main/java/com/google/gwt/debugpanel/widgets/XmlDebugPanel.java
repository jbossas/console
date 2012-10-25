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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Formats a {@link TreeTableModel} as an XML document inside a {@link TextArea}.
 */
public class XmlDebugPanel extends Composite implements TreeTableModelListener{
  private TreeTableModel model;
  private Map<Object, Element> nodes;
  private Document document;
  private Element root;

  private TextArea textArea;

  public XmlDebugPanel(TreeTableModel model) {
    this.model = model;
    nodes = new IdentityHashMap<Object, Element>();
    document = XMLParser.createDocument();
    root = document.createElement("debug-panel");
    initWidget(textArea = new TextArea());
    textArea.ensureDebugId("debug-panel-xml");
    textArea.setStyleName(Utils.style() + "-xml");

    // Build the XML document based on the current state.
    document.appendChild(root);
    nodes.put(model.getRoot(), root);
    build(model.getRoot());

    model.addTreeTableModelListener(this);
  }

  private void build(Object node) {
    for (int i = 0; i < model.getChildCount(node); i++) {
      Object child = model.getChild(node, i);
      nodeAdded(node, child, i);
      build(child);
    }
  }

  //@Override
  public void nodeAdded(Object parent, Object node, int index) {
    Element parentNode = nodes.get(parent);
    Element childNode = document.createElement("event");
    for (int i = 0; i < model.getColumnCount(); i++) {
      childNode.setAttribute(getAttributeName(i), getAttributeValue(node, i));
    }
    parentNode.appendChild(childNode);
    nodes.put(node, childNode);
    updateTextArea();
  }

  //@Override
  public void nodeRemoved(Object parent, Object node, int index) {
    Element parentNode = parent == null ? root : nodes.get(parent);
    Element childNode = nodes.get(node);
    parentNode.removeChild(childNode);
    nodes.remove(node);
    updateTextArea();
  }

  //@Override
  public void valueChanged(Object node, int columnIndex) {
    Element element = nodes.get(node);
    element.setAttribute(getAttributeName(columnIndex), getAttributeValue(node, columnIndex));
    updateTextArea();
  }

  private void updateTextArea() {
    if (isVisible()) {
      textArea.setText(document.toString());
    }
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    updateTextArea();
  }

  public Document getDocument() {
    return document;
  }

  public TextArea getTextArea() {
    return textArea;
  }

  private String getAttributeName(int column) {
    return model.getColumnName(column).toLowerCase().replaceAll("[^A-Za-z0-9]", "_");
  }

  private String getAttributeValue(Object node, int column) {
    return String.valueOf(model.getValueAt(node, column));
  }
}
