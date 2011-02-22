package org.jboss.as.console.client.shared;

import java.util.List;

/**
 * Responsible for loading deployment data
 * and turning it a usable representation.
 *
 * @author Heiko Braun
 * @date 1/31/11
 */
public interface DeploymentStore {
    List<DeploymentRecord> loadDeployments();
}
