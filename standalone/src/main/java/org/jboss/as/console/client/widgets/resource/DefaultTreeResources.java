package org.jboss.as.console.client.widgets.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Tree;

/**
 * Allows us to override Tree default images.
 * If we don't override one of the methods, the default will be used.
 *
 * @author Heiko Braun
 * @date 3/3/11
 *
 */
public interface DefaultTreeResources extends Tree.Resources {

    public static final DefaultTreeResources INSTANCE =  GWT.create(DefaultTreeResources.class);

    /**
     * An image indicating a closed branch.
     */
    @Source("treeClosed.png")
    ImageResource treeClosed();

    /**
     * An image indicating an open branch.
     */
    @Source("treeOpen.png")
    ImageResource treeOpen();
}
