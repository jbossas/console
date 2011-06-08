package org.jboss.as.console.client.shared.help;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.dmr.client.ModelNode;

/**
 * Displays attribute descriptions for form items.
 *
 * @author Heiko Braun
 * @date 6/8/11
 */
public class FormHelpPanel {

    private DisclosurePanel helpPanel;
    private AddressCallback address;
    private Form form;
    private boolean hasBeenBuild;

    public FormHelpPanel(AddressCallback address, Form form) {
        this.address = address;
        this.form = form;
    }

    public Widget asWidget()
    {
        ImageResource helpIcon = Icons.INSTANCE.help();
        helpPanel = new DisclosurePanel(helpIcon, helpIcon, "");
        helpPanel.addStyleName("help-panel");
        helpPanel.getHeader().getElement().setAttribute("style", "float:right");
        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> disclosurePanelOpenEvent) {
                helpPanel.addStyleName("help-panel-open");
                buildAttributeHelp();
            }
        });

        helpPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> disclosurePanelCloseEvent) {
                helpPanel.removeStyleName("help-panel-open");
            }
        });

        return helpPanel;

    }

    private void buildAttributeHelp() {

        if(!hasBeenBuild)
        {
            Console.MODULES.getHelpSystem().getAttributeDescriptions(
                    address.getAddress(), form, new AsyncCallback<Widget>() {
                @Override
                public void onSuccess(Widget result) {
                    helpPanel.add(result);
                    hasBeenBuild = true;
                }

                @Override
                public void onFailure(Throwable caught) {
                    Console.error("Failed to retrieve attribute description", caught.getMessage());
                    helpPanel.add(new HTML("<ul><li>Failed to retrieve attribute descriptions.</li></ul>"));
                    hasBeenBuild = true;
                }
            });
        }
    }

    public interface AddressCallback
    {
        ModelNode getAddress();
    }
}
