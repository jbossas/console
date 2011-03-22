package org.jboss.as.console.client.domain.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public interface ServerGroupStore {

    void loadServerGroups(AsyncCallback<List<ServerGroupRecord>> callback);
    void loadServerGroup(String name, AsyncCallback<ServerGroupRecord> callback);
    void loadSocketBindingGroupNames(final AsyncCallback<List<String>> callback);
    void persist(ServerGroupRecord updatedEntity, AsyncCallback<Boolean> callback);
    void deleteGroup(ServerGroupRecord selectedRecord, AsyncCallback<Boolean> callback);
}
