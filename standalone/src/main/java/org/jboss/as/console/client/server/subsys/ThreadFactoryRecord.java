package org.jboss.as.console.client.server.subsys;

import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.Map;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadFactoryRecord extends ListGridRecord {
    public ThreadFactoryRecord(String name, String group, int prio) {
        setAttribute("name", name);
        setAttribute("group", group);
        setAttribute("prio", prio);
    }

    public ThreadFactoryRecord() {
    }

    public void fromValues(Map values)
    {
        Set<String> keys = values.keySet();
        for(String key : keys)
        {
            setAttribute(key, values.get(key));
        }
    }
}