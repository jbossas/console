package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class SubsystemRecord extends ListGridRecord {

    public SubsystemRecord(String name) {
        setAttribute("subsystem-name", name);
    }

    public String getTitle() {
        return getName();
    }

    public String getName() {
        return getAttribute("subsystem-name");
    }
}
