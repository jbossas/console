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
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggerConfig;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.ListItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class LoggingDetails<T> {

    private String entitiesName;
    private Form<T> form;
    private ToolButton editBtn;
    private ToolButton cancelBtn;
    private ToolButton removeBtn;
    private T editedEntity;
    private LoggingCmdAdapter executor;

    public LoggingDetails(String entitiesName, Form<T> form, LoggingCmdAdapter executor) {
        this.entitiesName = entitiesName;
        this.form = form;
        this.executor = executor;
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
                    editedEntity = form.getEditedEntity();
                    executor.onSaveDetails(form);
                }
            }
        };
        editBtn.addClickHandler(editHandler);

        cancelBtn = new ToolButton(Console.CONSTANTS.common_label_cancel());
        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.cancel();
                LoggingDetails.this.setEnabled(false);
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

        detailToolStrip.addToolButton(editBtn);
        detailToolStrip.addToolButton(cancelBtn);
        detailToolStrip.addToolButton(removeBtn);

        detailPanel.add(detailToolStrip);

        detailPanel.add(form.asWidget());

        setEnabled(false);  // initially don't allow edit

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    public void bind(CellTable<T> loggerConfigTable) {
        form.bind(loggerConfigTable);
    }

    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        cancelBtn.setVisible(isEnabled);
        removeBtn.setVisible(!isEnabled);

        if (isEnabled) {
            editBtn.setText(Console.CONSTANTS.common_label_save());
        } else {
            editBtn.setText(Console.CONSTANTS.common_label_edit());
        }
    }

    public T getEditedEntity() {
        return editedEntity;
    }
}
