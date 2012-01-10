package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormEditor;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class XADataSourceConnection extends FormEditor<XADataSource>{


    public XADataSourceConnection(FormToolStrip.FormCallback<XADataSource> callback) {

        super(XADataSource.class);

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("xa-data-source", "*");

        setCallback(callback);
        setHelpAddress(helpAddress);
    }

    @Override
    public Widget asWidget() {


        TextBoxItem connectionSql= new TextBoxItem("connectionSql", "New Connection Sql") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        /*TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");
        CheckBoxItem jtaItem = new CheckBoxItem("jta", "Use JTA?");
        CheckBoxItem ccmItem = new CheckBoxItem("ccm", "Use CCM?");*/

        ComboBoxItem tx = new ComboBoxItem("transactionIsolation", "Transaction Isolation");
        tx.setValueMap(new String[]{
                "TRANSACTION_NONE",
                "TRANSACTION_READ_UNCOMMITTED",
                "TRANSACTION_READ_COMMITTED",
                "TRANSACTION_REPEATABLE_READ",
                "TRANSACTION_SERIALIZABLE"
        }
        );

        CheckBoxItem rmOverride = new CheckBoxItem("enableRMOverride", "Same RM Override");
        CheckBoxItem interleave = new CheckBoxItem("enableInterleave", "Interleave");
        CheckBoxItem padXid = new CheckBoxItem("padXid", "Pad XID");
        CheckBoxItem wrap = new CheckBoxItem("wrapXaResource", "Wrap XA");

        getForm().setFields(tx, rmOverride, interleave, padXid, wrap, BlankItem.INSTANCE, connectionSql);

        return super.asWidget();
    }
}
