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
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.dmr.client.ModelNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Displays descriptions for metric items.
 *
 * @author Heiko Braun
 * @date 6/8/11
 */
public class MetricHelpPanel {

    private DisclosurePanel helpPanel;
    private AddressBinding address;
    private boolean hasBeenBuild;
    private Column[] columns;

    public MetricHelpPanel(String address, Column[] columns) {
        this.address = parseAddressDeclaration(address);
        this.columns = columns;
    }

    public Widget asWidget()
    {
        ImageResource helpIcon = Icons.INSTANCE.help();
        helpPanel = new DisclosurePanel(helpIcon, helpIcon, "");

        helpPanel.addStyleName("help-panel");
        helpPanel.getHeader().getElement().setAttribute("style", "float:right");
        helpPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                event.getTarget().addStyleName("help-panel-open");
                buildAttributeHelp();
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

    private void buildAttributeHelp() {

        if(!hasBeenBuild)
        {

            Console.MODULES.getHelpSystem().getMetricDescriptions(
                    address, columns, new AsyncCallback<HTML>() {
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

    public interface AddressCallback
    {
        ModelNode getAddress();
    }

    private static AddressBinding parseAddressDeclaration(String tokens) {
        List<String[]> address = parseAddressString(tokens);

        AddressBinding addressBinding = new AddressBinding();

        for(String[] tuple : address)
        {
            System.out.println(tuple[0]+"="+tuple[1]);
            addressBinding.add(tuple[0], tuple[1]);
        }

        return addressBinding;
    }

    private static List<String[]> parseAddressString(String value) {
        List<String[]> address = new LinkedList<String[]>();

        if(value.equals("/")) // default parent value
            return address;

        String[] split = value.split("/");

        for(int i=0; i<split.length;i++)
        {
            String nextToken = split[i];
            if(!nextToken.isEmpty())
                address.add(nextToken.split("="));
        }
        return address;
    }
}
