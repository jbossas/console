package org.jboss.as.console.client.domain.groups;

import org.jboss.as.console.client.util.DataClass;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class PropertyRecord extends DataClass {

    public PropertyRecord(String key, String value) {
        setAttribute("key", key);
        setAttribute("value", value);
    }

    public String getKey() {
        return getAttribute("key");
    }

    public String getValue() {
        return getAttribute("value");
    }
}
