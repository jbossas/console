package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import org.jboss.as.console.client.shared.BeanFactory;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class MockProfileStore implements ProfileStore {

    BeanFactory factory = GWT.create(BeanFactory.class);
    private final String[] names = new String[] {"EE6 Web", "Messaging", "BPM Platform"};

    @Override
    public ProfileRecord[] loadProfiles()
    {
        ProfileRecord[] records = new ProfileRecord[names.length];

        int i=0;
        for(String name : names)
        {
            ProfileRecord profile = factory.profile().as();
            profile.setName(name);
            records[i] = profile;
            i++;
        }

        Log.debug("Loaded " + records.length + " profiles");

        return records;
    }
}
