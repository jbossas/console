package org.jboss.as.console.client.widgets.nav;

import org.jboss.ballroom.client.layout.LHSNavTreeItem;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public final class Predicate {
    private String subsysName;
    private LHSNavTreeItem navItem;

    public Predicate(String subsysName, LHSNavTreeItem navItem) {
        this.subsysName = subsysName;
        this.navItem = navItem;
    }

    public boolean matches(String current) {
        return current.equals(subsysName);
    }

    public LHSNavTreeItem getNavItem() {
        return navItem;
    }
}