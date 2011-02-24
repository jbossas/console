package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
 public class DefaultCellTableResources implements CellTable.Resources
	{
		CellTable.Resources real = GWT.create(CellTable.Resources.class);
        
		public ImageResource cellTableFooterBackground() {
			return real.cellTableFooterBackground();
		}

        @Override
        public ImageResource cellTableHeaderBackground() {
            return real.cellTableHeaderBackground(); 
        }

        @Override
        public ImageResource cellTableLoading() {
            return real.cellTableLoading();
        }

        @Override
        public ImageResource cellTableSelectedBackground() {
            return real.cellTableSelectedBackground();
        }

        @Override
        public ImageResource cellTableSortAscending() {
            return real.cellTableSortAscending();
        }

        @Override
        public ImageResource cellTableSortDescending() {
            return real.cellTableSortDescending();
        }

        public com.google.gwt.user.cellview.client.CellTable.Style cellTableStyle() {
			return new DefaultCellTableStyle();
		}
	}