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
package org.jboss.as.console.client.shared.subsys.logging;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.forms.Form;

/**
 * Add dialog for a logging entity (logger or handler).
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class AddLoggingEntityWindow<T> extends DefaultWindow {
    
    private LoggingCmdAdapter<T> commandAdapter;
    private Form<T> form;
    
    public AddLoggingEntityWindow(String title, Form<T> form, LoggingCmdAdapter<T> commandAdapter) {
        super(title);
        this.form = form;
        this.commandAdapter = commandAdapter;
        setWidth(320);
        setHeight(260);

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                AddLoggingEntityWindow.this.hide();
            }
        });
        
        setWidget(makeWidget());
        setGlassEnabled(true);
        center();
        hide();
    }
    
    private Widget makeWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "width:95%, margin:15px;");
        
        layout.add(form.asWidget());

        Label cancel = new Label(Console.CONSTANTS.common_label_cancel());
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AddLoggingEntityWindow.this.hide();
            }
        });

        DefaultButton submit = new DefaultButton(Console.CONSTANTS.common_label_save(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                //FormValidation validation = form.validate();
                //if (!validation.hasErrors()) {
                    // proceed
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                        @Override
                        public void execute() {
                            //commandAdapter.onAdd(form);
                            AddLoggingEntityWindow.this.hide();
                        }
                    });

                }
         //   } // end if
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "margin-top:10px;width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);

        options.add(submit);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style", "vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");

        layout.add(options);
        
        return layout;
    }
}
