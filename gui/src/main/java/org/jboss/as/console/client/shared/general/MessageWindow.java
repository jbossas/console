/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.general;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * A general-purpose text display panel with OK and Cancel buttons. A callback is made
 * to inform which button was pressed to dismiss the window.
 *
 * @author David Bosschaert
 */
public class MessageWindow {
    private final String text;
    private final Result callback;

    public MessageWindow(String text, Result callback) {
        this.text = text;
        this.callback = callback;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final HTML label = new HTML(text);
        layout.add(label);

        DialogueOptions options = new DialogueOptions(
            "OK", new MyClickHandler(true),
            "Cancel", new MyClickHandler(false));

        return new WindowContentBuilder(layout, options).build();
    }

    private class MyClickHandler implements ClickHandler {
        private final boolean value;

        private MyClickHandler(boolean val) {
            value = val;
        }

        @Override
        public void onClick(ClickEvent event) {
            callback.result(value);
        }
    }

    public static interface Result {
        /**
         * Called with the result of the button pressed.
         * @param value <tt>true</tt> when OK was pressed, <tt>false</tt> otherwise.
         */
        void result(boolean result);
    }
}
