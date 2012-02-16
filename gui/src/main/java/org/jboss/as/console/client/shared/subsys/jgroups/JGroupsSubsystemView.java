package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class JGroupsSubsystemView extends SuspendableViewImpl implements JGroupsPresenter.MyView {


    private JGroupsPresenter presenter;
    private PagedView panel;
    private StackEditor stackEditor;
    private StackOverview stackOverview;
    private List<JGroupsStack> stacks;
    private TransportEditor transportEditor;

    @Override
    public void setPresenter(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("JGroups");
        layout.add(titleBar);

        panel = new PagedView();

        stackOverview = new StackOverview(presenter);
        stackEditor = new StackEditor(presenter);
        transportEditor = new TransportEditor(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), stackOverview.asWidget());
        panel.addPage("Protocols", stackEditor.asWidget());
        panel.addPage("Transport", transportEditor.asWidget());

        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void updateStacks(List<JGroupsStack> stacks) {
        this.stacks = stacks;
        stackOverview.updateStacks(stacks);
    }

    @Override
    public void setSelectedStack(String selectedStack) {

        if(null==selectedStack)
        {
            panel.showPage(0);
        }
        else{

            for(JGroupsStack stack : stacks)
            {
                if(stack.getName().equals(selectedStack))
                {

                    // update subpages
                    stackEditor.setStack(stack);
                    transportEditor.setStack(stack);
                    break;
                }
            }

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }
}
