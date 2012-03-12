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
package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * Generic popup window that executes a command on the EntityToDmrBridge when the user clicks the Save button.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public abstract class EntityPopupWindow<T> extends DefaultWindow {

    protected EntityToDmrBridge<T> bridge;
    protected FormAdapter<T> form;
    protected Widget helpWidget;

    public EntityPopupWindow(String title, FormAdapter<T> form, Widget helpWidget, EntityToDmrBridge<T> bridge) {
        super(title);
        this.form = form;
        this.bridge = bridge;
        this.helpWidget = helpWidget;
        setWidth(480);
        setHeight(360);

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                EntityPopupWindow.this.hide();
            }
        });

        trapWidget(makeWidget());
        setGlassEnabled(true);
        center();
        hide();
    }

    private Widget makeWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        if (helpWidget != null) {
            layout.add(helpWidget);
        }

        layout.add(form.asWidget());

        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                EntityPopupWindow.this.hide();
            }
        };

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if (!validation.hasErrors()) {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            doCommand(form);
                            EntityPopupWindow.this.hide();
                        }
                    });
                }
            } // end if
        };

        DialogueOptions options = new DialogueOptions(
                submitHandler, cancelHandler
        );

        return new WindowContentBuilder(layout, options).build();

    }

    public void setNewBean() {
        T newBean = bridge.newEntity();
        form.edit(newBean);
    }

    public void setBean(T bean) {
        form.edit(bean);
    }

    /**
     * Execute a command, typically delegating to the EntityToDmrBridge.
     * @param form The form that was just edited.
     */
    protected abstract void doCommand(FormAdapter<T> form);
}
