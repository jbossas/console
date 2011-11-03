package org.jboss.as.console.client.shared.runtime;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public interface Sampler {

    Widget asWidget();

    void addSample(Metric metric);

    void clearSamples();

    long numSamples();

    void recycle();
}
