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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.shared.jvm.JvmEditor;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * Shows an editable view of a single server group.
 *
 * @author Heiko Braun
 * @date 2/16/11
 */
public class ServerGroupView extends SuspendableViewImpl implements ServerGroupPresenter.MyView {

    private ServerGroupPresenter presenter;
    private VerticalPanel panel;

    private PropertyEditor propertyEditor;
    private JvmEditor jvmEditor;

    private DefaultCellTable<ServerGroupRecord> serverGroupTable;
    private ListDataProvider<ServerGroupRecord> serverGroupProvider;
    private ServerGroupDetails details;

    @Override
    public void setPresenter(ServerGroupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(Console.CONSTANTS.common_label_serverGroupConfigurations());
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();

        ToolButton newServerGroupBtn =  new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewGroupDialoge();
            }
        });
        newServerGroupBtn.ensureDebugId(Console.CONSTANTS.debug_label_add_serverGroupsView());
        toolStrip.addToolButtonRight(newServerGroupBtn);
        
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                final ServerGroupRecord serverGroup = getSelectionModel().getSelectedObject();
                Feedback.confirm(
                        Console.MESSAGES.deleteServerGroup(),
                        Console.MESSAGES.deleteServerGroupConfirm(serverGroup.getGroupName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.onDeleteGroup(serverGroup);
                            }
                        });
            }
        });

        deleteBtn.ensureDebugId(Console.CONSTANTS.debug_label_delete_serverGroupsView());
        toolStrip.addToolButtonRight(deleteBtn);

        layout.add(toolStrip);

        // ----

        panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 70, Style.Unit.PX, 100, Style.Unit.PCT);

        // ---------------------------------------------

        panel.add(new ContentHeaderLabel(Console.MESSAGES.available(Console.CONSTANTS.common_label_serverGroupConfigurations())));
        panel.add(new ContentDescription(Console.CONSTANTS.common_serverGroups_desc()));

        serverGroupTable = new DefaultCellTable<ServerGroupRecord>(10);
        serverGroupProvider = new ListDataProvider<ServerGroupRecord>();
        serverGroupProvider.addDataDisplay(serverGroupTable);

        // Create columns
        Column<ServerGroupRecord, String> nameColumn = new Column<ServerGroupRecord, String>(new TextCell()) {
            @Override
            public String getValue(ServerGroupRecord object) {
                return object.getGroupName();
            }
        };


        Column<ServerGroupRecord, String> profileColumn = new Column<ServerGroupRecord, String>(new TextCell()) {
            @Override
            public String getValue(ServerGroupRecord object) {
                return object.getProfileName();
            }
        };


        serverGroupTable.addColumn(nameColumn, "Group Name");
        serverGroupTable.addColumn(profileColumn, "Profile");

        panel.add(serverGroupTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(serverGroupTable);
        panel.add(pager);


        // ---------------------------------------------------

        details =new ServerGroupDetails(presenter);

        // ---------------------------------------------------


        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");

        bottomLayout.add(details.asWidget(), "Attributes");
        jvmEditor = new JvmEditor(presenter);
        jvmEditor.setAddressCallback(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.add("server-group", getSelectionModel().getSelectedObject().getGroupName());
                address.add("jvm", "*");
                return address;
            }
        });
        bottomLayout .add(jvmEditor.asWidget(), Console.CONSTANTS.common_label_virtualMachine());

        propertyEditor = new PropertyEditor(presenter);
        //propertyEditor.setHelpText("A system property to set on all servers in this server-group.");
        bottomLayout.add(propertyEditor.asWidget(), Console.CONSTANTS.common_label_systemProperties());
        propertyEditor.setAllowEditProps(false);

        bottomLayout.selectTab(0);

        panel.add(new ContentGroupLabel("Server Group"));
        panel.add(bottomLayout);

        details.bind(serverGroupTable);


        // --------------------


        serverGroupTable.getSelectionModel().addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                ServerGroupRecord group = getSelectionModel().getSelectedObject();
                presenter.loadJVMConfiguration(group);
                presenter.loadProperties(group);
            }
        });

        return layout;
    }

    public void setServerGroups(final List<ServerGroupRecord> groups) {

        serverGroupProvider.setList(groups);
        if(!groups.isEmpty())
            getSelectionModel().setSelected(groups.get(0), true);
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        details.setSocketBindings(result);
    }

    private SingleSelectionModel<ServerGroupRecord> getSelectionModel() {
        return ((SingleSelectionModel<ServerGroupRecord>) serverGroupTable.getSelectionModel());
    }

    @Override
    public void setJvm(ServerGroupRecord group, Jvm jvm) {
        jvmEditor.setSelectedRecord(group.getGroupName(), jvm);
    }

    @Override
    public void setProperties(ServerGroupRecord group, List<PropertyRecord> properties) {
        propertyEditor.setProperties(group.getGroupName(), properties);
    }
}
