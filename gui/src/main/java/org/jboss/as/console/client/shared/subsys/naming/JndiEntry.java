package org.jboss.as.console.client.shared.subsys.naming;

import java.util.ArrayList;
import java.util.List;

class JndiEntry {

    private String name;
    private List<JndiEntry> children;

    JndiEntry(String name) {
        this.name = name;
        this.children = new ArrayList<JndiEntry>();
    }

    public List<JndiEntry> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}