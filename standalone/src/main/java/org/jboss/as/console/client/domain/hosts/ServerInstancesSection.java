package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.core.Places;
import org.jboss.as.console.client.widgets.DisclosureStackHeader;
import org.jboss.as.console.client.widgets.LHSNavigationTree;
import org.jboss.as.console.client.widgets.LHSTreeItem;

/**
 * @author Heiko Braun
 * @date 3/4/11
 */
public class ServerInstancesSection implements HostSelectionEvent.HostSelectionListener{


    private String selectedHost = null;
    private DisclosurePanel panel;

    private LHSNavigationTree instanceTree;

    public ServerInstancesSection() {

        panel = new DisclosureStackHeader("Server Instances").asWidget();

        instanceTree = new LHSNavigationTree();

        LHSTreeItem overview = new LHSTreeItem("Server Status", new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) {
                String token = buildToken();
                Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        Places.fromString(token)
                );
            }
        });

        instanceTree.addItem(overview);

        // listen on host selection events
        Console.MODULES.getEventBus().addHandler(
                HostSelectionEvent.TYPE, this
        );

        panel.setContent(instanceTree);
    }

    public Widget asWidget()
    {
        return panel;
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
