package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockServerGroupStore implements ServerGroupStore {

    static Map props = new LinkedHashMap();
    static {
        props.put("-XX:-UseParallelGC", "true");
        props.put("-XX:-DisableExplicitGC", "true");
        props.put("DEFAULT_LOG_LEVEL", "INFO");
    }

    static ServerGroupRecord[] records = new ServerGroupRecord [] {
            new ServerGroupRecord("EE6 Server", "EE6 Web")
            {{
                    setAttribute("jvm", "jdk_16_default");
                    setAttribute("socket-binding", "default");
                    setAttribute("properties", props);
                }},
            new ServerGroupRecord("Web Server", "EE6 Web")
            {{
                    setAttribute("jvm", "jdk_15_default");
                    setAttribute("socket-binding", "DMZ");
                }},
            new ServerGroupRecord("Payment", "Messaging")
            {{
                    setAttribute("jvm", "jrockit_15");
                    setAttribute("socket-binding", "default");
                }},
            new ServerGroupRecord("Hot Standby", "BPM Platform")
            {{
                    setAttribute("jvm", "jdk_16_default");
                    setAttribute("socket-binding", "default");
                }},
            new ServerGroupRecord("Backoffice", "EE6 Web")
            {{
                    setAttribute("jvm", "jdk_16_default");
                    setAttribute("socket-binding", "default");
                }}

    };

    @Override
    public ServerGroupRecord[] loadServerGroups() {

        Log.debug("Loaded " + records.length + " server groups");
        return records;
    }
}
