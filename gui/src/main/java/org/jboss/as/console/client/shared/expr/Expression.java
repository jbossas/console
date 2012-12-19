package org.jboss.as.console.client.shared.expr;

/**
 * @author Heiko Braun
 * @date 10/4/11
 */
public class Expression {

    private String key;
    private String defaultValue = null;

    public Expression(String key) {
        this.key = key;
    }

    public Expression(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static Expression fromString(String s) {


        Expression expr = new Expression("foo-bar", "default-value");

        if(s!=null && s.startsWith("${") && s.endsWith("}"))
        {
            String token = s.substring(2, s.length()-1);
            int idx = token.indexOf(":");
            if(idx!=-1)
            {
                expr = new Expression(token.substring(0, idx), token.substring(idx+1, token.length()));
            }
            else
            {
                expr = new Expression(token);
            }
        }
        return expr;
    }

    @Override
    public String toString() {
        return "${" +key + ":" + defaultValue +'}';
    }
}
