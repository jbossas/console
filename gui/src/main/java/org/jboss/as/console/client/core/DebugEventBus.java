package org.jboss.as.console.client.core;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.Event;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;

import javax.inject.Inject;

/**
 * @author Heiko Braun
 * @date 8/29/12
 */
public class DebugEventBus extends SimpleEventBus {

    private InvocationMetrics metrics;

    @Inject
    public DebugEventBus(InvocationMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void fireEvent(Event<?> event) {

        try {
            super.fireEvent(event);
        } finally {

            String clazz = event.getClass().getName();
            if(metrics.hasMetrics() &&
                    clazz.startsWith("org.jboss.as.console.client")) {
                System.out.println("---- event stats: "+ clazz +" ----");

                metrics.dump();
                metrics.reset();
                System.out.println("---- /event stats ----");
            }
        }

    }
}
