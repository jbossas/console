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

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.StatusItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/8/11
 */
public class ServerInstancesView extends SuspendableViewImpl implements ServerInstancesPresenter.MyView {

    private ServerInstancesPresenter presenter;
    private ContentHeaderLabel nameLabel;
    private ListDataProvider<ServerInstance> instanceProvider;
    private String selectedHost = null;
    private ComboBox groupFilter;
    private CellTable<ServerInstance> instanceTable;
    private ToolButton startBtn;

    @Override
    public void setPresenter(ServerInstancesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(Console.CONSTANTS.common_label_serverInstances());
        layout.add(titleBar);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("rhs-content-panel");

        // ----------------------------------------------------------------------

        nameLabel = new ContentHeaderLabel("replace me");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverInstance());
        horzPanel.add(image);
        horzPanel.add(nameLabel);

        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);

        // ----------------------------------------------------------------------

        vpanel.add(new ContentGroupLabel("Server Instances"));

        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");

        groupFilter = new ComboBox();
        groupFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterByGroup(event.getValue());
            }
        });

        Widget typeFilterWidget = groupFilter.asWidget();
        typeFilterWidget.getElement().setAttribute("width", "200px;");


        tableOptions.add(new Label(Console.CONSTANTS.common_label_serverGroup()+":"));
        tableOptions.add(typeFilterWidget);

        tableOptions.getElement().setAttribute("style", "float:right;");
        vpanel.add(tableOptions);

        // ----------------------------------------------------------------------

        instanceTable = new DefaultCellTable<ServerInstance>(10);
        instanceProvider = new ListDataProvider<ServerInstance>();
        instanceProvider.addDataDisplay(instanceTable);

        // Create columns
        Column<ServerInstance, String> nameColumn = new Column<ServerInstance, String>(new TextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getName();
            }
        };


        Column<ServerInstance, String> groupColumn = new Column<ServerInstance, String>(new TextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getGroup();
            }
        };

        Column<ServerInstance, ImageResource> statusColumn =
                new Column<ServerInstance, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ServerInstance instance) {

                ImageResource res = null;

                if(instance.isRunning())
                    res = Icons.INSTANCE.statusGreen_small();
                else
                    res = Icons.INSTANCE.statusRed_small();

                return res;
            }
        };

        instanceTable.addColumn(nameColumn, Console.CONSTANTS.common_label_server());
        instanceTable.addColumn(groupColumn, Console.CONSTANTS.common_label_serverGroup());
        instanceTable.addColumn(statusColumn, Console.CONSTANTS.common_label_status());
        vpanel.add(instanceTable);


        // scroll enabled
        ScrollPanel scroll = new ScrollPanel();
        scroll.add(vpanel);

        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 28, Style.Unit.PX, 100, Style.Unit.PCT);


        // ----------------------------------------------------------------------

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.setStyleName("fill-layout-width");

        final Form<ServerInstance> form = new Form<ServerInstance>(ServerInstance.class);
        form.setNumColumns(2);

        ToolStrip formTools = new ToolStrip();
        startBtn = new ToolButton("Start/Stop", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                String state = form.getEditedEntity().isRunning() ? "stop" : "start";
                Feedback.confirm(Console.CONSTANTS.common_label_serverInstances(), Console.MESSAGES.changeServerStatus(state, form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    ServerInstance instance = form.getEditedEntity();
                                    presenter.startServer(instance.getServer(), !instance.isRunning());
                                }
                            }
                        });
            }
        });
        formTools.addToolButtonRight(startBtn);
        form.addEditListener(new EditListener<ServerInstance>(

        ) {
            @Override
            public void editingBean(ServerInstance serverInstance) {
                String label = serverInstance.isRunning() ? "Stop":"Start";
                startBtn.setText(label);
            }
        });

        /* https://issues.jboss.org/browse/AS7-953
        formTools.addToolButtonRight(new ToolButton("Reload", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                Feedback.confirm("Reload server configuration",
                        "Do you want ot reload the server configuration for server "+form.getEditedEntity().getName()+"?",
                        new Feedback.ConfirmationHandler()
                        {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                {
                                    ServerInstance instance = form.getEditedEntity();
                                    presenter.reloadServer(instance.getServer());
                                }
                            }
                        });
            }
        }));

        */
        formPanel.add(formTools);

        // -----


        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_serverInstance());
        TextItem serverItem = new TextItem("server", Console.CONSTANTS.common_label_serverConfig());
        StatusItem enableItem = new StatusItem("running", "Running?");

        form.setFields(nameItem, serverItem, enableItem);
        form.bind(instanceTable);
        form.setEnabled(false);

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "margin-top:15px;");

        formPanel.add(formWidget);

        // ----------------------------------------------------------
        TabPanel bottomLayout = new TabPanel();
        bottomLayout.addStyleName("default-tabpanel");
        bottomLayout.getElement().setAttribute("style", "padding-top:20px");

        bottomLayout.add(formPanel, "Availability");
        bottomLayout.add(new HTML(""), "JVM Status");

        bottomLayout.selectTab(0);

        vpanel.add(new ContentGroupLabel("Status"));

        vpanel.add(bottomLayout);

        return layout;
    }

    @Override
    public void setSelectedHost(String selectedHost) {
        this.selectedHost = selectedHost;
        nameLabel.setText(Console.MESSAGES.serversRunningOnHost(presenter.getCurrentHostSelection()));
    }

    @Override
    public void updateInstances(List<ServerInstance> instances) {
        instanceProvider.setList(instances);
        if(!instances.isEmpty())
            instanceTable.getSelectionModel().setSelected(instances.get(0), true);
    }

    @Override
    public void updateServerConfigurations(List<Server> servers) {
        List<String> names = new ArrayList<String>(servers.size());
        names.add(""); // de-select filter
        for(Server server : servers)
        {
            if(!names.contains(server.getGroup())) // could be turned into Set based API
                names.add(server.getGroup());
        }
        groupFilter.setValues(names);
    }

    private String buildToken(String serverName) {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.ServerPresenter+
                ";host="+selectedHost +
                ";server=" + serverName;
        return token;
    }
}
