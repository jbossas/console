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
 * A {@link CommandLink} that toggles between two labels and two {@link Command commands}.
 */
public class TogglingCommandLink extends Anchor {
  private String text1, text2;
  private Command command1, command2;

  private State state;

  public TogglingCommandLink(String text1, Command command1, String text2, Command command2) {
    super(text1, "javascript:void(0)");
    this.text1 = text1;
    this.command1 = command1;
    this.text2 = text2;
    this.command2 = command2;
    this.state = State.Primary;

    addClickHandler(new ClickHandler() {

      //@Override
      public void onClick(ClickEvent event) {
        toggle();
      }
    });
  }

  /**
   * Toggles the link executing the command. All state is changed before the command
   * is executed and it is therefore safe to call this from within the commands.
   */
  public void toggle() {
    Command cmd = state.isPrimary() ? command1 : command2;
    setState(state.other());
    cmd.execute();
  }

  /**
   * Returns the state of the link.
   */
  public State getState() {
    return state;
  }

  /**
   * Changes the state without executing the command.
   */
  public void setState(State state) {
    this.state = state;
    setText(state.isPrimary() ? text1 : text2);
  }

  public static enum State {
    Primary, Secondary;

    public boolean isPrimary() {
      return this == Primary;
    }

    public State other() {
      return isPrimary() ? Secondary : Primary;
    }
  }
}
