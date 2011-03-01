package org.jboss.as.console.client.domain.model;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public interface ServerGroupStore {
    List<ServerGroupRecord> loadServerGroups();
    List<ServerGroupRecord> loadServerGroups(String profileName);

    void persist(ServerGroupRecord updatedEntity);

    boolean deleteGroup(ServerGroupRecord selectedRecord);
}
