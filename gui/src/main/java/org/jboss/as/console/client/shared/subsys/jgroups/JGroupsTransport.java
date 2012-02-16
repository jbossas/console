package org.jboss.as.console.client.shared.subsys.jgroups;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.widgets.forms.Binding;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public interface JGroupsTransport {

    String getType();
    void setType(String type);

    @Binding(detypedName = "diagnostics-socket-binding")
    String getDiagSocketBinding();
    void setDiagSocketBinding(String socketBinding);

    String getMachine();
    void setMachine(String machine);

    boolean isShared();
    void setShared(boolean b);

    @Binding(detypedName = "socket-binding")
    String getSocketBinding();
    void setSocketBinding(String socketBinding);

    @Binding(detypedName = "default-executor")
    String getDefaultExecutor();
    void setDefaultExecutor(String executor);

    @Binding(detypedName = "oob-executor")
    String getOobExecutor();
    void setOobExecutor(String executor);


    @Binding(detypedName = "timer-executor")
    String getTimerExecutor();
    void setTimerExecutor(String executor);

    @Binding(detypedName = "thread-factory")
    String getThreadFactory();
    void setThreadFactory(String factory);

    String getSite();
    void setSite(String site);

    String getRack();
    void setRack(String rack);

    @Binding(skip = true)
    List<PropertyRecord> getProperties();
    void setProperties(List<PropertyRecord> properties);

}
