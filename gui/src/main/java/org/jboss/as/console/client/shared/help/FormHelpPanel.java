package org.jboss.as.console.client.shared.help;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import org.jboss.as.console.client.widgets.nav.AriaLink;
import org.jboss.ballroom.client.widgets.InlineLink;
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
        //ImageResource helpIcon = Icons.INSTANCE.noIcon();
        //helpPanel = new DisclosurePanel(helpIcon, helpIcon, "[help]");

        AriaLink header = new AriaLink (Console.CONSTANTS.help_need_help());
        header.addStyleName("help-panel-header");
        header.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                helpPanel.setOpen(!helpPanel.isOpen());
            }
        });

        helpPanel = new DisclosurePanel(header);

        helpPanel.addStyleName( isAligned ? "help-panel-aligned" : "help-panel");

        final String popupStyle = isAligned ? "help-panel-aligned-open" : "help-panel-open";

        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                event.getTarget().addStyleName(popupStyle);
                helpPanel.getHeaderTextAccessor().setText(Console.CONSTANTS.help_close_help());
                buildAttributeHelp();
            }
        });

        helpPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
            @Override
            public void onClose(CloseEvent<DisclosurePanel> event) {
                helpPanel.getHeaderTextAccessor().setText(Console.CONSTANTS.help_need_help());
                event.getTarget().removeStyleName(popupStyle);
            }
        });


        return helpPanel;

    }

    private void buildAttributeHelp() {

        if(!hasBeenBuild)
        {
            Console.getHelpSystem().getAttributeDescriptions(
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
