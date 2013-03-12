package org.jboss.as.console.client.core.bootstrap;

import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import org.jboss.as.console.client.Build;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.gwt.flow.client.Control;
import org.jboss.gwt.flow.client.Function;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class TrackExecutionMode implements Function<BootstrapContext> {

    private GoogleAnalytics analytics;

    public TrackExecutionMode(GoogleAnalytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public void execute(Control<BootstrapContext> control) {

        BootstrapContext bootstrap = control.getContext();

        String value = bootstrap.isStandalone() ? "standalone" : "domain";
        analytics.trackEvent("bootstrap", "exec-mode", value);
        analytics.trackEvent("bootstrap", "console-version", Build.VERSION);

        control.proceed();
    }

}
