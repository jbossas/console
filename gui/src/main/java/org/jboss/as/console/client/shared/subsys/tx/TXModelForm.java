package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 8/2/12
 */
public class TXModelForm {

    private Form<TransactionManager> form;

    private TransactionPresenter presenter;
    private FormItem[] fields;

    public TXModelForm(TransactionPresenter presenter, FormItem... fields) {
        this.presenter = presenter;
        this.fields = fields;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout");


        form = new Form<TransactionManager>(TransactionManager.class);
        form.setNumColumns(2);

        FormToolStrip<TransactionManager> toolstrip =
                new FormToolStrip<TransactionManager>(form, new FormToolStrip.FormCallback<TransactionManager>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveConfig(changeset);
                    }

                    @Override
                    public void onDelete(TransactionManager entity) {

                    }
                });
        toolstrip.providesDeleteOp(false);

        Widget toolstripWidget = toolstrip.asWidget();
        layout.add(toolstripWidget);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "transactions");
                return address;
            }
        }, form);

        layout.add(helpPanel.asWidget());

        form.setFields(fields);
        form.setEnabled(false);

        layout.add(form.asWidget());

        return layout;
    }

    public void edit(TransactionManager tm) {
        form.edit(tm);
    }

    public void clearValues() {
        form.clearValues();
    }
}
