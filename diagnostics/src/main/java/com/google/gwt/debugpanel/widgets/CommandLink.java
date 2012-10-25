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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;

/**
 * A link associated with a {@link Command}.
 */
public class CommandLink extends Anchor {
  private Command command;

  public CommandLink(String text) {
    this(text, null, null);
  }

  public CommandLink(String text, Command command) {
    this(text, command, null);
  }

  public CommandLink(String text, Command command, String styleName) {
    super(text, true, "javascript:void(0)");
    this.command = command;
    if (styleName != null) {
      setStyleName(styleName);
    }

    addClickHandler(new ClickHandler() {

      //@Override
      public void onClick(ClickEvent event) {
        execute();
      }
    });
  }

  public void execute() {
    if (command != null) {
      command.execute();
    }
  }

  public Command getCommand() {
    return command;
  }

  public void setCommand(Command command) {
    this.command = command;
  }
}
