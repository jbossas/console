package org.jboss.as.console.client.core.bootstrap;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import org.jboss.as.console.client.Build;
import org.jboss.as.console.client.core.BootstrapContext;

import java.util.Iterator;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class TrackExecutionMode extends BoostrapStep {

    private BootstrapContext bootstrap;
    private GoogleAnalytics analytics;

    public TrackExecutionMode(BootstrapContext bootstrapContext, GoogleAnalytics analytics) {
        this.bootstrap = bootstrapContext;
        this.analytics = analytics;
    }

    @Override
    public void execute(Iterator<BoostrapStep> iterator, AsyncCallback<Boolean> outcome) {

        String value = bootstrap.isStandalone() ? "standalone" : "domain";
        analytics.trackEvent("bootstrap", "exec-mode", value);
        analytics.trackEvent("bootstrap", "console-version", Build.VERSION);
        outcome.onSuccess(Boolean.TRUE);
        next(iterator, outcome);
    }
}
