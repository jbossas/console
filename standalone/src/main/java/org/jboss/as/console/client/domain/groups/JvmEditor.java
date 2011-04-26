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

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Jvm;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public class JvmEditor {

    private ServerGroupPresenter presenter;

    private Form<Jvm> form;
    private ServerGroupRecord selectedRecord;

    BeanFactory factory = GWT.create(BeanFactory.class);
    private boolean hasJvm;

    Label label;

    public JvmEditor(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        ToolStrip toolStrip = new ToolStrip();
        final ToolButton edit = new ToolButton("Edit");
        ClickHandler editHandler = new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                if(edit.getText().equals("Edit"))
                {
                    edit.setText("Save");
                    onEdit();
                }
                else
                {
                    edit.setText("Edit");
                    onSave();
                }
            }
        };

        edit.addClickHandler(editHandler);
        toolStrip.addToolButton(edit);

        ToolButton delete = new ToolButton("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(hasJvm)
                    presenter.onDeleteJvm(selectedRecord.getGroupName(), form.getEditedEntity());
            }
        });

        toolStrip.addToolButton(delete);
        panel.add(toolStrip);

        label = new Label("This group currently relies on inherited JVM settings");
        panel.add(label);

        form = new Form<Jvm>(Jvm.class);

        TextBoxItem nameItem = new TextBoxItem("name", "Name")
        {
            @Override
            public void setEnabled(boolean b) {
                if(b && !hasJvm)
                    super.setEnabled(b);
                else if (!b)
                    super.setEnabled(b);
            }
        };
        TextBoxItem heapItem = new TextBoxItem("heapSize", "Heap Size");
        TextBoxItem maxHeapItem = new TextBoxItem("maxHeapSize", "Max Heap Size");
        //CheckBoxItem debugItem = new CheckBoxItem("debugEnabled", "Debug Enabled?");
        //TextBoxItem debugOptionsItem = new TextBoxItem("debugOptions", "Debug Options");

        form.setFields(nameItem, heapItem, maxHeapItem);
        form.setEnabled(false);

        panel.add(form.asWidget());

        return panel;
    }

    private void onSave() {
        form.setEnabled(false);

        if(hasJvm)
            presenter.onSaveJvm(selectedRecord.getGroupName(), form.getEditedEntity().getName(), form.getChangedValues());
        else
            presenter.onCreateJvm(selectedRecord.getGroupName(), form.getUpdatedEntity());
    }

    private void onEdit() {
      form.setEnabled(true);
    }

    public void setSelectedRecord(ServerGroupRecord record) {
        this.selectedRecord = record;

        Jvm jvm = record.getJvm();
        hasJvm = jvm!=null;

        label.setVisible(!hasJvm);

        if(jvm!=null)
            form.edit(jvm);
        else
            form.edit(factory.jvm().as());

    }
}
