package org.jboss.as.console.client.shared.subsys.ejb3.model;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public interface Module {

    String getName();
    void setName(String name);

    String getSlot();
    void setSlot(String name);
}
