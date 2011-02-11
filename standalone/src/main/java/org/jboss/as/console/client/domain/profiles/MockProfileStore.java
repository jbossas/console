package org.jboss.as.console.client.domain.profiles;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class MockProfileStore implements ProfileStore {

    private final ProfileRecord[] records =
            new ProfileRecord[]
                    {
                            new ProfileRecord("EE6 Web Profile"),
                            new ProfileRecord("Messaging Profile"),
                            new ProfileRecord("BPM Platform Profile")

                    };

    @Override
    public ProfileRecord[] loadProfiles()
    {
        return records;
    }
}
