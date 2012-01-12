package org.jboss.as.console.client.shared.general.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.InterfaceManagement;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.validation.ValidationResult;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/24/11
 */
public class NewInterfaceWizard {

    private InterfaceManagement presenter;
    private HTML errorMessages = new HTML();

    public NewInterfaceWizard(InterfaceManagement presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Interface> form = new Form(Interface.class);
        errorMessages.setStyleName("error-panel");

        TextBoxItem nameItem = new TextBoxItem("name", "Name");

        TextBoxItem inetAddress = new TextBoxItem("inetAddress", "Inet Address", false);

        final ComboBoxItem anyAddress = new ComboBoxItem("addressWildcard", "Address Wildcard") {
            {
                isRequired = false;
            }
        };

        anyAddress.setDefaultToFirstOption(true);
        anyAddress.setValueMap(new String[]{"", Interface.ANY_ADDRESS, Interface.ANY_IP4, Interface.ANY_IP6});

        form.setFields(nameItem, inetAddress, anyAddress);

        DialogueOptions options = new DialogueOptions(

                // save
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            Interface entity = form.getUpdatedEntity();

                            // otherwise the validation rejects it as unmodified
                            Map<String,Object> changedValues = form.getChangedValues();
                            changedValues.put("name", entity.getName());
                            changedValues.put("inetAddress", entity.getInetAddress());
                            changedValues.put("addressWildcard", entity.getAddressWildcard());

                            errorMessages.setHTML("");

                            ValidationResult result = presenter.validateInterfaceConstraints(entity, changedValues);

                            if(result.isValid())
                            {
                                presenter.createNewInterface(entity);
                            }
                            else
                            {
                                SafeHtmlBuilder html = new SafeHtmlBuilder();
                                int i=0;
                                for(String detail : result.getMessages())
                                {
                                    if(i==0) html.appendHtmlConstant("<b>");
                                    html.appendEscaped(detail).appendHtmlConstant("<br/>");
                                    if(i==0) html.appendHtmlConstant("</b>");

                                    i++;
                                }

                                errorMessages.setHTML(html.toSafeHtml());
                            }
                        }
                    }
                },

                // cancel
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialoge();
                    }
                }

        );

        // ----------------------------------------

        Widget formWidget = form.asWidget();

        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("interface", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(formWidget);

        layout.add(errorMessages);

        return new WindowContentBuilder(layout, options).build();
    }
}
