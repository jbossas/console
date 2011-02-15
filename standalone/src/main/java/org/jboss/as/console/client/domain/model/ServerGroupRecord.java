package org.jboss.as.console.client.domain.model;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ServerGroupRecord extends ListGridRecord {

    public ServerGroupRecord(String name) {
        setAttribute("group-name", name);
    }
}
