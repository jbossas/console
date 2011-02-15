package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockServerGroupStore implements ServerGroupStore {

    static ServerGroupRecord[] records = new ServerGroupRecord [] {
            new ServerGroupRecord("EE6 Server"),
            new ServerGroupRecord("Web Server"),
            new ServerGroupRecord("Payment"),
            new ServerGroupRecord("Hot Standby"),
            new ServerGroupRecord("Backoffice")

    };

    @Override
    public ServerGroupRecord[] loadServerGroups() {

        Log.debug("Loaded " + records.length + " server groups");
        return records;
    }
}
