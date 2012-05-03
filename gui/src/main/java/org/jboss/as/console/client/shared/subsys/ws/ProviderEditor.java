package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceProvider;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 1/11/12
 */
public class ProviderEditor {

    private Form<WebServiceProvider> providerForm;

    private WebServicePresenter presenter;

    public ProviderEditor(WebServicePresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        providerForm = new Form<WebServiceProvider>(WebServiceProvider.class);
        providerForm .setNumColumns(2);

        FormToolStrip<WebServiceProvider> formToolStrip = new FormToolStrip<WebServiceProvider>(
                providerForm,
                new FormToolStrip.FormCallback<WebServiceProvider>(){
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveProvider(changeset);
                    }

                    @Override
                    public void onDelete(WebServiceProvider entity) {

                    }
                });
        formToolStrip.providesDeleteOp(false);


        CheckBoxItem modify = new CheckBoxItem("modifyAddress", "Modify SOAP Address");
        TextBoxItem wsdlHost = new TextBoxItem("wsdlHost", "WSDL Host", true);
        NumberBoxItem wsdlPort = new NumberBoxItem("wsdlPort", "WSDL Port", false) {
            {
                isRequired=false;
            }
        };
        NumberBoxItem wsdlSecurePort = new NumberBoxItem("wsdlSecurePort", "WSDL Secure Port", false) {
            {
                isRequired=false;
            }
        };

        providerForm.setFields(modify, wsdlHost, wsdlPort, wsdlSecurePort);
        providerForm.setEnabled(false);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback(){
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                        address.add("subsystem", "webservices");
                        return address;
            }
        }, providerForm);

        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle("Provider")
                .setHeadline("Web Services Provider")
                .setDescription(Console.CONSTANTS.subsys_ws_desc())
                .addContent("tools", formToolStrip.asWidget())
                .addContent("help", helpPanel.asWidget())
                .addContent("form", providerForm.asWidget());

        return layout.build();

    }

    public void setProvider(WebServiceProvider provider)
    {
        providerForm.edit(provider);
    }
}
