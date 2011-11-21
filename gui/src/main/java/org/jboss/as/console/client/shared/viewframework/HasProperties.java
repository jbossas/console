package org.jboss.as.console.client.shared.viewframework;

import org.jboss.as.console.client.shared.properties.PropertyRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
public interface HasProperties {
    List<PropertyRecord> getProperties();
}
