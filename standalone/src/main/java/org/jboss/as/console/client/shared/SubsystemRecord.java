package org.jboss.as.console.client.shared;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class SubsystemRecord extends ListGridRecord {

    public SubsystemRecord(String token, String title) {
        setAttribute("subsystem-token", token);
        setAttribute("subsystem-title", title);
    }

    public String getTitle() {
        return getAttribute("subsystem-title");
    }

    public String getToken() {
        return getAttribute("subsystem-token");
    }
}
