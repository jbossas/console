package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockSubsystemStore implements SubsystemStore {

    static SubsystemRecord[] records = new SubsystemRecord[] {
            new SubsystemRecord("Threads"),
            new SubsystemRecord("Web"),
            new SubsystemRecord("EJB"),
            new SubsystemRecord("JCA"),
            new SubsystemRecord("Messaging"),
            new SubsystemRecord("Transactions"),
            new SubsystemRecord("Web Services"),
            new SubsystemRecord("Clustering")

    };

    @Override
    public SubsystemRecord[] loadSubsystems() {
        return records;
    }

    @Override
    public SubsystemRecord[] loadSubsystems(String profileName) {
        Log.debug("Loaded " + records.length + " subsystems for profile '"+profileName+"'");
        return records;
    }
}
