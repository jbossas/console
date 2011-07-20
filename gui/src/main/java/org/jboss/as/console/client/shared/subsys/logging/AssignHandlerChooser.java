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
package org.jboss.as.console.client.shared.subsys.logging;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;

/**
 * A Form that allows the user to choose a Handler.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class AssignHandlerChooser<T> extends Form<T> {
    
    private ComboBoxItem availableHandlersItem;
    
    public AssignHandlerChooser(Class<T> conversionType) {
        super(conversionType);
        setNumColumns(1);
    }
    
    @Override
    public Widget asWidget() {
        this.availableHandlersItem = new ComboBoxItem("handlerToAssign", Console.CONSTANTS.subsys_logging_handlers());
        this.availableHandlersItem.setRequired(true);
        setFields(availableHandlersItem);
        return super.asWidget();
    }

    /**
     * Update the Handlers that the user can choose.
     * 
     * @param handlers The Handlers.
     */
    public void updateAvailableHandlers(String[] handlers) {
        this.availableHandlersItem.setValueMap(handlers);
    }
}
