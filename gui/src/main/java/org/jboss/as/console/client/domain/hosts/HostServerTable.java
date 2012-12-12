package org.jboss.as.console.client.domain.hosts;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.state.GlobalHostSelection;
import org.jboss.as.console.client.shared.state.HostList;
import org.jboss.as.console.client.shared.state.ServerInstanceList;
import org.jboss.as.console.client.widgets.lists.DefaultCellList;
import org.jboss.as.console.client.widgets.popups.DefaultPopup;
import org.jboss.ballroom.client.widgets.common.DefaultButton;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.Collections;
import java.util.List;

/**
 * A miller column based selection of host/serve combinations
 *
 * @author Heiko Braun
 * @date 12/9/11
 */
public class HostServerTable {

    private static final int ESCAPE = 27;
    public final static double GOLDEN_RATIO = 1.618;

    private boolean isRightToLeft = false;
    private HostServerManagement presenter;

    private CellList<Host> hostList;
    private CellList<ServerInstance> serverList;

    private ListDataProvider<Host> hostProvider = new ListDataProvider<Host>();
    private ListDataProvider<ServerInstance> serverProvider = new ListDataProvider<ServerInstance>();

    private PopupPanel popup;

    private HorizontalPanel header;
    private HTML currentDisplayedValue;
    int popupWidth = -1;
    private String description = null;
    private HTML ratio;
    private DefaultPager hostPager  ;
    private DefaultPager serverPager;

    private int clipAt = 20;

    private SingleSelectionModel<Host> hostSelectionModel;
    private SingleSelectionModel<ServerInstance> serverSelectionModel;


    public HostServerTable(HostServerManagement presenter) {
        this.presenter = presenter;
    }

    private static String clip(String value, int clipping)
    {
        String result = value;
        if(value!=null && value.length()>clipping)
            result = value.substring(0, clipping)+"...";
        return result;
    }

    public void setRightToLeft(boolean rightToLeft) {
        isRightToLeft = rightToLeft;
    }

    public void setPopupWidth(int popupWidth) {
        this.popupWidth = popupWidth;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Widget asWidget() {

        final String panelId = "popup_"+ HTMLPanel.createUniqueId();
        popup = new DefaultPopup(DefaultPopup.Arrow.TOPLEFT);

        popup.getElement().setId(panelId);

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:5px;");
        //layout.addStyleName("tablepicker-popup");

        if(description!=null)
            layout.add(new HTML(description));

        ratio = new HTML("RATIO HERE");
        layout.add(ratio);
        // --------------

        hostList = new DefaultCellList<Host>(new HostCell());
        hostList.setPageSize(6);
        hostSelectionModel = new SingleSelectionModel<Host>();
        hostList.setSelectionModel(hostSelectionModel);
        hostList.addStyleName("fill-layout-width");
        hostList.addStyleName("clip-text") ;

        serverList = new DefaultCellList<ServerInstance>(new ServerCell());
        serverSelectionModel = new SingleSelectionModel<ServerInstance>(new ProvidesKey<ServerInstance>() {
            @Override
            public Object getKey(ServerInstance serverInstance) {
                return serverInstance.getName();
            }
        });
        serverList.setSelectionModel(serverSelectionModel);
        serverList.setPageSize(6);
        serverList.addStyleName("fill-layout-width");
        serverList.addStyleName("clip-text") ;

        hostProvider = new ListDataProvider<Host>();
        serverProvider = new ListDataProvider<ServerInstance>();

        hostProvider.addDataDisplay(hostList);
        serverProvider.addDataDisplay(serverList);

        hostList.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {

                serverProvider.setList(Collections.EMPTY_LIST);

                Host selectedHost = getSelectedHost();

                Console.MODULES.getEventBus().fireEvent(
                        new GlobalHostSelection(selectedHost.getName())
                );

                if(selectedHost!=null)
                {
                    presenter.loadServer(selectedHost.getName());
                }

            }
        });

        serverList.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ServerInstance server = getSelectedServer();
                Host selectedHost = getSelectedHost();

