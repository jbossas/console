package org.jboss.as.console.client.widgets.lists;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellListResources implements CellList.Resources {

    CellList.Resources real = GWT.create(CellList.Resources.class);

    @Override
    public ImageResource cellListSelectedBackground() {
        return real.cellListSelectedBackground();
    }

    @Override
    public CellList.Style cellListStyle() {
        return new DefaultCellListStyle();
    }
}
