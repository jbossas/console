package org.jboss.as.console.client.shared.dispatch;

import com.google.gwt.core.client.Scheduler;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 8/29/12
 */
public class DMRCache {

    private static Map<String, DMRResponse> values = new HashMap<String, DMRResponse>();
    private static int expiryTimeMs = 5*1000;

    public void put(final String key, DMRResponse response)
    {
        values.put(key, response);
        Scheduler.get().scheduleFixedPeriod(
                new Scheduler.RepeatingCommand() {
                    @Override
                    public boolean execute() {
                        values.remove(key);
                        return false;
                    }
                }, expiryTimeMs
        );
    }

    public DMRResponse get(String key) {
      return values.get(key);
    }

    public int size() {
        return values.size();
    }
}
