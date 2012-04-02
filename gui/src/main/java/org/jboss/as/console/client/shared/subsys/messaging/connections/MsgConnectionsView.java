package org.jboss.as.console.client.shared.subsys.messaging.connections;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

/**
 * @author Heiko Braun
 * @date 4/2/12
 */
public class MsgConnectionsView extends SuspendableViewImpl implements MsgConnectionsPresenter.MyView {

    private PagedView panel;
    private MsgConnectionsPresenter presenter;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new LayoutPanel();


        FakeTabPanel titleBar = new FakeTabPanel("Messaging Destinations");
        layout.add(titleBar);


        panel = new PagedView();

        panel.addPage(Console.CONSTANTS.common_label_back(), new HTML());
        /*panel.addPage("Provider Config", providerEditor.asWidget());
     panel.addPage("JMS Destinations", jmsEditor.asWidget()) ;*/

        // default page
        panel.showPage(0);


        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setPresenter(MsgConnectionsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelectedProvider(String selectedProvider) {


        if(null==selectedProvider)
        {
            panel.showPage(0);
        }
        else{

            //presenter.loadDetails(selectedProvider);

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }

}