                if(selectedHost!=null &server!=null)
                {
                    presenter.onServerSelected(selectedHost, server);
                    updateDisplay();
                }
            }
        });


        HorizontalPanel millerHeader = new HorizontalPanel();
        Label host = new Label("Host");
        millerHeader.add(host);
        Label server = new Label("Server");

        millerHeader.add(server);
        millerHeader.getElement().setAttribute("style", "width:100%;border-bottom:1px solid #A7ABB4");

        host.getElement().getParentElement().setAttribute("width", "50%");
        server.getElement().getParentElement().setAttribute("width", "50%");


        layout.add(millerHeader);

        millerHeader.getElement().addClassName("cellTableHeader");
        millerHeader.getElement().getParentElement().setAttribute("style", "vertical-align:bottom");

        HorizontalPanel millerPanel = new HorizontalPanel();
        millerPanel.setStyleName("fill-layout");


        hostPager = new DefaultPager();
        hostPager.setDisplay(hostList);
        FlowPanel lhs = new FlowPanel();
        lhs.add(hostList);
        lhs.add(hostPager.asWidget());

        millerPanel.add(lhs);


        serverPager = new DefaultPager();
        serverPager.setDisplay(serverList);
        FlowPanel rhs = new FlowPanel();
        rhs.add(serverList);
        rhs.add(serverPager.asWidget());
        millerPanel.add(rhs);

        hostPager.setVisible(false);
        serverPager.setVisible(false);

        lhs.getElement().getParentElement().setAttribute("style", "border-right:1px solid #A7ABB4");
        lhs.getElement().getParentElement().setAttribute("width", "50%");
        lhs.getElement().getParentElement().setAttribute("valign", "top");
        rhs.getElement().getParentElement().setAttribute("valign", "top");
        rhs.getElement().getParentElement().setAttribute("width", "50%");

        ScrollPanel scroll = new ScrollPanel(millerPanel);
        layout.add(scroll);

        DefaultButton doneBtn = new DefaultButton("Done", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popup.hide();
            }
        });
        doneBtn.getElement().setAttribute("style","float:right");
        layout.add(doneBtn);

        // --------------


        popup.setWidget(layout);


        // --------------

        currentDisplayedValue = new HTML("&nbsp;");
        currentDisplayedValue.setStyleName("table-picker-value");

        header = new HorizontalPanel();
        header.setStyleName("table-picker");
        header.add(currentDisplayedValue);

        //Image img = new Image(ConsoleIcons.INSTANCE.tablePicker());
        HTML icon = new HTML("<span style='font-size:18px;cursor:pointer'><i class='icon-caret-down'></i></span>");
        header.add(icon);

        currentDisplayedValue.getElement().getParentElement().setAttribute("width", "100%");

        icon.getParent().getElement().setAttribute("width", "18");

        header.getElement().setAttribute("width", "100%");
        header.getElement().setAttribute("cellspacing", "0");
        header.getElement().setAttribute("cellpadding", "0");
        header.getElement().setAttribute("border", "0");


        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                openPanel();
            }
        };

        currentDisplayedValue.addClickHandler(clickHandler);
        icon.addClickHandler(clickHandler);

        return header;
    }

    private void updateDisplay() {

        //String host = clip(getSelectedHost().getName(), clipAt);
        String server = clip(getSelectedServer().getName(), clipAt);

        currentDisplayedValue.setText(server);
    }

    public Host getSelectedHost() {
        return ((SingleSelectionModel<Host>) hostList.getSelectionModel()).getSelectedObject();
    }

    private ServerInstance getSelectedServer() {
        return ((SingleSelectionModel<ServerInstance>) serverList.getSelectionModel()).getSelectedObject();
    }

    private void openPanel() {

        int winWidth = popupWidth!=-1 ? popupWidth : header.getOffsetWidth() * 2;
        int winHeight = (int) ( winWidth / GOLDEN_RATIO );

        popup.setWidth(winWidth +"px");
        popup.setHeight(winHeight + "px");

        // right to left
        if(isRightToLeft)
        {
            int popupLeft = header.getAbsoluteLeft() - (winWidth - header.getOffsetWidth());
            popup.setPopupPosition(
                    popupLeft-15,
                    header.getAbsoluteTop()+32
            );
        }
        else
        {
            int popupLeft = header.getAbsoluteLeft();
            popup.setPopupPosition(
                    popupLeft,
                    header.getAbsoluteTop()+38
            );
        }

        popup.show();

    }

    public void clearSelection() {
        currentDisplayedValue.setText("");
    }

    /**
     * Display the currently active servers for selection
     * @param serverList
     */
    public void setServer(ServerInstanceList serverList) {

        List<ServerInstance> server = serverList.getServer();
        serverProvider.setList(server);

        serverPager.setVisible(server.size() >= 5);

        if(server.isEmpty())
        {
            currentDisplayedValue.setText("No Server");
        }

        this.serverList.getSelectionModel().setSelected(serverList.getSelectedServer(), true);
    }

    public void setHosts(HostList hosts) {

        ratio.setText("");

        hostPager.setVisible(hosts.getHosts().size()>=5);

        hostProvider.setList(hosts.getHosts());

        hostList.getSelectionModel().setSelected(hosts.getSelectedHost(), true);

    }

    public void defaultHostSelection() {

        if(hostProvider.getList().size()>0)
        {
            Host defaultHost = hostList.getVisibleItem(0);
            Log.debug("Select default host: "+defaultHost.getName());
            selectHost(defaultHost);
        }
    }

    private void selectHost(String hostName) {

        for(Host host : hostProvider.getList())
        {
            if(hostName.equals(host.getName()))
            {
                selectHost(host);
                break;
            }
        }
    }

    /**
     * will reload the server list
     * @param host
     */
    private void selectHost(Host host) {
        hostList.getSelectionModel().setSelected(host, true);
    }

    interface Template extends SafeHtmlTemplates {
        @Template("<div class='server-selection-host'>{0}</div>")
        SafeHtml message(String title);
    }

    interface ServerTemplate extends SafeHtmlTemplates {
        @Template("<table class='server-selection-server' width='90%'><tr><td>{0}</td><td width='10%'><i class='{1}'></i></td></tr></table>")
        SafeHtml message(String title, String icon);
    }


    // -----

    private static final Template HOST_TEMPLATE = GWT.create(Template.class);
    private static final ServerTemplate SERVER_TEMPLATE = GWT.create(ServerTemplate.class);

    public class HostCell extends AbstractCell<Host> {

        @Override
        public void render(
                Context context,
                Host host,
                SafeHtmlBuilder safeHtmlBuilder)
        {
            safeHtmlBuilder.append(HOST_TEMPLATE.message(clip(host.getName(), clipAt)));
        }

    }

    public class ServerCell extends AbstractCell<ServerInstance> {

        @Override
        public void render(
                Context context,
                ServerInstance server,
                SafeHtmlBuilder safeHtmlBuilder)
        {
            String icon = server.isRunning() ? "icon-ok":"icon-ban-circle";
            String name = clip(server.getName(), clipAt);
            safeHtmlBuilder.append(SERVER_TEMPLATE.message(name, icon));
        }

    }
}


