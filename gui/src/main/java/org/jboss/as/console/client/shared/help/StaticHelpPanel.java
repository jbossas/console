package org.jboss.as.console.client.shared.help;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.nav.AriaLink;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.ballroom.client.widgets.icons.Icons;

/**
 * Displays static help descriptions.
 *
 * @author Heiko Braun
 * @date 6/8/11
 */
public class StaticHelpPanel {

    private DisclosurePanel helpPanel;
    private SafeHtml helpText;

    public StaticHelpPanel(SafeHtml helpText) {
        this.helpText = helpText;
    }

    public StaticHelpPanel(String helpText) {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<div class='help-attribute-descriptions' style='padding-left:10px;'>");
        builder.appendHtmlConstant(helpText);
        builder.appendHtmlConstant("</div>");
        this.helpText = builder.toSafeHtml();
    }

    public Widget asWidget()
    {
        /*ImageResource helpIcon = Icons.INSTANCE.help();
        helpPanel = new DisclosurePanel(helpIcon, helpIcon, "");  */

        AriaLink header = new AriaLink (Console.CONSTANTS.help_need_help());
        header.addStyleName("help-panel-header");
        header.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                helpPanel.setOpen(!helpPanel.isOpen());
            }
        });

        helpPanel = new DisclosurePanel(header);

        helpPanel.addStyleName( "help-panel");

        final String popupStyle = "help-panel-open";

        helpPanel.add(new HTML(helpText));
        helpPanel.addStyleName("help-panel");
        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                event.getTarget().addStyleName("help-panel-open");
                helpPanel.getHeaderTextAccessor().setText(Console.CONSTANTS.help_close_help());
            }
        });

        helpPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                event.getTarget().removeStyleName("help-panel-open");
                helpPanel.getHeaderTextAccessor().setText(Console.CONSTANTS.help_need_help());
            }
        });

        return helpPanel;

    }
}
