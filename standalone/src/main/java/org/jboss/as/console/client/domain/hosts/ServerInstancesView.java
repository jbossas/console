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
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.ComboBox;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.SplitEditorPanel;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.ButtonItem;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

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
    private ComboBox configFilter;
    private CellTable<ServerInstance> instanceTable;

    @Override
    public void setPresenter(ServerInstancesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        SplitEditorPanel editorPanel = new SplitEditorPanel();
        LayoutPanel layout = editorPanel.getTopLayout();

        TitleBar titleBar = new TitleBar("Server Instances");
        layout.add(titleBar);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        // ----------------------------------------------------------------------

        nameLabel = new ContentHeaderLabel("Server Status");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        Image image = new Image(Icons.INSTANCE.serverInstance());
        horzPanel.add(image);
        horzPanel.add(nameLabel);

        image.getElement().getParentElement().setAttribute("width", "25");

        vpanel.add(horzPanel);

        // ----------------------------------------------------------------------


        HorizontalPanel tableOptions = new HorizontalPanel();
        tableOptions.getElement().setAttribute("cellpadding", "2px");

        configFilter = new ComboBox();
        configFilter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onFilterType(event.getValue());
            }
        });

        Widget typeFilterWidget = configFilter.asWidget();
        typeFilterWidget.getElement().setAttribute("style", "width:200px;");


        tableOptions.add(new Label("Config:"));
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


        Column<ServerInstance, String> serverColumn = new Column<ServerInstance, String>(new TextCell()) {
            @Override
            public String getValue(ServerInstance object) {
                return object.getServer();
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

        instanceTable.addColumn(nameColumn, "Instance Name");
        instanceTable.addColumn(serverColumn, "Config");
        instanceTable.addColumn(statusColumn, "Status");
        vpanel.add(instanceTable);


        // scroll enabled
        ScrollPanel scroll = new ScrollPanel();
        scroll.add(vpanel);

        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 100, Style.Unit.PCT);


        // ----------------------------------------------------------------------

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.setStyleName("fill-layout-width");

        final Form<ServerInstance> form = new Form<ServerInstance>(ServerInstance.class);
        //form.setNumColumns(2);

        ToolStrip formTools = new ToolStrip();
        formTools.addToolButton(new ToolButton("Start/Stop", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                String state = form.getEditedEntity().isRunning() ? "Stop" : "Start";
                Feedback.confirm(state + " server instance", "Do you want to " + state + " this server "+form.getEditedEntity().getName()+"?",
                        new Feedback.ConfirmationHandler()
                        {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                {
                                    ServerInstance instance = form.getEditedEntity();
                                    presenter.startServer(instance.getServer(), !instance.isRunning());
                                }
                            }
                        });
            }
        }));

        formPanel.add(formTools);

        // -----


        TextItem nameItem = new TextItem("name", "Instance Name");
        TextItem serverItem = new TextItem("server", "Server Configuration");
        CheckBoxItem enableItem = new CheckBoxItem("running", "Running?");

        form.setFields(nameItem, serverItem, enableItem);
        form.bind(instanceTable);
        form.setEnabled(false);

        Widget formWidget = form.asWidget();
        //formWidget.getElement().setAttribute("style", "margin-top:15px;");

        formPanel.add(formWidget);

        // ----------------------------------------------------------
        TabLayoutPanel bottomLayout = editorPanel.getBottomLayout();

        bottomLayout.add(formPanel, "Instance Details");
        bottomLayout.add(new HTML("This going to display heap, permgen, etc, "), "JVM Status");

        bottomLayout.selectTab(0);

        return editorPanel.asWidget();
    }

    @Override
    public void setSelectedHost(String selectedHost) {
        this.selectedHost = selectedHost;
        //nameLabel.setText("Server Status ("+selectedHost+")");
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
            names.add(server.getName());
        }
        configFilter.setValues(names);
    }

    private String buildToken(String serverName) {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.ServerPresenter+
                ";host="+selectedHost +
                ";server=" + serverName;
        return token;
    }
}
