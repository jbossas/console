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
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.ButtonItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/8/11
 */
public class InstancesView extends SuspendableViewImpl implements InstancesPresenter.MyView {

    private InstancesPresenter presenter;
    private ContentHeaderLabel nameLabel;
    private ListDataProvider<ServerInstance> instanceProvider;
    private String selectedHost = null;
    private ComboBox configFilter;
    private CellTable<ServerInstance> instanceTable;

    @Override
    public void setPresenter(InstancesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Instances");
        layout.add(titleBar);

        VerticalPanel vpanel = new VerticalPanel();
        vpanel.setStyleName("fill-layout-width");
        vpanel.getElement().setAttribute("style", "padding:15px;");

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);

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
        layout.setWidgetTopHeight(scroll, 35, Style.Unit.PX, 60, Style.Unit.PCT);


        // ----------------------------------------------------------------------

        LayoutPanel formPanel = new LayoutPanel();
        //formPanel.getElement().setAttribute("style", "background-color:#ffffff;margin:15px;");

        final Form<ServerInstance> form = new Form<ServerInstance>(ServerInstance.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Instance Name");
        TextItem serverItem = new TextItem("server", "Server Configuration");

        final ButtonItem enableItem = new ButtonItem("running", "Action") {
            @Override
            public void setValue(Boolean b) {

                if(b)
                    button.setText("Stop");
                else
                    button.setText("Start");

            }
        };

        enableItem.addClickHandler(new ClickHandler() {
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
        });

        form.setFields(nameItem, enableItem, serverItem);
        form.bind(instanceTable);

        Widget formWidget = form.asWidget();
        formWidget.getElement().setAttribute("style", "margin-top:15px;");

        formPanel.add(formWidget);


        formPanel.setWidgetTopHeight(formWidget, 5, Style.Unit.PX, 100, Style.Unit.PCT);

        //layout.add(formPanel);
        //layout.setWidgetBottomHeight(formPanel, 0, Style.Unit.PX, 30, Style.Unit.PCT);


        // ----------------------------------------------------------

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        tabLayoutpanel.add(formPanel, "Instance Details");
        tabLayoutpanel.add(new HTML("This going to display heap, permgen, etc, "), "JVM Status");
        //tabLayoutpanel.add(new HTML("Baz"), "Other");

        tabLayoutpanel.selectTab(0);

        layout.add(tabLayoutpanel);
        layout.setWidgetBottomHeight(tabLayoutpanel, 0, Style.Unit.PX, 40, Style.Unit.PCT);

        return layout;
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
        configFilter.setItemSelected(0, true);
    }

    private String buildToken(String serverName) {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.ServerPresenter+
                ";host="+selectedHost +
                ";server=" + serverName;
        return token;
    }
}
