package org.jboss.as.console.client.domain.groups;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public interface PropertyRecord {

    public String getKey();
    public void setKey(String key);

    public String getValue();
    public void setValue(String value);
}
