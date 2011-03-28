package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.widgets.LHSNavItem;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
public class InstancesSection implements HostSelectionEvent.HostSelectionListener{


    private LayoutPanel layout;
    private String selectedHost = null;

    public InstancesSection() {

        layout = new LayoutPanel();
        layout.setStyleName("stack-section");

        LHSNavItem overview = new LHSNavItem(
                "Server Status",
                Icons.INSTANCE.inventory_small(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                                Places.fromString(buildToken())
                        );
                    }
                }
        );

        LHSNavItem startNew = new LHSNavItem(
                "Launch Instance",
                Icons.INSTANCE.add_small(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        String token = buildToken() + ";action=new";
                        Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                                Places.fromString(token)
                        );
                    }
                }
        );

        // --------------------------------------------------

        layout.add(overview);
        //layout.add(startNew);

        layout.setWidgetTopHeight(overview, 0, Style.Unit.PX, 25, Style.Unit.PX);
        //layout.setWidgetTopHeight(startNew, 25, Style.Unit.PX, 25, Style.Unit.PX);

        // listen on host selection events
        Console.MODULES.getEventBus().addHandler(
                HostSelectionEvent.TYPE, this
        );
    }

    public Widget asWidget()
    {
        return layout;
    }

    @Override
    public void onHostSelection(String hostName) {
        selectedHost = hostName;
    }

    public void setSelectedHost(String hostName) {
        this.selectedHost = hostName;
    }

    private String buildToken() {
        assert selectedHost!=null : "host selection is null!";
        final String token = "hosts/" + NameTokens.InstancesPresenter+";host="+selectedHost;
        return token;
    }
}
