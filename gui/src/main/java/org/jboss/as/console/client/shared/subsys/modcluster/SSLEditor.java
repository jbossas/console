package org.jboss.as.console.client.shared.subsys.modcluster;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.modcluster.model.SSLConfig;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/28/12
 */
public class SSLEditor {

    private ModclusterPresenter presenter;
    private Form<SSLConfig> sslForm;

    public SSLEditor(ModclusterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        sslForm = new Form<SSLConfig>(SSLConfig.class);

        TextBoxItem alias = new TextBoxItem("keyAlias", "Key Alias", false);
        TextBoxItem password = new TextBoxItem("password", "Password", false);
        TextBoxItem certFile = new TextBoxItem("certFile", "Cert File", false);
        TextBoxItem keyFile = new TextBoxItem("keyFile", "Key File", false);
        TextBoxItem cipherSuite = new TextBoxItem("cipherSuite", "Cipher Suite", false);
        TextBoxItem revokeUrl = new TextBoxItem("revocationUrl", "Revocation URL", false);
        TextBoxItem protocol = new TextBoxItem("protocol", "Protocol", false);

        sslForm.setFields(alias, password, certFile, keyFile, cipherSuite, revokeUrl, protocol);
        sslForm.setNumColumns(2);


        FormHelpPanel sslHelp = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "modcluster");
                address.add("mod-cluster-config", "configuration");
                address.add("ssl", "configuration");
                return address;
            }
        }, sslForm);

        FormToolStrip<SSLConfig> sslTools = new FormToolStrip<SSLConfig>(
                sslForm, new FormToolStrip.FormCallback<SSLConfig>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveSsl(sslForm.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(SSLConfig entity) {

            }
        });


        FormLayout sslPanel = new FormLayout()
                .setForm(sslForm)
                .setHelp(sslHelp)
                .setSetTools(sslTools);


        sslForm.setEnabled(false);

        return sslPanel.build();
    }

    public void edit(SSLConfig sslConfig) {
        sslForm.edit(sslConfig);
    }


}
