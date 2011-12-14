package org.jboss.as.console.client.widgets.tree;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.view.client.TreeViewModel;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
public class DefaultCellTree extends CellTree{


    private final static DefaultCellTreeResources RESOURCES = new DefaultCellTreeResources();

    public DefaultCellTree(TreeViewModel treeModel, String root) {
        super(treeModel, root, RESOURCES);
    }


}
