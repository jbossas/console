package org.jboss.as.console.client.domain.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public abstract class SimpleCallback<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(Throwable caught) {
        Log.error("Unknown error", caught);
    }
}
