package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.widgets.icons.ConsoleIcons;
import org.jboss.as.console.client.widgets.lists.DefaultCellList;
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
        popup = new PopupPanel(true, true) {

            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        // Dismiss when escape is pressed
                        popup.hide();
                    }
                }
            }

            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
            }
        };

        popup.getElement().setId(panelId);
        popup.setStyleName("default-popup");


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
        hostList.setSelectionModel(new SingleSelectionModel<Host>());
        hostList.addStyleName("fill-layout-width");
        hostList.addStyleName("clip-text") ;

        serverList = new DefaultCellList<ServerInstance>(new ServerCell());
        serverList.setSelectionModel(new SingleSelectionModel<ServerInstance>());
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
                Host selectedHost = getSelectedHost();
                presenter.loadServer(selectedHost);
            }
        });

        serverList.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ServerInstance server = getSelectedServer();
                presenter.onServerSelected(getSelectedHost(), getSelectedServer());
                updateDisplay();
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

        Image img = new Image(ConsoleIcons.INSTANCE.tablePicker());
        header.add(img);

        currentDisplayedValue.getElement().getParentElement().setAttribute("width", "100%");

        img.getParent().getElement().setAttribute("width", "18");

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
        img.addClickHandler(clickHandler);

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
                    header.getAbsoluteTop()+21
            );
        }
        else
        {
            int popupLeft = header.getAbsoluteLeft();
            popup.setPopupPosition(
                    popupLeft,
                    header.getAbsoluteTop()+21
            );
        }

        popup.show();

    }

    public void clearSelection() {
        currentDisplayedValue.setText("");
    }

    /**
     * Display the currently active servers for selection
     * @param servers
     */
    public void setServer(List<ServerInstance> servers) {

        serverPager.setVisible(servers.size()>=5);

        serverProvider.setList(servers);

        if(!servers.isEmpty())
            serverList.getSelectionModel().setSelected(servers.get(0), true);
    }

    public void setHosts(List<Host> hosts) {

        ratio.setText("");

        hostPager.setVisible(hosts.size()>=5);

        hostProvider.setList(hosts);
        serverProvider.setList(Collections.EMPTY_LIST);
    }

    public void defaultHostSelection() {
        if(hostList.getRowCount()>0)
        {
            selectHost(hostList.getVisibleItem(0));
        }
    }

    public void selectHost(Host host) {
        hostList.getSelectionModel().setSelected(host, true);

    }

    public void selectServer(ServerInstance server) {

        serverList.getSelectionModel().setSelected(server, true);
    }

    interface Template extends SafeHtmlTemplates {
        @Template("<div class='server-selection-host'>{0}</div>")
        SafeHtml message(String title);
    }

    interface ServerTemplate extends SafeHtmlTemplates {
        @Template("<div class='server-selection-server'>{0}</div>")
        SafeHtml message(String title);
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
            String state = server.isRunning() ? " (active)":"";
            safeHtmlBuilder.append(SERVER_TEMPLATE.message(clip(server.getName(), clipAt)+state));
        }

    }
}


