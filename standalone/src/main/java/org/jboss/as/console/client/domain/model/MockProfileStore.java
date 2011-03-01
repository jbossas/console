package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import org.jboss.as.console.client.shared.BeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public class MockProfileStore implements ProfileStore {

    BeanFactory factory = GWT.create(BeanFactory.class);
    private final String[] names = new String[] {"EE6 Web", "Messaging", "BPM Platform"};

    @Override
    public List<ProfileRecord> loadProfiles()
    {
        List<ProfileRecord> records = new ArrayList<ProfileRecord>(names.length);

        for(String name : names)
        {
            ProfileRecord profile = factory.profile().as();
            profile.setName(name);
            records.add(profile);
        }

        Log.debug("Loaded " + records.size()+ " profiles");

        return records;
    }

    @Override
    public List<String> loadProfileNames() {
        List<ProfileRecord> profileRecords = loadProfiles();
        List<String> names = new ArrayList<String>();

        for(ProfileRecord profile : profileRecords)
        {
            names.add(profile.getName());
        }
        return names;
    }
}
