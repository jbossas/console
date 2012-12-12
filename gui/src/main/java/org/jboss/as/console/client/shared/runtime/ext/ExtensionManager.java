package org.jboss.as.console.client.shared.runtime.ext;

/**
 * @author Heiko Braun
 * @date 12/12/12
 */
public interface ExtensionManager {
    void loadExtensions();

    void onDumpVersions();
}
