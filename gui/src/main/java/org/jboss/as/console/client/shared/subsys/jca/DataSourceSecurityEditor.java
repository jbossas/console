package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.forms.FormEditor;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class DataSourceSecurityEditor extends FormEditor<DataSource>{

    public DataSourceSecurityEditor(FormToolStrip.FormCallback<DataSource> callback) {

        super(DataSource.class);

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("data-source", "*");

        setCallback(callback);
        setHelpAddress(helpAddress);
    }

    @Override
    public Widget asWidget() {

        TextBoxItem securityDomain = new TextBoxItem("securityDomain", "Security Domain") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem user = new TextBoxItem("username", "Username") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem pass = new TextBoxItem("password", "Password") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        getForm().setFields(user, pass, securityDomain);

        return super.asWidget();
    }
}
