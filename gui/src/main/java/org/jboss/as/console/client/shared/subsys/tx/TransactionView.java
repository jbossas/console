package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;
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

    private boolean provideMetrics = true;
    private TXExecutionView executionMetric;
    private TXRollbackView rollbackMetric;

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

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(toolstripWidget, 28, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 100, Style.Unit.PCT);

        panel.add(new ContentHeaderLabel("Transaction Manager Configuration"));

        // -----

        panel.add(new ContentGroupLabel("Attributes"));


        NumberBoxItem defaultTimeout = new NumberBoxItem("defaultTimeout", "Default Timeout");
        CheckBoxItem enableStatistics = new CheckBoxItem("enableStatistics", "Enable Statistics");
        CheckBoxItem enableTsm = new CheckBoxItem("enableTsmStatus", "Enable TSM Status");

        TextBoxItem path = new TextBoxItem("path", "Path");
        TextBoxItem relativeTo = new TextBoxItem("relativeTo", "Relative To");
        TextBoxItem objectStorePath = new TextBoxItem("objectStorePath", "Object Store Path");
        TextBoxItem objectStorePathRelativeTo = new TextBoxItem("ObjectStoreRelativeTo", "Object Store Relative To");

        CheckBoxItem recoveryListener = new CheckBoxItem("recoveryListener", "Recovery Listener");
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem statusSocketBinding = new TextBoxItem("statusSocketBinding", "Status Socket Binding");

        form.setFields(enableStatistics, enableTsm, defaultTimeout);
        form.setFieldsInGroup("Storage", new DisclosureGroupRenderer(), path, relativeTo, objectStorePath, objectStorePathRelativeTo);
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

        panel.add(form.asWidget());

        // ----------------------------------

        if(provideMetrics){
            panel.add(new ContentGroupLabel("Metrics"));

            TabPanel bottomLayout = new TabPanel();
            bottomLayout.addStyleName("default-tabpanel");
            bottomLayout.getElement().setAttribute("style", "padding-top:20px;");

            this.executionMetric = new TXExecutionView(presenter);
            bottomLayout.add(executionMetric.asWidget(),"Transactions");

            this.rollbackMetric = new TXRollbackView(presenter);
            bottomLayout.add(rollbackMetric.asWidget(),"Rollbacks");

            bottomLayout.selectTab(0);

            panel.add(bottomLayout);
        }

        return layout;
    }

    public void setProvideMetrics(boolean provideMetrics) {
        this.provideMetrics = provideMetrics;
    }

    @Override
    public void setTransactionManager(TransactionManager tm) {
        form.edit(tm);

        if(provideMetrics)
        {
            executionMetric.addSample(
                    new TXMetric(
                            tm.getNumTransactions(),
                            tm.getNumCommittedTransactions(),
                            tm.getNumAbortedTransactions(),
                            tm.getNumTimeoutTransactions())
            );

            rollbackMetric.addSample(
                    new RollbackMetric(
                            tm.getNumApplicationRollback(),
                            tm.getNumResourceRollback())
            );
        }

    }

    @Override
    public void recycleCharts() {
        if(provideMetrics)
        {
            executionMetric.recycle();
            rollbackMetric.recycle();
        }
    }
}
