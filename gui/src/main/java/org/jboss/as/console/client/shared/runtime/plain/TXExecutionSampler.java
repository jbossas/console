package org.jboss.as.console.client.shared.runtime.plain;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.runtime.TXMetric;

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
