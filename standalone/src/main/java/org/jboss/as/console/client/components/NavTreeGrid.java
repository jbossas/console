package org.jboss.as.console.client.components;

import com.smartgwt.client.widgets.tree.TreeGrid;

public class NavTreeGrid extends TreeGrid {


    public NavTreeGrid(String title) {

        setTitle(title);
        setWidth100();
        setHeight100();
        setShowHeader(false);
        setCanSort(false);
        setLeaveScrollbarGap(false);
    }
}