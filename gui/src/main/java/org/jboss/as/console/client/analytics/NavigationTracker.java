package org.jboss.as.console.client.analytics;

import com.google.gwt.core.client.Scheduler;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.GaAccount;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 10/24/12
 */
public class NavigationTracker implements NavigationHandler {
    private final GoogleAnalytics analytics;
    private EventBus eventBus;

    @Inject
    public NavigationTracker(
            @GaAccount final String gaAccount,
            final EventBus eventBus, GoogleAnalytics delegate) {

        this.analytics = delegate;
        this.eventBus = eventBus;

       // if (GWT.isScript()) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    analytics.init(gaAccount);
                    bind();
                }
            });
       // }
    }

    public void bind() {
        eventBus.addHandler(NavigationEvent.getType(), NavigationTracker.this);
    }

    @Override
    public void onNavigation(NavigationEvent navigationEvent) {
        analytics.trackPageview(navigationEvent.getRequest().getNameToken());
    }
}