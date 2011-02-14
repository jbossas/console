package org.jboss.as.console.client.domain;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class ProfileRecord extends ListGridRecord {

    public ProfileRecord(String name) {
        setAttribute("profile-name", name);
    }
}
