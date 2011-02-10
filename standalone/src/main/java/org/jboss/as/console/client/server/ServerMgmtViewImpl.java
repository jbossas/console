package org.jboss.as.console.client.server;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.components.sgwt.NavigationItem;
import org.jboss.as.console.client.components.sgwt.NavigationSection;
import org.jboss.as.console.client.components.ViewName;
import org.jboss.as.console.client.components.sgwt.LHSNavigation;
import org.jboss.as.console.client.util.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/4/11
 */
public class ServerMgmtViewImpl extends ViewImpl
        implements ServerMgmtApplicationPresenter.ServerManagementView{

    private HLayout layout;
    private LHSNavigation lhsNavigation;
    private Canvas contentCanvas;

    public ServerMgmtViewImpl() {
        super();

        layout = new HLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setStyleName("lhs-navigation-panel");

        lhsNavigation = new LHSNavigation("server", getNavigationSections());

        contentCanvas = new Canvas();
        contentCanvas.setWidth100();
        contentCanvas.setHeight100();

        layout.addMember(lhsNavigation.asWidget());
        layout.addMember(contentCanvas);
    }

    @Override
    public Widget asWidget() {
        return layout;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerMgmtApplicationPresenter.TYPE_SetToolContent) {
            if(content!=null)
                setContent(content);

        } else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    public void setContent(Widget newContent) {
        Canvas[] children;
        while ((children = contentCanvas.getChildren()).length > 0) {
            children[0].destroy();
        }

        contentCanvas.addChild(newContent);
        contentCanvas.markForRedraw();
    }

    private List<NavigationSection> getNavigationSections()
    {
        final ArrayList<NavigationSection> sections = new ArrayList<NavigationSection>();

        final NavigationSection serverConfig= new NavigationSection(
                new ViewName("common", "Common Settings"),
                new NavigationItem(new ViewName("path","Path"), "",null),
                new NavigationItem(new ViewName("interfaces","Interfaces"), "",null),
                new NavigationItem(new ViewName("sockets","Socket Binding Groups"), "",null),
                new NavigationItem(new ViewName("properties","System Properties"), "",null)
        );

        final NavigationSection subsystems= new NavigationSection(
                new ViewName("config", "Subsystem Configuration"),
                new NavigationItem(new ViewName("threads","Threading"), "",null)
        );

        final NavigationSection deployments = new NavigationSection(
                new ViewName("deployments", "Deployments"),
                new NavigationItem(new ViewName("deployments","Deployments"), "", null)
        );

        sections.add(serverConfig);
        sections.add(subsystems);
        sections.add(deployments);
        return sections;
    };
}
