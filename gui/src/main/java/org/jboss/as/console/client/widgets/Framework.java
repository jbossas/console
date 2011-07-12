package org.jboss.as.console.client.widgets;

import com.google.gwt.autobean.shared.AutoBeanFactory;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

/**
 *
 * Shared components the widget library does rely on.
 *
 * @author Heiko Braun
 * @date 7/12/11
 */
public interface Framework {

    EventBus getEventBus();
    PlaceManager getPlaceManager();
    AutoBeanFactory getBeanFactory();
}
