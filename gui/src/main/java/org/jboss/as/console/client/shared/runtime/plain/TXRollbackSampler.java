package org.jboss.as.console.client.shared.runtime.plain;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.runtime.RollbackMetric;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public interface TXRollbackSampler {

    Widget asWidget();

    void addSample(RollbackMetric metric);

    void clearSamples();

    long numSamples();

    void recycle();
}
