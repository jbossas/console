package org.jboss.as.console.client.shared.subsys.naming;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class JndiEntry {

    String name;

    Map<String, JndiEntry> children = new HashMap<String, JndiEntry>();

    public JndiEntry(String name) {
        this.name = name;
    }

    public Map<String, JndiEntry> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return name +"["+children.size()+"]";
    }
}
