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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shows all the Debug Panel widgets along with links to hide/show them.
 */
public class DebugPanelWidget extends Composite {
  private Component[] components;
  private DebugPanelListener listener;
  private SimplePanel panel;
  private CommandLink showLink;

  public DebugPanelWidget(DebugPanelListener listener, final boolean showReset, Component... components) {
    this.listener = listener;
    this.components = components;

    initWidget(panel = new SimplePanel());
    panel.setStyleName(Utils.style() + "-panel");
    showLink = new CommandLink("Show Debug Panel", new Command() {
      //@Override
      public void execute() {
        show(showReset);
      }
    });
    showLink.setStyleName(Utils.style() + "-link");
    panel.add(showLink);
  }

  public void show(boolean showReset) {
    if (listener != null) {
      listener.onShow();
    }

    VerticalPanel child = new VerticalPanel();
    Widget[] widgets = new Widget[components.length];
    for (int i = 0; i < components.length; i++) {
      child.add(widgets[i] = components[i].getWidget());
      widgets[i].setVisible(components[i].isVisibleOnStartup());
    }
    child.add(new DebugPanelLinks(components, widgets, showReset));
    panel.clear();
    panel.add(child);
  }

  public void reset() {
    panel.clear();
    panel.add(showLink);

    if (listener != null) {
      listener.onReset();
    }
  }

  /**
   * A component to be shown in the debug panel area.
   */
  public static interface Component {

    /**
     * The name of this component to be used in the hide/show links.
     */
    public String getName();

    /**
     * Whether this component should be active when the debug panel is shown.
     */
    public boolean isVisibleOnStartup();

    /**
     * Construct the widgets to be shown when called. Note, this will only be
     * called once, so the widgets creation can be easily delayed until called.
     */
    public Widget getWidget();
  }

  /**
   * The links below the debug panel.
   */
  private class DebugPanelLinks extends Composite {
    public DebugPanelLinks(Component[] components, Widget[] widgets, boolean showReset) {
      FlowPanel links = new FlowPanel();
      for (int i = 0; i < components.length; i++) {
        final Component c = components[i];
        final Widget w = widgets[i];

        String name = c.getName();
        TogglingCommandLink link = new TogglingCommandLink("Hide " + name, new Command() {
          //@Override
          public void execute() {
            w.setVisible(false);
          }
        }, "Show " + name, new Command() {
          //@Override
          public void execute() {
            w.setVisible(true);
          }
        });
        if (!c.isVisibleOnStartup()) {
          link.setState(TogglingCommandLink.State.Secondary);
        }
        link.setStyleName(Utils.style() + "-link");
        links.add(link);
      }

      if (showReset) {
        CommandLink link = new CommandLink("Reset", new Command() {
          //@Override
          public void execute() {
            reset();
          }
        });
        link.setStyleName(Utils.style() + "-link");
        links.add(link);
      }

      initWidget(links);
      setStyleName(Utils.style() + "-links");
    }
  }
}
