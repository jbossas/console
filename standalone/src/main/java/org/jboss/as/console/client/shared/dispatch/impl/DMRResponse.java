package org.jboss.as.console.client.shared.dispatch.impl;


import org.jboss.as.console.client.shared.dispatch.Result;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRResponse<ModelNode> implements Result {
    private String responseText;
    private String contentType;

    public DMRResponse(String responseText, String contentType) {
        this.responseText = responseText;
        this.contentType = contentType;
    }

    public String getResponseText() {
        return responseText;
    }
}
