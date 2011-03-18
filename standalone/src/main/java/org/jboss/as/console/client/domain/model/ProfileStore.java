package org.jboss.as.console.client.domain.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ProfileStore {
    void loadProfiles(AsyncCallback<List<ProfileRecord>> callback);
}
