package org.jboss.as.console.client.components;

import com.google.gwt.user.client.History;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class NavTreeGrid extends TreeGrid {


    public NavTreeGrid(String title) {

        setTitle(title);
        setWidth100();
        setHeight100();
        setShowHeader(false);
        setCanSort(false);
        setLeaveScrollbarGap(false);

        addCellClickHandler(new CellClickHandler() {
            @Override
            public void onCellClick(CellClickEvent event) {
                final TreeNode selectedRecord = (TreeNode) getSelectedRecord();

                /*Console.MODULES.getPlaceManager().revealPlaceHierarchy(
                        new ArrayList<PlaceRequest>() {{
                            add(new PlaceRequest("domain"));
                            add(new PlaceRequest(selectedRecord.getName()));
                        }}
                );*/

                // TODO: by convention for now
                History.newItem(selectedRecord.getName().toLowerCase());

            }
        });

    }
}