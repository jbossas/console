package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 12/13/11
 */
public class DataSourceConnectionEditor {

    private DataSourcePresenter presenter;
    private FormToolStrip.FormCallback<DataSource> callback;
    private ModelNode helpAddress;
    private Form<DataSource> form;

    public DataSourceConnectionEditor(DataSourcePresenter presenter , FormToolStrip.FormCallback<DataSource> callback) {

        this.presenter = presenter;

        ModelNode helpAddress = Baseadress.get();
        helpAddress.add("subsystem", "datasources");
        helpAddress.add("data-source", "*");

        this.callback = callback;
        this.helpAddress = helpAddress;
        this.form = new Form<DataSource>(DataSource.class);
    }

    public Widget asWidget() {


        TextAreaItem connectionSql= new TextAreaItem("connectionSql", "New Connection Sql") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        TextAreaItem urlItem = new TextAreaItem("connectionUrl", "Connection URL");
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



        form.setNumColumns(2);

        form.setFields(
                urlItem,
                connectionSql,
                tx, BlankItem.INSTANCE, jtaItem, ccmItem);

        form.setEnabled(false);

        ToolButton verifyBtn = new ToolButton(Console.CONSTANTS.subsys_jca_dataSource_verify(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.verifyConnection(form.getEditedEntity().getName(), false);
            }
        });

        verifyBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_verify_dataSourceDetails());

        FormToolStrip<DataSource> formTools = new FormToolStrip<DataSource>(form,callback);
        formTools.providesDeleteOp(false);

        // standalone only
        if(Console.getBootstrapContext().isStandalone())
            formTools.addToolButtonRight(verifyBtn);

        FormLayout layout = new FormLayout()
                .setHelp(new FormHelpPanel(new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        return helpAddress;
                    }
                }, form)
                )
                .setForm(form)
                .setSetTools(formTools);

        return layout.build();
    }

    public Form<DataSource> getForm() {
        return form;
    }
}
