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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmEditor;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

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

        TitleBar titleBar = new TitleBar("Host JVM");
        layout.add(titleBar);

        ToolStrip toolStrip = new ToolStrip();

        ToolButton add= new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewJVMDialogue();
            }
        });

        toolStrip.addToolButtonRight(add);

        layout.add(toolStrip);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---

        panel.add(new ContentHeaderLabel("Host JVM Declarations"));
        panel.add(new HTML("These JVM settings will be inherited by any server on this host"));

        panel.add(new ContentGroupLabel("Available JVM Declarations"));

        table = new DefaultCellTable<Jvm>(10);

        TextColumn<Jvm> nameCol = new TextColumn<Jvm>() {
            @Override
            public String getValue(Jvm object) {
                return object.getName();
            }
        };

        TextColumn<Jvm> debugCol = new TextColumn<Jvm>() {
            @Override
            public String getValue(Jvm object) {
                return String.valueOf(object.isDebugEnabled());
            }
        };

        table.addColumn(nameCol, "Name");
        table.addColumn(debugCol, "IsDebugEnabled?");

        panel.add(table);

        // ----


        panel.add(new ContentGroupLabel("JVM Details"));

        jvmEditor = new JvmEditor(presenter);
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
