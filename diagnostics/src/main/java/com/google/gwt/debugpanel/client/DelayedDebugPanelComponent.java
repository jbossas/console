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

import com.google.gwt.debugpanel.widgets.DebugPanelWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link DebugPanelWidget.Component} that has the widgets initialization
 * delayed until first shown.
 */
public abstract class DelayedDebugPanelComponent 
    extends SimplePanel implements DebugPanelWidget.Component {
  private boolean initialized = false;

  public DelayedDebugPanelComponent() {
    initialized = false;
  }

  @Override
  public Widget getWidget() {
    return this;
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    if (!initialized && visible) {
      initialized = true;
      setWidget(createWidget());
    }
  }

  protected abstract Widget createWidget();

  //@Override
  public final boolean isVisibleOnStartup() {
    return false;
  }

  public void reset() {
    initialized = false;
    setWidget(null);
  }
}
