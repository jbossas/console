package org.jboss.as.console.client.tools.mapping;

/**
 * @author Heiko Braun
 * @date 7/16/12
 */
public final class RequestParameter {

    String paramDesc ;
    String paramName;
    String paramType;
    boolean required;

    public RequestParameter(String paramDesc, String paramName, String paramType, boolean required) {
        this.paramDesc = paramDesc;
        this.paramName = paramName;
        this.paramType = paramType;
        this.required = required;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public boolean isRequired() {
        return required;
    }
}
