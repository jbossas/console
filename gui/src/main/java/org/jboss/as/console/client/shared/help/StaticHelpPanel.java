package org.jboss.as.console.client.shared.help;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.icons.Icons;

/**
 * Displays static help descriptions.
 *
 * @author Heiko Braun
 * @date 6/8/11
 */
public class StaticHelpPanel {

    private DisclosurePanel helpPanel;
    private HTML helpText;

    public StaticHelpPanel(HTML helpText) {
        this.helpText = helpText;
    }

    public StaticHelpPanel(String helpText) {
        this.helpText = new HTML("<ul class='help-attribute-descriptions'><li>"+helpText+"</li></ul>");
    }

    public Widget asWidget()
    {
        ImageResource helpIcon = Icons.INSTANCE.help();
        helpPanel = new DisclosurePanel(helpIcon, helpIcon, "");
        helpPanel.add(helpText);
        helpPanel.addStyleName("help-panel");
        helpPanel.getHeader().getElement().setAttribute("style", "float:right");
        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                event.getTarget().addStyleName("help-panel-open");
            }
        });

        helpPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                event.getTarget().removeStyleName("help-panel-open");
            }
        });

        return helpPanel;

    }
}
