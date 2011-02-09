package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.*;
import org.jboss.as.console.client.util.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtViewImpl extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView{

    AbstractToolsetView delegate;
    Widget delegateWidget = null;

    public ServerMgmtViewImpl() {
        super();
        this.delegate = createToolset();
    }

    @Override
    public Widget asWidget() {
        if(null==delegateWidget)
            delegateWidget = delegate.asWidget();
        return delegateWidget;
    }


    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_SetToolContent) {
            if(content!=null)
                delegate.setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    private static AbstractToolsetView createToolset() {

        AbstractToolsetView toolset = new AbstractToolsetView("config") {
            @Override
            protected String defaultView()
            {
                return "server/subsystems";
            }

            @Override
            protected List<NavigationSection> getNavigationSections()
            {
                final ArrayList<NavigationSection> sections = new ArrayList<NavigationSection>();

                final NavigationSection serverConfig= new NavigationSection(
                        new ViewName("server", "Configuration"),
                        new NavigationItem(new ViewName("server/path","Path"), "",null),
                        /*
                        new NavigationItem(new ViewName("subsystems","Subsystems"), "", new ViewFactory()
                        {
                            @Override
                            public Widget createView() {
                                return injector.getSubsystemTool().asWidget();
                            }
                        }),*/
                        new NavigationItem(new ViewName("server/interfaces","Interfaces"), "",null),
                        new NavigationItem(new ViewName("server/sockets","Sockets"), "",null),
                        new NavigationItem(new ViewName("server/properties","Properties"), "",null)
                );

                 final NavigationSection subsystems= new NavigationSection(
                        new ViewName("server", "Subsystems"),
                        new NavigationItem(new ViewName("server/subsys/threads","Threading"), "",null)
                );

                final NavigationSection deployments = new NavigationSection(
                        new ViewName("server", "Deployments"),
                        new NavigationItem(new ViewName("server/deployments","Deployments"), "", null)
                );

                sections.add(serverConfig);
                sections.add(subsystems);
                sections.add(deployments);
                return sections;
            };
        };

        return toolset;
    }
}
