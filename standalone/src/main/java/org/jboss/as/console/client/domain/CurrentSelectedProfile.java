package org.jboss.as.console.client.domain;

import org.jboss.as.console.client.domain.model.ProfileRecord;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class CurrentSelectedProfile {

    private ProfileRecord currentProfile;

    public ProfileRecord get() {
        return currentProfile;
    }

    public void set(ProfileRecord currentProfile) {
        this.currentProfile = currentProfile;
    }
}
