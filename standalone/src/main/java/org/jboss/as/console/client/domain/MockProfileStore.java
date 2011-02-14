package org.jboss.as.console.client.domain;

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
        return records;
    }
}
