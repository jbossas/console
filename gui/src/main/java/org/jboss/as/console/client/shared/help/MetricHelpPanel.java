package org.jboss.as.console.client.shared.help;

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
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.ballroom.client.widgets.InlineLink;
import org.jboss.ballroom.client.widgets.icons.Icons;

/**
 * Displays descriptions for metric items.
 *
 * @author Heiko Braun
 * @date 6/8/11
 */
public class MetricHelpPanel {

    private DisclosurePanel helpPanel;
    private boolean hasBeenBuild;
    private Column[] columns;
    private boolean isAligned = false;
    private HelpSystem.AddressCallback addressCallback;

    public MetricHelpPanel(HelpSystem.AddressCallback address, Column[] columns) {
        this.addressCallback = address;
        this.columns = columns;
    }

    public void setAligned(boolean b)
    {
        this.isAligned = b;
    }

    public Widget asWidget()
    {
        InlineLink header = new InlineLink (Console.CONSTANTS.help_need_help());
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
                event.getTarget().removeStyleName(popupStyle);
                helpPanel.getHeaderTextAccessor().setText(Console.CONSTANTS.help_need_help());
            }
        });


        return helpPanel;

    }

    private void buildAttributeHelp() {

        if(!hasBeenBuild)
        {

            Console.MODULES.getHelpSystem().getMetricDescriptions(
                    addressCallback, columns, new AsyncCallback<HTML>() {
                @Override
                public void onSuccess(HTML result) {
                    helpPanel.clear();
                    helpPanel.add(result);

                    /*result.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            helpPanel.setOpen(!helpPanel.isOpen());
                        }
                    }); */
                    hasBeenBuild = true;
                }

                @Override
                public void onFailure(Throwable caught) {
                    //Console.error("Failed to retrieve attribute description", caught.getMessage());
                    helpPanel.clear();
                    helpPanel.add(new HTML("<ul><li>Failed to retrieve metric descriptions.</li></ul>"));
                }
            });
        }
    }
}
