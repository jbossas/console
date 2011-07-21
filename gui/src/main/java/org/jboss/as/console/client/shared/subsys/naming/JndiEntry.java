package org.jboss.as.console.client.shared.subsys.naming;

import java.util.ArrayList;
import java.util.List;

class JndiEntry {

    private boolean name;
    private List<JndiEntry> children;

    JndiEntry(boolean name) {
        this.name = name;
        this.children = new ArrayList<JndiEntry>();
    }

    public List<JndiEntry> getChildren() {
        return children;
    }

    public boolean getName() {
        return name;
    }
}