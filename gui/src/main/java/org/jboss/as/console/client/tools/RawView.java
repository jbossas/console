package org.jboss.as.console.client.tools;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class RawView {


    private HTML dump;
    private BrowserPresenter presenter;
    private ModelNode currentAddress;

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:10px");

        dump = new HTML("");

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(new ToolButton("Refresh", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(currentAddress!=null)
                {
                    dump.setHTML("");
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            presenter.readResource(currentAddress);
                        }
                    });
                }
            }
        }));

        layout.add(tools.asWidget());

        SafeHtmlBuilder helpText = new SafeHtmlBuilder();
        helpText.appendHtmlConstant("<ul>");
        helpText.appendHtmlConstant("<li>");
        helpText.appendEscaped("This is a raw dump of the current configuration values on this node. It only resolves values on the current level, hence some attributes (or children) may show up as UNDEFINED.");
        helpText.appendHtmlConstant("</ul>");
        StaticHelpPanel help = new StaticHelpPanel(helpText.toSafeHtml());
        layout.add(help.asWidget());

        layout.add(dump);

        return layout;
    }

    public void display(ModelNode address, Property model)
    {
        currentAddress = address;
        SafeHtmlBuilder parsed = parse(model.getValue().toString());
        dump.setHTML(parsed.toSafeHtml());
    }

    private static SafeHtmlBuilder parse(String s) {

        SafeHtmlBuilder html = new SafeHtmlBuilder();

        html.appendHtmlConstant("<pre class='model-dump'>");

        String[] lines = s.split("\n");
        for(String line : lines)
        {
            html.appendHtmlConstant("<span class='browser-dump-line'>");
            html.appendEscaped(line).appendHtmlConstant("<br/>");
            html.appendHtmlConstant("</span>");
        }

        html.appendHtmlConstant("</pre>");
        return html;
    }

    public void clearDisplay()
    {
        currentAddress = null;
        dump.setHTML("");
    }

    public void setPresenter(BrowserPresenter presenter) {
        this.presenter = presenter;
    }
}
