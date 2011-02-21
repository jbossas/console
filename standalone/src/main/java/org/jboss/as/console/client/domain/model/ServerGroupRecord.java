package org.jboss.as.console.client.domain.model;

import org.jboss.as.console.client.util.DataClass;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ServerGroupRecord extends DataClass {

    public ServerGroupRecord(String name) {
        setAttribute("group-name", name);
    }

    public ServerGroupRecord(String name, String profile) {
        this(name);
        setAttribute("profile-name", profile);
    }
}
