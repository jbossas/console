package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TransactionView extends SuspendableViewImpl implements TransactionPresenter.MyView{

    private TransactionPresenter presenter = null;
    private Form<TransactionManager> form ;

    @Override
    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Transactions");
        layout.add(titleBar);

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

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(toolstripWidget, 40, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 70, Style.Unit.PX, 100, Style.Unit.PCT);

        panel.add(new ContentHeaderLabel("Transaction Manager Configuration"));

        // -----

        panel.add(new ContentGroupLabel("Attributes"));


        NumberBoxItem defaultTimeout = new NumberBoxItem("defaultTimeout", "Default Timeout");
        CheckBoxItem enableStatistics = new CheckBoxItem("enableStatistics", "Enable Statistics");
        CheckBoxItem enableTsm = new CheckBoxItem("enableTsmStatus", "Enable TSM Status");

        TextBoxItem path = new TextBoxItem("path", "Path");
        TextBoxItem relativeTo = new TextBoxItem("relativeTo", "Relative To");
        TextBoxItem objectStorePath = new TextBoxItem("objectStorePath", "Object Store Path");
        TextBoxItem objectStorePathRelativeTo = new TextBoxItem("objectStoreRelativeTo", "Object Store Relative To");

        CheckBoxItem recoveryListener = new CheckBoxItem("recoveryListener", "Recovery Listener");
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem statusSocketBinding = new TextBoxItem("statusSocketBinding", "Status Socket Binding");

        form.setFields(enableStatistics, enableTsm, defaultTimeout, path, relativeTo, objectStorePath, objectStorePathRelativeTo);
        form.setFieldsInGroup("Recovery", new DisclosureGroupRenderer(), socketBinding, statusSocketBinding, recoveryListener);

        form.setEnabled(false);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "transactions");
                return address;
            }
        }, form);

        panel.add(helpPanel.asWidget());

        panel.add(form.asWidget());

        return layout;
    }


    @Override
    public void setTransactionManager(TransactionManager tm) {
        form.edit(tm);
    }
}
