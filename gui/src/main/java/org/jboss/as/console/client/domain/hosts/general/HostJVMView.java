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

package org.jboss.as.console.client.domain.hosts.general;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmEditor;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class HostJVMView extends DisposableViewImpl implements HostJVMPresenter.MyView {

    private HostJVMPresenter presenter;
    private JvmEditor jvmEditor;
    private DefaultCellTable<Jvm> table;
    private ListDataProvider<Jvm> dataProvider;

    @Override
    public Widget createWidget() {

        ToolStrip toolStrip = new ToolStrip();

        ToolButton addBtn= new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewJVMDialogue();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_hostJVMView());
        toolStrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final Jvm entity = ((SingleSelectionModel<Jvm>) table.getSelectionModel()).getSelectedObject();

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("JVM Configuration"),
                        Console.MESSAGES.deleteConfirm("JVM Configuration"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.onDeleteJvm("", entity);
                            }
                        });

            }
        });
        removeBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_hostJVMView());
        toolStrip.addToolButtonRight(removeBtn);

        // ---

        table = new DefaultCellTable<Jvm>(8, new ProvidesKey<Jvm>() {
            @Override
            public Object getKey(Jvm item) {
                return item.getName();
            }
        });
        dataProvider = new ListDataProvider<Jvm>();
        dataProvider.addDataDisplay(table);

        TextColumn<Jvm> nameCol = new TextColumn<Jvm>() {
            @Override
            public String getValue(Jvm object) {
                return object.getName();
            }
        };


        table.addColumn(nameCol, "Name");
        //table.addColumn(debugCol, "IsDebugEnabled?");

        jvmEditor = new JvmEditor(presenter, false, false);
        jvmEditor.setAddressCallback(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.add("host", "*");
                address.add("jvm", "*");
                return address;
            }
        });

        final SingleSelectionModel<Jvm> selectionModel = new SingleSelectionModel<Jvm>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                jvmEditor.setSelectedRecord("", selectionModel.getSelectedObject());
            }
        });
        table.setSelectionModel(selectionModel);


        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("JVM Configurations")
                .setDescription(Console.CONSTANTS.hosts_jvm_desc())
                .setHeadline(Console.CONSTANTS.hosts_jvm_title())
                .setMaster(Console.MESSAGES.available("JVM Configurations"), table)
                .setMasterTools(toolStrip)
                .setDetail(Console.CONSTANTS.common_label_selection(), jvmEditor.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(HostJVMPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setJvms(List<Jvm> jvms) {
        dataProvider.setList(jvms);

        table.selectDefaultEntity();
    }
}
