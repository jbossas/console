package org.jboss.as.console.client.domain.model;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/11/11
 */
public interface ServerGroupRecord {

    public String getGroupName();
    public void setGroupName(String name);

    public void setProfileName(String name);
    public String getProfileName();

    public void setProperties(Map<String,String> props);
    public Map<String,String> getProperties();

    public String getJvm();
    public void setJvm(String jvm);

    public String getSocketBinding();
    public void setSocketBinding(String socketBindingRef);
}
