/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
