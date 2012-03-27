package org.jboss.as.console.client.plugins;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/27/12
 */
public interface SubsystemRegistry {

    public List<SubsystemExtension> getExtensions();
}
