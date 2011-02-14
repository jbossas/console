package org.jboss.as.console.client.domain;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ProfileStore {
    ProfileRecord[] loadProfiles();
}
