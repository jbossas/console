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
import com.google.gwt.debugpanel.models.CookieModel;
import com.google.gwt.debugpanel.models.CookieModelListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the cookies in a table.
 */
public class CookieView extends Composite {
  private CookieModel model;
  private List<String> cookies;
  private FlexTable table;

  public CookieView(CookieModel model) {
    this.model = model;
    this.cookies = new ArrayList<String>();
    initWidget(table = new FlexTable());
    build();
    model.addCookieListener(new CookieModelListener() {
      //@Override
      public void cookieAdded(String name, String value) {
        added(name, value);
      }

      //@Override
      public void cookieChanged(String name, String value) {
        updated(name, value);
      }

      //@Override
      public void cookieRemoved(String name) {
        removed(name);
      }
    });
    setStyleName(Utils.style() + "-cookies");
  }

  protected void added(final String name, String value) {
    int idx = 0;
    for (; idx < cookies.size(); idx++) {
      int d = name.compareTo(cookies.get(idx));
      if (d == 0) {
        table.setText(idx + 1, 1, value);
        return;
      } else if (d < 0) {
        break;
      }
    }
    table.insertRow(idx + 1);
    table.setText(idx + 1, 0, name);
    table.setText(idx + 1, 1, value);
    table.setWidget(idx + 1, 2, new CommandLink("Remove", new Command() {
      //@Override
      public void execute() {
        removeCookie(name);
      }
    }, Utils.style() + "-link"));
    cookies.add(idx, name);
  }

  protected void updated(String name, String value) {
    for (int i = 0; i < cookies.size(); i++) {
      if (name.compareTo(cookies.get(i)) == 0) {
        table.setText(i + 1, 1, value);
        return;
      }
    }
    added(name, value);
  }

  protected void removed(String name) {
    for (int i = 0; i < cookies.size(); i++) {
      if (name.compareTo(cookies.get(i)) == 0) {
        table.removeRow(i + 1);
        cookies.remove(i);
        break;
      }
    }
  }

  protected void addCookie(String name, String value) {
    model.setCookie(name, value, null, null, null, false);
  }

  protected void removeCookie(String name) {
    model.removeCookie(name);
  }

  private void build() {
    // Add header row.
    table.setText(0, 0, "Cookie");
    table.setText(0, 1, "Value");
    table.setText(0, 2, "-");
    table.getRowFormatter().setStyleName(0, Utils.style() + "-cookiesHeader");

    // Add footer row.
    table.setWidget(1, 0, new CommandLink("Add a Cookie", new Command() {
      //@Override
      public void execute() {
        showAdd();
      }
    }, Utils.style() + "-link"));
    ((FlexTable.FlexCellFormatter) table.getCellFormatter()).setColSpan(1, 0, 3);

    for (String cookie : model.cookieNames()) {
      added(cookie, model.getCookie(cookie));
    }
  }

  protected void showAdd() {
    int row = table.getRowCount() - 1;
    table.removeRow(row);

    final TextBox name = new TextBox();
    final TextBox value = new TextBox();
    final ButtonBase button = Utils.createTextButton("Add", new ClickHandler() {
      //@Override
      public void onClick(ClickEvent event) {
        String n = name.getText();
        String v = value.getText();
        name.setText("");
        value.setText("");
        addCookie(n, v);
      }
    });

    table.setWidget(row, 0, name);
    table.setWidget(row, 1, value);
    table.setWidget(row, 2, button);
  }
}
