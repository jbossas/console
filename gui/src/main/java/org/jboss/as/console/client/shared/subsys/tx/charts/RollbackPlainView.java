package org.jboss.as.console.client.shared.subsys.tx.charts;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.tx.TXRollbackSampler;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class RollbackPlainView implements TXRollbackSampler {

    private TextBoxItem appRollback;
    private TextBoxItem resourceRollback;

    @Override
    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        Form<TXMetric> form = new Form<TXMetric>(TXMetric.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        appRollback = new TextBoxItem("total", "Application Rollbacks");
        resourceRollback = new TextBoxItem("committed", "Resource Rollbacks");

        form.setFields(appRollback, resourceRollback);
        layout.add(form.asWidget());

        return layout;
    }

    @Override
    public void addSample(RollbackMetric metric) {
        appRollback.setValue(String.valueOf(metric.getAppRollback()));
        resourceRollback.setValue(String.valueOf(metric.getResourceRollback()));
    }

    @Override
    public void clearSamples() {
        appRollback.clearValue();
        resourceRollback.clearValue();
    }

    @Override
    public long numSamples() {
        return 1;
    }

    @Override
    public void recycle() {

    }
}
