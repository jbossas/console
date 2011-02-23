package org.jboss.as.console.client.components.sgwt;

import org.jboss.as.console.client.components.ViewFactory;
import org.jboss.as.console.client.components.ViewName;

/**
 * @author Ian Springer
 */
@Deprecated
public class NavigationItem {
    private final ViewName viewName;
    private final String icon;
    private final ViewFactory viewFactory;
    private final boolean enabled;

    private boolean refreshRequired = false;

    public NavigationItem(ViewName name, String icon, ViewFactory viewFactory) {
        this(name, icon, viewFactory, true);
    }

    public NavigationItem(ViewName name, String icon, ViewFactory viewFactory, boolean enabled) {
        this.icon = (icon!=null & !icon.equals("")) ? icon : "images/blank.png";
        this.viewName = name;
        this.viewFactory = viewFactory;
        this.enabled = enabled;
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

    public String getIcon() {
        return icon;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * This flag indicates if the item's view must be refreshed when its main navigation item
     * is selected within the user interface. Most times, if the navigation item's view is already
     * created, it can just be re-displayed as it was previously - in which case this flag can
     * be <code>false</code> (which is its default). However, if the view must be recreated due to
     * the possibility of some dynamic data changing (where that dynamic data is needed to build
     * the view), then this flag should be <code>true</code>.
     *
     * @return flag to indicate if the navigation item's view needs to be refreshed when the item is selected again
     */
    public boolean isRefreshRequired() {
        return refreshRequired;
    }

    public void setRefreshRequired(boolean flag) {
        this.refreshRequired = flag;
    }
}
