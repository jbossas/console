/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.widgets.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
public class VerticalTabLayoutPanel {

    private HorizontalPanel layout;
    private VerticalPanel tabContainer;
    private List<TabDelegate> tabs = new ArrayList<TabDelegate>();

    private DeckPanel decks;

    public VerticalTabLayoutPanel() {
        super();

        layout = new HorizontalPanel();
        layout.setStyleName("fill-layout");

        this.tabContainer = new VerticalPanel();
        this.tabContainer.setStyleName("vertical-tabpanel-tabs");
        this.decks = new DeckPanel();

        layout.add(tabContainer);
        layout.add(decks);

        decks.getElement().getParentElement().setAttribute("width", "100%");
    }


    public void add(Widget widget, String name) {
        decks.add(widget);
        TabDelegate tab = new TabDelegate(name, decks.getWidgetCount() - 1);
        tabs.add(tab);
        tabContainer.add(tab);
    }

    public Widget asWidget() {
        return layout;
    }

    class TabDelegate extends HTML {

        int index;

        TabDelegate(String html, final int index) {
            super(html);
            this.index = index;

            setStyleName("vertical-tabpanel-tab");
            addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {

                    demoteAll();
                    addStyleName("vertical-tabpanel-tab-selected");
                    decks.showWidget(index);
                }
            });
        }
    }

    private void demoteAll() {
        for(TabDelegate tab : tabs)
            tab.removeStyleName("vertical-tabpanel-tab-selected");
    }



}
