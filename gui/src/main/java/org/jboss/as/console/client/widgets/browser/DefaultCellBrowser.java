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
package org.jboss.as.console.client.widgets.browser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellBrowser;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.TreeViewModel;

import static com.google.gwt.resources.client.ImageResource.ImageOptions;

/**
 * @author Harald Pehl
 * @date 11/23/2012
 */
public class DefaultCellBrowser extends CellBrowser
{
    public static final int DEFAILT_PAGE_SIZE=  10;
    public static final int MINIMUM_COLUMN_WIDTH = 50;
    public static final String DEFAULT_HEIGHT = "200px";
    public static final Resources DEFAULT_RESOURCES = GWT.create(Resources.class);

    protected <T> DefaultCellBrowser(final Builder<T> builder)
    {
        super(builder);
        setHeight(DEFAULT_HEIGHT);
        setMinimumColumnWidth(MINIMUM_COLUMN_WIDTH);
    }

    @Override
    protected <C> Widget createPager(final HasData<C> display)
    {
        // by returning null we can prevent the "flickering" of the "show more" link
        // see http://stackoverflow.com/a/6827755/1538056
        return null;
    }

    public static class Builder<T> extends CellBrowser.Builder
    {
        public Builder(final TreeViewModel viewModel, final Object rootValue)
        {
            super(viewModel, rootValue);
            resources(DEFAULT_RESOURCES);
            pageSize(DEFAILT_PAGE_SIZE);
        }

        @Override
        public DefaultCellBrowser build()
        {
            return new DefaultCellBrowser(this);
        }
    }


    interface Resources extends CellBrowser.Resources
    {
        @Override
        @Source("blank.png")
        @ImageOptions(width = 8)
        ImageResource cellBrowserOpen();

        @Override
        @Source("blank.png")
        @ImageOptions(width = 8)
        ImageResource cellBrowserClosed();

        @Override
        @Source("defaultCellBrowser.css")
        Style cellBrowserStyle();
    }

    interface Style extends CellBrowser.Style
    {
        String cellBrowserKeyboardSelectedItem();
        String cellBrowserOpenItem();
        String cellBrowserSelectedItem();
        String cellBrowserWidget();
    }
}
