package org.jboss.as.console.client.domain.model;

import org.jboss.as.console.client.domain.model.ProfileRecord;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ProfileStore {
    ProfileRecord[] loadProfiles();
}
