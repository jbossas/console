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

package org.jboss.as.console.client.domain.hosts;

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
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
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
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerConfigView extends SuspendableViewImpl implements ServerConfigPresenter.MyView{

    private ServerConfigPresenter presenter;

    private ServerConfigDetails details;
    private JvmEditor jvmEditor;
    private PropertyEditor propertyEditor;

    private PortsView portsView;
    private DefaultCellTable<Server> serverConfigTable;
    private ListDataProvider serverConfigProvider;

    public ServerConfigView() {
        serverConfigTable = new DefaultCellTable<Server>(10);
        serverConfigProvider = new ListDataProvider<Server>();
        serverConfigProvider.addDataDisplay(serverConfigTable);
    }

    @Override
    public void setPresenter(ServerConfigPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(Console.CONSTANTS.common_label_serverConfigs());
        layout.add(titleBar);

        // ----

        final ToolStrip toolStrip = new ToolStrip();

        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewConfigDialoge();
            }
        }));

        ToolButton delete = new ToolButton(Console.CONSTANTS.common_label_delete());
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {

                final Server server = getSelectionModel().getSelectedObject();

                Feedback.confirm(
                        Console.MESSAGES.deleteServerConfig(),
                        Console.MESSAGES.deleteServerConfigConfirm(server.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.tryDelete(server);
                            }
                        });
            }
        });

        toolStrip.addToolButtonRight(delete);

        layout.add(toolStrip);

        // ---

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scrollPanel = new ScrollPanel(panel);
        layout.add(scrollPanel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(toolStrip, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scrollPanel, 70, Style.Unit.PX, 100, Style.Unit.PCT);


        // ------------------------------------------------------

        // table

        panel.add(new ContentHeaderLabel("Available Server Configurations"));
        panel.add(new ContentDescription("A server configuration does specify the overall configuration of a server. A server configuration can be started and perform work. Server configurations belong to server groups."));

        // Create columns
        Column<Server, String> nameColumn = new Column<Server, String>(new TextCell()) {
            @Override
            public String getValue(Server object) {
                return object.getName();
            }
        };


        Column<Server, String> groupColumn = new Column<Server, String>(new TextCell()) {
            @Override
            public String getValue(Server object) {
                return object.getGroup();
            }
        };


        serverConfigTable.addColumn(nameColumn, "Configuration Name");
        serverConfigTable.addColumn(groupColumn, Console.CONSTANTS.common_label_serverGroup());

        panel.add(serverConfigTable);

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(serverConfigTable);
        panel.add(pager);


        // ---------------------

        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");

        // details

        details = new ServerConfigDetails(presenter);
        bottomLayout.add(details.asWidget(), "Attributes");
        details.bind(serverConfigTable);

        // jvm editor
        jvmEditor = new JvmEditor(presenter);
        jvmEditor.setAddressCallback(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {

                ModelNode address = new ModelNode();
                address.add("host", presenter.getSelectedHost());
                address.add("server-config", "*");
                address.add("jvm", "*");
                return address;
            }
        });
        bottomLayout.add(jvmEditor.asWidget(), Console.CONSTANTS.common_label_virtualMachine());

        propertyEditor = new PropertyEditor(presenter);
        propertyEditor.setHelpText("A system property to set on this server.");
        bottomLayout.add(propertyEditor.asWidget(), Console.CONSTANTS.common_label_systemProperties());
        propertyEditor.setAllowEditProps(false);

        portsView = new PortsView();
        //bottomLayout.add(portsView.asWidget(), "Ports");

        // ------------

        panel.add(new ContentGroupLabel("Server Configuration"));
        panel.add(bottomLayout);

        bottomLayout.selectTab(0);


        // --------------------


        serverConfigTable.getSelectionModel().addSelectionChangeHandler(
                new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                Server server = getSelectionModel().getSelectedObject();
                presenter.loadJVMConfiguration(server);
                presenter.loadProperties(server);
                presenter.loadPorts(server);
            }
        });

        return layout;
    }

    private SingleSelectionModel<Server> getSelectionModel() {
        return ((SingleSelectionModel<Server>) serverConfigTable.getSelectionModel());
    }

    @Override
    public void setPorts(String socketBinding, Server server, List<SocketBinding> sockets) {
        //portsView.setPorts(server.getSocketBinding(), server, sockets);
    }

    @Override
    public void setJvm(String reference, Jvm jvm) {
        jvmEditor.setSelectedRecord(reference, jvm);
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        details.setAvailableSockets(result);
    }

    @Override
    public void setProperties(String reference, List<PropertyRecord> properties) {
        propertyEditor.setProperties(reference, properties);
    }

    @Override
    public void setConfigurations(String selectedHost, List<Server> servers, String selectedConfigName) {
        serverConfigProvider.setList(servers);

        if(!servers.isEmpty())
        {
        	if(selectedConfigName == null || selectedConfigName.equals("")) {
        		getSelectionModel().setSelected(servers.get(0), true);
        	}
        	else {
        		getSelectionModel().setSelected(findSelectedServer(servers, selectedConfigName), true);
        	}
        }

    }
    
    private Server findSelectedServer(List<Server> servers,String name){
    	for (Server server : servers) {
			if(server.getName().equals(name)) {
				return server;
			}
		}
    	return servers.get(0);	
    } 

}
