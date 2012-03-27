package org.jboss.as.console.client.widgets.pages;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.jboss.as.console.client.Console;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/6/11
 */
public class PagedView {

    private DeckPanel deck;
    private LinkBar bar;
    private boolean navOnFirstPage = false;
    private Widget navigationBar;
    private List<PageCallback> callbacks = new LinkedList<PageCallback>();

    public PagedView(boolean navOnFirstPage) {
        this.navOnFirstPage = navOnFirstPage;

        deck = new DeckPanel();
        deck.addStyleName("fill-layout");
        bar = new LinkBar();
    }

    public PagedView() {
        this(false);
    }

    public Widget asWidget() {

        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("fill-layout-width");

        navigationBar = bar.asWidget();
        navigationBar.addStyleName("paged-view-navigation");
        layout.add(navigationBar);
        layout.add(deck);

        layout.setWidgetTopHeight(navigationBar, 2, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(deck, 30, Style.Unit.PX, 100, Style.Unit.PCT);

        navigationBar.setVisible(navOnFirstPage);

        return layout;
    }

    public void addPage(String title, Widget pageWidget)
    {
        deck.add(pageWidget);
        final int index = deck.getWidgetCount()-1;

        bar.addLink(title, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showPage(index);
            }
        });
    }

    public void showPage(int index) {


        // notify callbacks
        for(PageCallback callback : callbacks)
            callback.onRevealPage(index);


        if(!navOnFirstPage && navigationBar!=null)
        {
            // navigation only on subsequent pages
            if(index>0)
            {
                navigationBar.setVisible(true);
            }
            else
            {
                navigationBar.setVisible(false);
            }
        }

         // TODO: clear history tokens
        if(index==0)
        {
            PlaceManager placeManager = Console.getPlaceManager();
            String nameToken = placeManager.getCurrentPlaceRequest().getNameToken();
            History.newItem(nameToken, false);
        }

        deck.showWidget(index);
        bar.setActive(index);
    }

    public void addPageCallback(PageCallback callback) {
        callbacks.add(callback);
    }

    public int getPage() {
        return deck.getVisibleWidget();
    }

    public interface PageCallback {
        void onRevealPage(int index);
    }
}
