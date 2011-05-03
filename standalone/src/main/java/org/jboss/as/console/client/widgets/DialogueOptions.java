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

package org.jboss.as.console.client.widgets;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import org.jboss.as.console.client.Console;

/**
 * @author Heiko Braun
 * @date 4/15/11
 */
public class DialogueOptions extends HorizontalPanel {


    public DialogueOptions(ClickHandler submitHandler, ClickHandler cancelHandler) {
        this(Console.CONSTANTS.common_label_save(), submitHandler, Console.CONSTANTS.common_label_cancel(), cancelHandler);
    }

    public DialogueOptions(
            String submitText, ClickHandler submitHandler,
            String cancelText, ClickHandler cancelHandler) {


        getElement().setAttribute("style", "margin-top:10px;width:100%");

        DefaultButton submit = new DefaultButton(submitText);
        submit.getElement().setAttribute("style", "min-width:60px;height:18px");
        submit.addClickHandler(submitHandler);


        Label cancel = new Label(cancelText);
        cancel.setStyleName("html-link");
        cancel.addClickHandler(cancelHandler);

        getElement().setAttribute("style", "margin-top:15px; width:100%");

        HTML spacer = new HTML("&nbsp;");
        add(spacer);


        add(submit);
        add(spacer);
        add(cancel);
        cancel.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");
    }
}
