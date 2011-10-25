package org.jboss.as.console.client.shared.subsys.tx.charts;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.tx.TXMetricSampler;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXPlainView implements TXMetricSampler {

    private TextBoxItem total;
    private TextBoxItem committed;
    private TextBoxItem aborted;
    private TextBoxItem timedout;

    @Override
    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        Form<TXMetric> form = new Form<TXMetric>(TXMetric.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        total = new TextBoxItem("total", "Total");
        committed = new TextBoxItem("committed", "Committed");
        aborted = new TextBoxItem("aborted", "Aborted");
        timedout= new TextBoxItem("timedout", "Timed Out");

        form.setFields(total, committed, aborted, timedout);
        layout.add(form.asWidget());

        return layout;
    }

    @Override
    public void addSample(TXMetric metric) {
        total.setValue(String.valueOf(metric.getTotal()));
        committed.setValue(String.valueOf(metric.getCommitted()));
        aborted.setValue(String.valueOf(metric.getAborted()));
        timedout.setValue(String.valueOf(metric.getTimedOut()));
    }

    @Override
    public void clearSamples() {
        total.clearValue();
        committed.clearValue();
        aborted.clearValue();
        timedout.clearValue();
    }

    @Override
    public long numSamples() {
        return 1;
    }
}
