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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * A general-purpose input window panel. The user enters a response in the
 * textbox, and a callback is made with the result.
 *
 * @author David Bosschaert
 */
public class InputWindow {
    private final String initial;
    private final Result callback;

    public InputWindow(String initial, Result callback) {
        this.initial = initial;
        this.callback = callback;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final TextBox textBox = new TextBox();
        textBox.setValue(initial);
        layout.add(textBox);

        DialogueOptions options = new DialogueOptions(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    callback.result(textBox.getValue());
                }
            }, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    callback.result(null);
                }
            });

        return new WindowContentBuilder(layout, options).build();
    }

    public static interface Result {
        /**
         * Called with the result of the Input Window.
         * @param value The new value or <tt>null</tt> if the user pressed cancel.
         */
        void result(String value);
    }
}
