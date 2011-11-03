package org.jboss.as.console.client.shared.runtime.plain;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.runtime.TXMetric;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXPlainView implements TXExecutionSampler {

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

    @Override
    public void recycle() {

    }
}
