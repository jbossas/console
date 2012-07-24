package org.jboss.as.console.client.tools;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Heiko Braun
 * @date 7/24/12
 */
public interface FXFormManager {

    void createFormProxy(String templateId, String modelId, AsyncCallback<FormProxy> callback);
}
