package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;
import org.jboss.as.console.client.shared.subsys.messaging.JMSEditor;
import org.jboss.as.console.client.shared.subsys.messaging.MessagingProviderEditor;
import org.jboss.as.console.client.shared.subsys.messaging.ProviderList;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

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

    @Override
    public Widget createWidget() {


        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Mail Sessions");
        layout.add(titleBar);

        panel = new PagedView();

        sessionEditor = new MailSessionEditor(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), sessionEditor.asWidget());
        //panel.addPage("Provider Config", providerEditor.asWidget());
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
