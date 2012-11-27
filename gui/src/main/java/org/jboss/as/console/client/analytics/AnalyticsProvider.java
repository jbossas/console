package org.jboss.as.console.client.analytics;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalytics;
import com.gwtplatform.mvp.client.googleanalytics.GoogleAnalyticsImpl;
import org.jboss.as.console.client.shared.Preferences;

/**
 * @author Heiko Braun
 * @date 10/24/12
 */
public class AnalyticsProvider implements Provider<GoogleAnalytics> {

    private GoogleAnalytics delegate;
    private static final GoogleAnalytics NOOP = new NoopAnalytics();

    @Override
    public GoogleAnalytics get() {

        GoogleAnalytics analytics = null;

        if(!Preferences.has(Preferences.Key.ANALYTICS) // not set at all
            || Preferences.get(Preferences.Key.ANALYTICS).equals("true")) // or set to true
        {
            analytics = new CustomAnalyticsImpl();
            System.out.println("Google analytics is setup");
        }
        else
        {
            System.out.println("Running non-operational analytics implementation");
            analytics = NOOP;
        }

        return analytics;
    }
    
    static class NoopAnalytics implements GoogleAnalytics
    {
        @Override
        public void init(String userAccount) {
            
        }

        @Override
        public void addAccount(String trackerName, String userAccount) {
            
        }

        @Override
        public void trackPageview() {
            
        }

        @Override
        public void trackPageview(String pageName) {
            
        }

        @Override
        public void trackPageview(String trackerName, String pageName) {
            
        }

        @Override
        public void trackEvent(String category, String action) {
            
        }

        @Override
        public void trackEventWithTracker(String trackerName, String category, String action) {
            
        }

        @Override
        public void trackEvent(String category, String action, String optLabel) {
            
        }

        @Override
        public void trackEventWithTracker(String trackerName, String category, String action, String optLabel) {
            
        }

        @Override
        public void trackEvent(String category, String action, String optLabel, int optValue) {
            
        }

        @Override
        public void trackEventWithTracker(String trackerName, String category, String action, String optLabel, int optValue) {
            
        }

        @Override
        public void trackEvent(String category, String action, String optLabel, int optValue, boolean optNonInteraction) {
            
        }

        @Override
        public void trackEventWithTracker(String trackerName, String category, String action, String optLabel, int optValue, boolean optNonInteraction) {
            
        }
    }
}
