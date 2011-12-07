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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmEditor;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/18/11
 */
public class HostJVMView extends DisposableViewImpl implements HostJVMPresenter.MyView {


    private HostJVMPresenter presenter;
    private JvmEditor jvmEditor;
    private CellTable<Jvm> table;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Host JVM Configurations");
        layout.add(titleBar);

        ToolStrip toolStrip = new ToolStrip();

        ToolButton addBtn= new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewJVMDialogue();
            }
        });
        addBtn.ensureDebugId(Console.CONSTANTS.debug_label_add_hostJVMView());
        toolStrip.addToolButtonRight(addBtn);

        layout.add(toolStrip);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 70, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        panel.add(new ContentHeaderLabel("Available JVM Configurations"));
        panel.add(new ContentDescription("These JVM configuration are applicable to any server on a host. " +
                "JVM configurations can be assigned to server configuration by name."));


        table = new DefaultCellTable<Jvm>(10);

        TextColumn<Jvm> nameCol = new TextColumn<Jvm>() {
            @Override
            public String getValue(Jvm object) {
                return object.getName();
            }
        };


        table.addColumn(nameCol, "Name");
        //table.addColumn(debugCol, "IsDebugEnabled?");

        panel.add(table);

        // ----


        panel.add(new ContentGroupLabel("JVM Details"));

        jvmEditor = new JvmEditor(presenter);
        jvmEditor.setAddressCallback(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.add("host", Console.MODULES.getCurrentSelectedHost().getName());
                address.add("jvm", "*");
                return address;
            }
        });
        panel.add(jvmEditor.asWidget());

        final SingleSelectionModel<Jvm> selectionModel = new SingleSelectionModel<Jvm>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                jvmEditor.setSelectedRecord("", selectionModel.getSelectedObject());
            }
        });
        table.setSelectionModel(selectionModel);


        return layout;
    }

    @Override
    public void setPresenter(HostJVMPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setJvms(List<Jvm> jvms) {
        table.setRowCount(jvms.size(), true);
        table.setRowData(jvms);

        if(!jvms.isEmpty())
            table.getSelectionModel().setSelected(jvms.get(0), true);
    }
}
