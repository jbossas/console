package org.jboss.as.console.client.shared.subsys.logging.model;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
public interface HasHandlers {
    List<String> getHandlers();
    void setHandlers(List<String> values);
}
