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

package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;


/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public class JvmEditor {

    private JvmManagement presenter;

    private Form<Jvm> form;

    BeanFactory factory = GWT.create(BeanFactory.class);
    private boolean hasJvm;

    private ToolButton edit;
    private String reference;
    private Widget formWidget;

    public JvmEditor(JvmManagement presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        ToolStrip toolStrip = new ToolStrip();
        edit = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                if(edit.getText().equals(Console.CONSTANTS.common_label_edit()))
                {
                    onEdit();
                }
                else
                {
                    onSave();
                }
            }
        };

        edit.addClickHandler(editHandler);
        toolStrip.addToolButton(edit);

        ToolButton delete = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(hasJvm)
                {
                    Feedback.confirm(Console.MESSAGES.deleteJVM(), Console.MESSAGES.deleteJVMConfirm(),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean isConfirmed) {
                                    if(isConfirmed)
                                        presenter.onDeleteJvm(reference, form.getEditedEntity());
                                }
                            });
                }
            }
        });

        toolStrip.addToolButton(delete);

        panel.add(toolStrip);


        form = new Form<Jvm>(Jvm.class);
        form.setNumColumns(2);

        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name());
        TextBoxItem heapItem = new TextBoxItem("heapSize", "Heap Size");
        TextBoxItem maxHeapItem = new TextBoxItem("maxHeapSize", "Max Heap Size");
        CheckBoxItem debugItem = new CheckBoxItem("debugEnabled", "Debug Enabled?");
        //TextBoxItem debugOptionsItem = new TextBoxItem("debugOptions", "Debug Options");

        form.setFields(nameItem, heapItem, maxHeapItem, debugItem);
        form.setEnabled(false);

        formWidget = form.asWidget();
        panel.add(formWidget);

        return panel;
    }

    private void onSave() {

        FormValidation validation = form.validate();
        if(!validation.hasErrors())
        {
            form.setEnabled(false);
            edit.setText(Console.CONSTANTS.common_label_edit());

            Jvm jvm = form.getUpdatedEntity();
            if(hasJvm)
                presenter.onUpdateJvm(reference, jvm.getName(), form.getChangedValues());
            else
                presenter.onCreateJvm(reference, jvm);
        }
    }

    private void onEdit() {

        edit.setText(Console.CONSTANTS.common_label_save());
        form.setEnabled(true);
    }

    public void setSelectedRecord(String reference, Jvm jvm) {
        this.reference = reference;

        hasJvm = jvm!=null;

        form.setEnabled(false);

        edit.setText(Console.CONSTANTS.common_label_edit());

        if(hasJvm)
            form.edit(jvm);
        else
            form.edit(factory.jvm().as());

    }
}
