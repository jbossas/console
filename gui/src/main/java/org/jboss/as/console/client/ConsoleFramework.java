package org.jboss.as.console.client;

import com.google.gwt.autobean.shared.AutoBeanFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.ballroom.client.spi.Framework;

/**
 * @author Heiko Braun
 * @date 7/12/11
 */
public class ConsoleFramework implements Framework {

    private final static BeanFactory factory = GWT.create(BeanFactory.class);

    @Override
    public EventBus getEventBus() {
        return Console.getEventBus();
    }

    @Override
    public PlaceManager getPlaceManager() {
        return Console.getPlaceManager();
    }

    @Override
    public AutoBeanFactory getBeanFactory() {
        return factory;
    }
}
