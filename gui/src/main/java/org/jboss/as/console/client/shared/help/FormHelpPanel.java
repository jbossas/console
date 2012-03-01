package org.jboss.as.console.client.shared.help;

import com.allen_sauer.gwt.log.client.Log;
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
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.icons.Icons;
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
    private FormAdapter form;
    private boolean hasBeenBuild;
    private boolean isAligned;

    public FormHelpPanel(AddressCallback address, FormAdapter form) {
        this.address = address;
        this.form = form;
    }

    public void setAligned(boolean b)
    {
        this.isAligned = b;
    }

    public Widget asWidget()
    {
        ImageResource helpIcon = Icons.INSTANCE.help();
        helpPanel = new DisclosurePanel(helpIcon, helpIcon, "");

        helpPanel.addStyleName( isAligned ? "help-panel-aligned" : "help-panel");
        helpPanel.getHeader().getElement().setAttribute("style", "float:right");

        final String popupStyle = isAligned ? "help-panel-aligned-open" : "help-panel-open";

        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                event.getTarget().addStyleName(popupStyle);
                buildAttributeHelp();
            }
        });

        helpPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                event.getTarget().removeStyleName(popupStyle);
            }
        });


        return helpPanel;

    }

    private void buildAttributeHelp() {

        if(!hasBeenBuild)
        {
            Console.MODULES.getHelpSystem().getAttributeDescriptions(
                    address.getAddress(), form, new AsyncCallback<HTML>() {
                @Override
                public void onSuccess(HTML result) {
                    helpPanel.clear();
                    helpPanel.add(result);
                    hasBeenBuild = true;
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Failed to retrieve attribute description", caught.getMessage());
                    helpPanel.clear();
                    helpPanel.add(new HTML("<ul><li>Failed to retrieve attribute descriptions.</li></ul>"));
                }
            });
        }
    }

    public interface AddressCallback
    {
        ModelNode getAddress();
    }
}
