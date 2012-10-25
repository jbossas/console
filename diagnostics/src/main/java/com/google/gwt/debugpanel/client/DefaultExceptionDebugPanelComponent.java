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
package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.models.ExceptionModel;
import com.google.gwt.debugpanel.widgets.DebugPanelWidget;
import com.google.gwt.debugpanel.widgets.ExceptionView;
import com.google.gwt.user.client.ui.Widget;

/**
 * {@link DebugPanelWidget} component that shows the exception log.
 */
public class DefaultExceptionDebugPanelComponent implements DebugPanelWidget.Component {
  private ExceptionModel model;

  public DefaultExceptionDebugPanelComponent(ExceptionModel model) {
    this.model = model;
  }

  //@Override
  public String getName() {
    return "Exceptions";
  }

  //@Override
  public Widget getWidget() {
    return new ExceptionView(model);
  }

  //@Override
  public boolean isVisibleOnStartup() {
    return false;
  }
}
