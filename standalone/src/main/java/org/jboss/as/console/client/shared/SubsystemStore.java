package org.jboss.as.console.client.shared;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public interface SubsystemStore {

    /**
     * load subsystems for default profile (i.e. standalone server)
     * @return
     */
    List<SubsystemRecord> loadSubsystems();

    /**
     * load subsystems for specific profile
     * @param profileName
     * @return
     */
    List<SubsystemRecord> loadSubsystems(String profileName);
}
