package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTree;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellTreeResources implements CellTree.Resources {
    
    CellTree.Resources real = GWT.create(CellTree.Resources.class);
    
    @Override
    public ImageResource cellTreeClosedItem() {
        return real.cellTreeClosedItem(); 
    }

    @Override
    public ImageResource cellTreeLoading() {
        return real.cellTreeLoading();
    }

    @Override
    public ImageResource cellTreeOpenItem() {
        return real.cellTreeOpenItem();
    }

    @Override
    public ImageResource cellTreeSelectedBackground() {
        return real.cellTreeSelectedBackground();
    }

    @Override
    public CellTree.Style cellTreeStyle() {
        return new DefaultCellTreeStyle();
    }
}
