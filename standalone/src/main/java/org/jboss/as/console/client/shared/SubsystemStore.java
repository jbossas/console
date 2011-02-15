package org.jboss.as.console.client.shared;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public interface SubsystemStore {

    /**
     * load subsystems for default profile (i.e. standalone server)
     * @return
     */
    SubsystemRecord[] loadSubsystems();

    /**
     * load subsystems for specific profile
     * @param profileName
     * @return
     */
    SubsystemRecord[] loadSubsystems(String profileName);
}
