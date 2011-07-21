package org.jboss.as.console.client.shared.subsys.naming;

import java.util.ArrayList;
import java.util.List;

class JndiEntry {

    private String name;
    private String type ="";
    private List<JndiEntry> children;

    JndiEntry(String name) {
        this.name = name;
        this.children = new ArrayList<JndiEntry>();
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {

        int idx = value.lastIndexOf(".");

        if(value!=null && idx>0) {
           value = value.substring(idx+1, value.length());
           this.type = value;
        }
    }

    public List<JndiEntry> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }
}