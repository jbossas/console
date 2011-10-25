package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public interface TXExecutionSampler {

    Widget asWidget();

    void addSample(TXMetric metric);

    void clearSamples();

    long numSamples();

    void recycle();
}
