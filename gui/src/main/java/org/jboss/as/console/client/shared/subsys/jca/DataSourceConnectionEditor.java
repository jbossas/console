package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
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
public class DataSourceConnectionEditor extends FormEditor<DataSource>{

    private boolean xaDisplay;

    public DataSourceConnectionEditor(FormToolStrip.FormCallback<DataSource> callback) {

        super(DataSource.class);

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("data-source", "*");

        setCallback(callback);
        setHelpAddress(helpAddress);
    }

    public void setXADisplay(boolean b) {
        this.xaDisplay = b;
    }

    @Override
    public Widget asWidget() {


        TextBoxItem connectionSql= new TextBoxItem("connectionSql", "New Connection Sql") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        TextBoxItem urlItem = new TextBoxItem("connectionUrl", "Connection URL");
        CheckBoxItem jtaItem = new CheckBoxItem("jta", "Use JTA?");
        CheckBoxItem ccmItem = new CheckBoxItem("ccm", "Use CCM?");

        ComboBoxItem tx = new ComboBoxItem("transactionIsolation", "Transaction Isolation");
        tx.setValueMap(new String[]{
                "TRANSACTION_NONE",
                "TRANSACTION_READ_UNCOMMITTED",
                "TRANSACTION_READ_COMMITTED",
                "TRANSACTION_REPEATABLE_READ",
                "TRANSACTION_SERIALIZABLE"
        }
        );

        if(!xaDisplay)
            getForm().setFields(urlItem, tx, connectionSql, BlankItem.INSTANCE, jtaItem, ccmItem);
        else
            getForm().setFields(tx, connectionSql);

        return super.asWidget();
    }
}
