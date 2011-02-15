package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import org.jboss.as.console.client.domain.model.ProfileRecord;
import org.jboss.as.console.client.domain.model.ProfileStore;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class MockProfileStore implements ProfileStore {

    private final ProfileRecord[] records =
            new ProfileRecord[]
                    {
                            new ProfileRecord("EE6 Web"),
                            new ProfileRecord("Messaging"),
                            new ProfileRecord("BPM Platform")

                    };

    @Override
    public ProfileRecord[] loadProfiles()
    {
        Log.debug("Loaded " + records.length + " profiles");
        return records;
    }
}
