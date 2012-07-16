package org.jboss.as.console.client.tools.mapping;

/**
 * @author Heiko Braun
 * @date 7/16/12
 */
public class ResponseParameter {

    String replyDesc;
    String replyType;

    public ResponseParameter(String replyDesc, String replyType) {
        this.replyDesc = replyDesc;
        this.replyType = replyType;
    }

    public String getReplyDesc() {
        return replyDesc;
    }

    public String getReplyType() {
        return replyType;
    }

}
