package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.ViewName;
import org.jboss.as.console.client.components.sgwt.LHSNavigation;
import org.jboss.as.console.client.components.sgwt.NavigationItem;
import org.jboss.as.console.client.components.sgwt.NavigationSection;
import org.jboss.as.console.client.util.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Server management default view implementation.
 * Works on a LHS navigation and a all purpose content panel on the right.
 *
 * <p/>
 * CSS:
 * <ul>
 * <li> 'lhs-navigation-panel', used for the top most horizontal panel
 * </ul>
 * @see LHSNavigation
 *
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtApplicationViewImpl extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView{

    private HLayout layout;
    private LHSNavigation lhsNavigation;
    private Canvas contentCanvas;

    public ServerMgmtApplicationViewImpl() {
        super();

        layout = new HLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setStyleName("lhs-navigation-panel");

        lhsNavigation = new LHSNavigation("server", getNavigationSections());

        contentCanvas = new Canvas();
        contentCanvas.setWidth100();
        contentCanvas.setHeight100();
        contentCanvas.setMargin(0);

        layout.addMember(lhsNavigation.asWidget());
        layout.addMember(contentCanvas);

    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_MainContent) {
            if(content!=null)
                setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    private void setContent(Widget newContent) {

        Canvas[] children = contentCanvas.getChildren();
        if(children.length>0) {
            contentCanvas.removeChild(children[0]);
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
    }

    private List<NavigationSection> getNavigationSections()
    {
        final ArrayList<NavigationSection> sections = new ArrayList<NavigationSection>();


        final NavigationSection subsystems= new NavigationSection(
                new ViewName("config", "Profile"),
                new NavigationItem(new ViewName("threads","Threads"), "",null),
                new NavigationItem(new ViewName("threads","Web"), "",null),
                new NavigationItem(new ViewName("threads","EJB"), "",null),
                new NavigationItem(new ViewName("threads","JCA"), "",null),
                new NavigationItem(new ViewName("threads","Messaging"), "",null),
                new NavigationItem(new ViewName("threads","Transactions"), "",null),
                new NavigationItem(new ViewName("threads","Web Services"), "",null),
                new NavigationItem(new ViewName("threads","Clustering"), "",null)


        );

        final NavigationSection deployments = new NavigationSection(
                new ViewName("deployments", "Deployments"),
                new NavigationItem(new ViewName("deployments","Web Applications"), "", null),
                new NavigationItem(new ViewName("deployments","Enterprise Applications"), "", null),
                new NavigationItem(new ViewName("deployments","Resource Adapters"), "", null),
                new NavigationItem(new ViewName("deployments","Other"), "", null)
        );

        final NavigationSection serverConfig= new NavigationSection(
                new ViewName("common", "General Configuration"),
                new NavigationItem(new ViewName("path","Path"), "",null),
                new NavigationItem(new ViewName("interfaces","Interfaces"), "",null),
                new NavigationItem(new ViewName("sockets","Socket Binding Groups"), "",null),
                new NavigationItem(new ViewName("properties","System Properties"), "",null)
        );

        sections.add(subsystems);
        sections.add(deployments);
        sections.add(serverConfig);
        return sections;
    };
}
