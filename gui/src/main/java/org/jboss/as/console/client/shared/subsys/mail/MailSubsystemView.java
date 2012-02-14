package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.apache.catalina.Server;
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
    private ServerConfigView smtpServerEditor;

    @Override
    public Widget createWidget() {


        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Mail Sessions");
        layout.add(titleBar);

        panel = new PagedView();

        sessionEditor = new MailSessionEditor(presenter);
        smtpServerEditor = new ServerConfigView(
                Console.MESSAGES.available("Mail Server"),
                "Mail server definitions",
                new FormToolStrip.FormCallback<MailServerDefinition>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveServer(ServerType.smtp, changeset);
                    }

                    @Override
                    public void onDelete(MailServerDefinition entity) {
                        presenter.onRemoveServer(ServerType.smtp, entity);
                    }
                }, presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), sessionEditor.asWidget());
        panel.addPage("Mail Server Configurations", smtpServerEditor.asWidget());
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
                    smtpServerEditor.setServerConfig(selectedSession, session.getSmtpServer());
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
