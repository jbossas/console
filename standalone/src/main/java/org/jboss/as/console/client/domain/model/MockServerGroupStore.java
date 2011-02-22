package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.*;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockServerGroupStore implements ServerGroupStore {

    BeanFactory factory = GWT.create(BeanFactory.class);

    static Map props = new LinkedHashMap();
    static {
        props.put("-XX:-UseParallelGC", "true");
        props.put("-XX:-DisableExplicitGC", "true");
        props.put("DEFAULT_LOG_LEVEL", "INFO");
    }

    /*static ServerGroupRecord[] records = new ServerGroupRecord [] {
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

  };  */

    @Override
    public ServerGroupRecord[] loadServerGroups() {

        List<ServerGroupRecord> tmp = new ArrayList<ServerGroupRecord>();

        ServerGroupRecord eeServer = factory.serverGroup().as();
        eeServer.setGroupName("EE6 Server");
        eeServer.setProfileName("EE6 Web");
        eeServer.setProperties(props);
        eeServer.setJvm("jdk_16_default");
        eeServer.setSocketBinding("default");
        tmp.add(eeServer);

        ServerGroupRecord webServer = factory.serverGroup().as();
        webServer.setGroupName("Web Server");
        webServer.setProfileName("EE6 Web");
        webServer.setProperties(Collections.EMPTY_MAP);
        webServer.setJvm("jdk_16_default");
        webServer.setSocketBinding("DMZ");
        tmp.add(webServer);
        
        ServerGroupRecord standby = factory.serverGroup().as();
        standby.setGroupName("Hot Standby");
        standby.setProfileName("Messaging");
        standby.setProperties(Collections.EMPTY_MAP);
        standby.setJvm("jrockit");
        standby.setSocketBinding("default_no_http");
        tmp.add(standby);
        
        Log.debug("Loaded " + tmp.size()+ " server groups");
        return tmp.toArray(new ServerGroupRecord[]{});
    }
}
