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

import com.google.gwt.debugpanel.common.ExceptionData;
import com.google.gwt.debugpanel.common.Utils;
import com.google.gwt.debugpanel.models.ExceptionModel;
import com.google.gwt.debugpanel.models.ExceptionModelListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows the exceptions in a table.
 */
public class ExceptionView extends Composite {
  private ExceptionModel model;
  private FlexTable table;
  
  public ExceptionView(ExceptionModel model) {
    this.model = model;
    initWidget(table = new FlexTable());
    build();

    model.addListener(new ExceptionModelListener() {
      //@Override
      public void exceptionAdded(int idx, ExceptionModel.ExceptionEvent ev) {
        add(idx, ev);
      }

      //@Override
      public void exceptionRemoved(int idx) {
        remove(idx);
      }
    });
    setStyleName(Utils.style() + "-errors");
  }

  private void build() {
    table.setText(0, 0, "Module");
    table.setText(0, 1, "Time");
    table.setText(0, 2, "Exception");
    table.getRowFormatter().setStyleName(0, Utils.style() + "-errorsHeader");

    for (int i = 0; i < model.getExceptionEventCount(); i++) {
      add(i, model.getExceptionEvent(i));
    }
  }

  protected void add(int idx, ExceptionModel.ExceptionEvent evt) {
    idx -= table.getRowCount() - 2;
    table.insertRow(idx);
    table.setText(idx, 0, Utils.formatClassName(evt.module));
    table.setText(idx, 1, Utils.formatDate(evt.time));
    table.setWidget(idx, 2, getExceptionWidget(evt.exception));
  }

  private Widget getExceptionWidget(ExceptionData ex) {
    Widget result = new HTML(String.valueOf(ex));
    result.setStyleName(Utils.style() + "-codePre");
    return result;
  }

  protected void remove(int idx) {
    idx = table.getRowCount() - 1 - idx;
    table.removeRow(idx);
  }
}
