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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;

/**
 * Displays form and buttons that allow editing of most attributes of the entity (Handler or LoggerConfig).
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggingDetails<T> implements EditListener {

    private String entitiesName;
    private FormAdapter<T> form;
    private ToolButton editBtn;
    private ToolButton cancelBtn;
    private ToolButton removeBtn;
    private ToolButton assignHandlerBtn;
    private ToolButton unassignHandlerBtn;
    private EntityBridge executor;
    private AssignHandlerWindow assignHandlerWindow;
    private UnassignHandlerWindow unassignHandlerWindow;
    private boolean isAssignHandlerAllowed; // Can we assign a handler to the currently selected bean?
    private boolean isUnassignHandlerAllowed;

    public LoggingDetails(String entitiesName, FormAdapter form, EntityBridge executor, 
                          AssignHandlerWindow assignHandlerWindow, UnassignHandlerWindow unassignHandlerWindow) {
        this.entitiesName = entitiesName;
        this.form = form;
        this.executor = executor;
        this.assignHandlerWindow = assignHandlerWindow;
        this.unassignHandlerWindow = unassignHandlerWindow;
    }

    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");

        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (editBtn.getText().equals(Console.CONSTANTS.common_label_edit())) {
                    executor.onEdit();
                } else {
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) {
                        executor.onSaveDetails(form);
                    }
                }
            }
        };
        editBtn.addClickHandler(editHandler);

        cancelBtn = new ToolButton(Console.CONSTANTS.common_label_cancel());
        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.cancel();
                LoggingDetails.this.setEditingEnabled(false);
            }
        };
        cancelBtn.addClickHandler(cancelHandler);

        removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove());
        ClickHandler removeHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm(Console.CONSTANTS.common_label_areYouSure(),
                        Console.MESSAGES.removeFromConfirm(executor.getName(form.getEditedEntity()), entitiesName),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    executor.onRemove(form);
                                }
                            }
                        });
            }
        };
        removeBtn.addClickHandler(removeHandler);

        assignHandlerBtn = new ToolButton(Console.CONSTANTS.subsys_logging_addHandler());
        ClickHandler addHandlerHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                assignHandlerWindow.setBean(form.getEditedEntity());
                assignHandlerWindow.show();
            }
        };
        assignHandlerBtn.addClickHandler(addHandlerHandler);
        
        unassignHandlerBtn = new ToolButton(Console.CONSTANTS.subsys_logging_removeHandler());
        ClickHandler removeHandlerHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                unassignHandlerWindow.setBean(form.getEditedEntity());
                unassignHandlerWindow.show();
            }
        };
        unassignHandlerBtn.addClickHandler(removeHandlerHandler);
        
        detailToolStrip.addToolButton(editBtn);
        detailToolStrip.addToolButton(cancelBtn);
        detailToolStrip.addToolButton(removeBtn);
        detailToolStrip.addToolButton(assignHandlerBtn);
        detailToolStrip.addToolButton(unassignHandlerBtn);

        detailPanel.add(detailToolStrip);

        detailPanel.add(form.asWidget());
        form.addEditListener(this);

        setEditingEnabled(false);  // initially don't allow edit

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    /**
     * Bind the table to the details section.
     * @param loggingEntityTable The table to be bound.
     */
    public void bind(CellTable<T> loggingEntityTable) {
        form.bind(loggingEntityTable);
    }

    /**
     * Set the state of the form and buttons if editing details is enabled.
     * @param isEnabled Set the flag for editing details.
     */
    public void setEditingEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        cancelBtn.setVisible(isEnabled);
        removeBtn.setVisible(!isEnabled);
        assignHandlerBtn.setVisible(isAssignHandlerAllowed && !isEnabled);
        unassignHandlerBtn.setVisible(isUnassignHandlerAllowed && !isEnabled);
        
        if (isEnabled) {
            editBtn.setText(Console.CONSTANTS.common_label_save());
        } else {
            editBtn.setText(Console.CONSTANTS.common_label_edit());
        }
    }

    @Override
    public void editingBean(Object bean) {
        this.isAssignHandlerAllowed = executor.isAssignHandlerAllowed(bean);
        List<String> assignedHandlers = executor.getAssignedHandlers(bean);
        this.isUnassignHandlerAllowed = this.isAssignHandlerAllowed && (assignedHandlers != null) && (assignedHandlers.size() > 0);
        assignHandlerBtn.setVisible(isAssignHandlerAllowed);
        unassignHandlerBtn.setVisible(isUnassignHandlerAllowed);
    }
}
