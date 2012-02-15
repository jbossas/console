package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class MailSubsystemView extends DisposableViewImpl implements MailPresenter.MyView{

    private MailPresenter presenter;
    private PagedView panel;
    private MailSessionEditor sessionEditor;
    private List<MailSession> sessions;
    private ServerConfigView serverConfigEditor;

    @Override
    public Widget createWidget() {


        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Mail");
        layout.add(titleBar);

        panel = new PagedView();

        sessionEditor = new MailSessionEditor(presenter);
        serverConfigEditor = new ServerConfigView(
                Console.MESSAGES.available("Mail Server"),
                Console.CONSTANTS.subsys_mail_server_desc(),
                presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), sessionEditor.asWidget());
        panel.addPage("Mail Server", serverConfigEditor.asWidget());
        //panel.addPage("JMS Destinations", jmsEditor.asWidget()) ;

        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    @Override
    public void setPresenter(MailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelectedSession(String selectedSession) {


        if(null==selectedSession)
        {
            panel.showPage(0);
        }
        else{

            for(MailSession session : sessions)
            {
                if(session.getJndiName().equals(selectedSession))
                {

                    // update subpages
                    serverConfigEditor.setServerConfig(session);
                    break;
                }
            }

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }

    @Override
    public void updateFrom(List<MailSession> list) {

        this.sessions = list;
        sessionEditor.updateFrom(list);
    }
}
