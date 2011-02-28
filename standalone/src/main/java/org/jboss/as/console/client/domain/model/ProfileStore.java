package org.jboss.as.console.client.domain.model;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ProfileStore {
    List<ProfileRecord> loadProfiles();
    List<String> loadProfileNames();
}
