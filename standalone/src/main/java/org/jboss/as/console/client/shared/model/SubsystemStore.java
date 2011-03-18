package org.jboss.as.console.client.shared.model;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
public interface SubsystemStore {

    /**
     * load subsystems for specific profile
     * @param profileName
     * @return
     */
    void loadSubsystems(String profileName, AsyncCallback<List<SubsystemRecord>> callback);
}
