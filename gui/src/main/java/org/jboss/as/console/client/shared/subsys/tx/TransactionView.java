package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TransactionView extends SuspendableViewImpl implements TransactionPresenter.MyView{

    private TransactionPresenter presenter = null;

    private boolean provideMetrics = true;
    private TXMetricView overviewMetric;
    private TXRollbackView rollbackMetric;

    @Override
    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new RHSContentPanel("Transactions");

        layout.add(new ContentHeaderLabel("Transaction Manager Configuration"));

        // -----

        layout.add(new ContentGroupLabel("Attributes"));

        Form<TransactionManager> form = new Form<TransactionManager>(TransactionManager.class);
        form.setNumColumns(2);

        NumberBoxItem defaultTimeout = new NumberBoxItem("defaultTimeout", "Default Timeout");
        CheckBoxItem enableStatistics = new CheckBoxItem("enableStatistics", "Enable Statistics");
        CheckBoxItem enableTsm = new CheckBoxItem("enableTsmStatus", "Enable TSM Status");

        CheckBoxItem recoveryListener = new CheckBoxItem("recoveryListener", "Recovery Listener");
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem statusSocketBinding = new TextBoxItem("statusSocketBinding", "Status Socket Binding");

        form.setFields(enableStatistics, enableTsm, defaultTimeout);
        form.setFieldsInGroup("Advanced", new DisclosureGroupRenderer(), socketBinding, statusSocketBinding, recoveryListener);

        form.setEnabled(false);

        layout.add(form.asWidget());

        // ----------------------------------

        if(provideMetrics){
            layout.add(new ContentGroupLabel("Metrics"));

            TabPanel bottomLayout = new TabPanel();
            bottomLayout.addStyleName("default-tabpanel");
            bottomLayout.getElement().setAttribute("style", "padding-top:20px;");

            this.overviewMetric = new TXMetricView(presenter);
            bottomLayout.add(overviewMetric.asWidget(),"Number of Transactions");

            this.rollbackMetric = new TXRollbackView(presenter);
            bottomLayout.add(rollbackMetric.asWidget(),"Number of Rollbacks");

            bottomLayout.selectTab(0);

            layout.add(bottomLayout);
        }

        return layout;
    }
}
