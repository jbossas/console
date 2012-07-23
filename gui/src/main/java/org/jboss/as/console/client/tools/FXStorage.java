package org.jboss.as.console.client.tools;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public interface FXStorage {
    Set<FXTemplate> loadTemplates();
    FXTemplate loadTemplate(String id);
    void storeTemplate(FXTemplate template);
    void removeTemplate(String id);

}
