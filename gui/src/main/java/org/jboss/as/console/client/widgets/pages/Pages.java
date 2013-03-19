
package org.jboss.as.console.client.widgets.pages;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabListenerCollection;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;

public class Pages extends Composite implements TabListener,
        SourcesTabEvents, HasWidgets, IndexedPanel {

    /**
     * This extension of DeckPanel overrides the public mutator methods to prevent
     * external callers from adding to the state of the DeckPanel.
     * <p>
     * Removal of Widgets is supported so that WidgetCollection.WidgetIterator
     * operates as expected.
     * </p>
     * <p>
     * We ensure that the DeckPanel cannot become of of sync with its associated
     * TabBar by delegating all mutations to the TabBar to this implementation of
     * DeckPanel.
     * </p>
     */
    private static class TabbedDeckPanel extends DeckPanel {
        private UnmodifiableTabBar tabBar;

        public TabbedDeckPanel(UnmodifiableTabBar tabBar) {
            this.tabBar = tabBar;
        }

        public void add(Widget w) {
            throw new UnsupportedOperationException(
                    "Use TabPanel.add() to alter the DeckPanel");
        }

        public void clear() {
            throw new UnsupportedOperationException(
                    "Use TabPanel.clear() to alter the DeckPanel");
        }

        public void insert(Widget w, int beforeIndex) {
            throw new UnsupportedOperationException(
                    "Use TabPanel.insert() to alter the DeckPanel");
        }

        public boolean remove(Widget w) {
            // Removal of items from the TabBar is delegated to the DeckPanel
            // to ensure consistency
            int idx = getWidgetIndex(w);
            if (idx != -1) {
                tabBar.removeTabProtected(idx);
                return super.remove(w);
            }

            return false;
        }

        protected void insertProtected(Widget w, String tabText, boolean asHTML,
                                       int beforeIndex) {

            // Check to see if the TabPanel already contains the Widget. If so,
            // remove it and see if we need to shift the position to the left.
            int idx = getWidgetIndex(w);
            if (idx != -1) {
                remove(w);
                if (idx < beforeIndex) {
                    beforeIndex--;
                }
            }

            tabBar.insertTabProtected(tabText, asHTML, beforeIndex);
            super.insert(w, beforeIndex);
        }

        protected void insertProtected(Widget w, Widget tabWidget, int beforeIndex) {

            // Check to see if the TabPanel already contains the Widget. If so,
            // remove it and see if we need to shift the position to the left.
            int idx = getWidgetIndex(w);
            if (idx != -1) {
                remove(w);
                if (idx < beforeIndex) {
                    beforeIndex--;
                }
            }

            tabBar.insertTabProtected(tabWidget, beforeIndex);
            super.insert(w, beforeIndex);
        }
    };

    /**
     * This extension of TabPanel overrides the public mutator methods to prevent
     * external callers from modifying the state of the TabBar.
     */
    private static class UnmodifiableTabBar extends TabBar {
        public void insertTab(String text, boolean asHTML, int beforeIndex) {
            throw new UnsupportedOperationException(
                    "Use Pages.insert() to alter the TabBar");
        }

        public void insertTab(Widget widget, int beforeIndex) {
            throw new UnsupportedOperationException(
                    "Use Pages.insert() to alter the TabBar");
        }

        public void removeTab(int index) {
            // It's possible for removeTab() to function correctly, but it's
            // preferable to have only TabbedDeckPanel.remove() be operable,
            // especially since TabBar does not export an Iterator over its values.
            throw new UnsupportedOperationException(
                    "Use Pages.remove() to alter the TabBar");
        }

        protected void insertTabProtected(String text, boolean asHTML,
                                          int beforeIndex) {
            super.insertTab(text, asHTML, beforeIndex);
        }

        protected void insertTabProtected(Widget widget, int beforeIndex) {
            super.insertTab(widget, beforeIndex);
        }

        protected void removeTabProtected(int index) {
            super.removeTab(index);
        }
    }

    private UnmodifiableTabBar tabBar = new UnmodifiableTabBar();
    private TabbedDeckPanel deck = new TabbedDeckPanel(tabBar);
    private TabListenerCollection tabListeners;

    /**
     * Creates an empty tab panel.
     */
    public Pages() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(tabBar);
        panel.add(deck);

        // css customizations
        tabBar.setStyleName("pages-bar");

        panel.setCellHeight(deck, "100%");
        tabBar.setWidth("100%");

        tabBar.addTabListener(this);
        initWidget(panel);
        setStyleName("pages-panel");
        deck.setStyleName("pages-panel-bottom");
        //deck.getElement().setAttribute("style", "border-left:1px solid #cccccc; border-right:1px solid #cccccc; border-bottom:1px solid #cccccc");
    }

    public void add(Widget w) {
        throw new UnsupportedOperationException(
                "A tabText parameter must be specified with add().");
    }

    /**
     * Adds a widget to the tab panel. If the Widget is already attached to the
     * TabPanel, it will be moved to the right-most index.
     *
     * @param w the widget to be added
     * @param tabText the text to be shown on its tab
     */
    public void add(Widget w, String tabText) {
        insert(w, tabText, getWidgetCount());
    }

    /**
     * Adds a widget to the tab panel. If the Widget is already attached to the
     * TabPanel, it will be moved to the right-most index.
     *
     * @param w the widget to be added
     * @param tabText the text to be shown on its tab
     * @param asHTML <code>true</code> to treat the specified text as HTML
     */
    public void add(Widget w, String tabText, boolean asHTML) {
        insert(w, tabText, asHTML, getWidgetCount());
    }

    /**
     * Adds a widget to the tab panel. If the Widget is already attached to the
     * TabPanel, it will be moved to the right-most index.
     *
     * @param w the widget to be added
     * @param tabWidget the widget to be shown in the tab
     */
    public void add(Widget w, Widget tabWidget) {
        insert(w, tabWidget, getWidgetCount());
    }

    public void addTabListener(TabListener listener) {
        if (tabListeners == null) {
            tabListeners = new TabListenerCollection();
        }
        tabListeners.add(listener);
    }

    public void clear() {
        while (getWidgetCount() > 0) {
            remove(getWidget(0));
        }
    }

    /**
     * Gets the deck panel within this tab panel. Adding or removing Widgets from
     * the DeckPanel is not supported and will throw
     * UnsupportedOperationExceptions.
     *
     * @return the deck panel
     */
    public DeckPanel getDeckPanel() {
        return deck;
    }

    /**
     * Gets the tab bar within this tab panel. Adding or removing tabs from from
     * the TabBar is not supported and will throw UnsupportedOperationExceptions.
     *
     * @return the tab bar
     */
    public TabBar getTabBar() {
        return tabBar;
    }

    public Widget getWidget(int index) {
        return deck.getWidget(index);
    }

    public int getWidgetCount() {
        return deck.getWidgetCount();
    }

    public int getWidgetIndex(Widget widget) {
        return deck.getWidgetIndex(widget);
    }

    /**
     * Inserts a widget into the tab panel. If the Widget is already attached to
     * the TabPanel, it will be moved to the requested index.
     *
     * @param widget the widget to be inserted
     * @param tabText the text to be shown on its tab
     * @param asHTML <code>true</code> to treat the specified text as HTML
     * @param beforeIndex the index before which it will be inserted
     */
    public void insert(Widget widget, String tabText, boolean asHTML,
                       int beforeIndex) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        deck.insertProtected(widget, tabText, asHTML, beforeIndex);
    }

    /**
     * Inserts a widget into the tab panel. If the Widget is already attached to
     * the TabPanel, it will be moved to the requested index.
     *
     * @param widget the widget to be inserted.
     * @param tabWidget the widget to be shown on its tab.
     * @param beforeIndex the index before which it will be inserted.
     */
    public void insert(Widget widget, Widget tabWidget, int beforeIndex) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        deck.insertProtected(widget, tabWidget, beforeIndex);
    }

    /**
     * Inserts a widget into the tab panel. If the Widget is already attached to
     * the TabPanel, it will be moved to the requested index.
     *
     * @param widget the widget to be inserted
     * @param tabText the text to be shown on its tab
     * @param beforeIndex the index before which it will be inserted
     */
    public void insert(Widget widget, String tabText, int beforeIndex) {
        insert(widget, tabText, false, beforeIndex);
    }

    public Iterator iterator() {
        // The Iterator returned by DeckPanel supports removal and will invoke
        // TabbedDeckPanel.remove(), which is an active function.
        return deck.iterator();
    }

    public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
        if (tabListeners != null) {
            return tabListeners.fireBeforeTabSelected(this, tabIndex);
        }
        return true;
    }

    public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
        deck.showWidget(tabIndex);
        if (tabListeners != null) {
            tabListeners.fireTabSelected(this, tabIndex);
        }
    }

    public boolean remove(int index) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        return deck.remove(index);
    }

    /**
     * Removes the given widget, and its associated tab.
     *
     * @param widget the widget to be removed
     */
    public boolean remove(Widget widget) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        return deck.remove(widget);
    }

    public void removeTabListener(TabListener listener) {
        if (tabListeners != null) {
            tabListeners.remove(listener);
        }
    }

    /**
     * Programmatically selects the specified tab.
     *
     * @param index the index of the tab to be selected
     */
    public void selectTab(int index) {
        tabBar.selectTab(index);
    }
}