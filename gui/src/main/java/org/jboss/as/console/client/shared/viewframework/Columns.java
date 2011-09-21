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
package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.icons.Icons;

/**
 * Helpful Column classes for types known to the View Framework.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class Columns {

    // dont allow instance
    private Columns() {
    } 

    public static class NameColumn extends TextColumn<NamedEntity> {
        public static final String LABEL = Console.CONSTANTS.common_label_name();
        
        @Override
        public String getValue(NamedEntity record) {
            return record.getName();
        }
    }

    public static class EnabledColumn extends Column<EnabledEntity, ImageResource> {
        public static final String LABEL = Console.CONSTANTS.common_label_enabled();
        
        public EnabledColumn() {
            super(new ImageResourceCell());
        }

        @Override
        public ImageResource getValue(EnabledEntity entity) {
            ImageResource res = null;

            if (entity.isEnabled()) {
                res = Icons.INSTANCE.statusGreen_small();
            } else {
                res = Icons.INSTANCE.statusRed_small();
            }

            return res;
        }
    }
}
