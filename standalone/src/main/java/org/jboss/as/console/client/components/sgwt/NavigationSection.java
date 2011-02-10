package org.jboss.as.console.client.components.sgwt;

import org.jboss.as.console.client.components.ViewName;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ian Springer
 */
public class NavigationSection {
    private ViewName viewName;
    private List<NavigationItem> navigationItems;
    private Map<String, NavigationItem> navigationItemsByName;

    public NavigationSection(ViewName name, NavigationItem... navigationItems) {
        this.viewName = name;
        this.navigationItems = Arrays.asList(navigationItems);
        this.navigationItemsByName = new LinkedHashMap<String, NavigationItem>(navigationItems.length);
        for (NavigationItem navigationItem : navigationItems) {
            this.navigationItemsByName.put(navigationItem.getName(), navigationItem);
        }
    }

    public ViewName getViewName() {
        return viewName;
    }

    public String getName() {
        return viewName.getName();
    }

    public String getTitle() {
        return viewName.getTitle();
    }

    public List<NavigationItem> getNavigationItems() {
        return navigationItems;
    }

    public NavigationItem getNavigationItem(String name) {
        return navigationItemsByName.get(name);
    }
}
