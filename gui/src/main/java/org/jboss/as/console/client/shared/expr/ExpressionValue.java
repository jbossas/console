package org.jboss.as.console.client.shared.expr;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public interface ExpressionValue {
    String getKey();
    void setKey(String s);

    String getDefaultValue();
    void setDefaultValue(String s);

    String getResolvedValue();
    void setResolvedValue(String s);
}
