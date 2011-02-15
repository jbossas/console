package org.jboss.as.console.client.shared;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.as.console.client.NameTokens;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public class MockSubsystemStore implements SubsystemStore {

    static SubsystemRecord[] records = new SubsystemRecord[] {
            new SubsystemRecord(NameTokens.ThreadManagementPresenter,"Threads"),
            new SubsystemRecord("web","Web"),
            new SubsystemRecord("ejb","EJB"),
            new SubsystemRecord("jca","JCA"),
            new SubsystemRecord("messaging","Messaging"),
            new SubsystemRecord("tx","Transactions"),
            new SubsystemRecord("ws","Web Services"),
            new SubsystemRecord("ha","Clustering")

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
