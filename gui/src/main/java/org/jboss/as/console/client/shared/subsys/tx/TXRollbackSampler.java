package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.tx.model.RollbackMetric;
import org.jboss.as.console.client.shared.subsys.tx.model.TXMetric;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public interface TXRollbackSampler {

    Widget asWidget();

    void addSample(RollbackMetric metric);

    void clearSamples();

    long numSamples();
}
