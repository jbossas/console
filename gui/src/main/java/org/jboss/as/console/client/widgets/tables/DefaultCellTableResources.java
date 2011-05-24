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