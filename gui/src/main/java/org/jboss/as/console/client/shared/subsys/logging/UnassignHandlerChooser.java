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
public class UnassignHandlerChooser<T> extends Form<T> {
    
    private ComboBoxItem handlersItem;
    private EntityBridge<T> bridge;
    
    public UnassignHandlerChooser(Class<T> conversionType, EntityBridge<T> bridge) {
        super(conversionType);
        this.bridge = bridge;
        setNumColumns(1);
    }
    
    @Override
    public Widget asWidget() {
        handlersItem = new ComboBoxItem("handlerToUnassign", Console.CONSTANTS.subsys_logging_handlers());
        handlersItem.setRequired(true);
        setFields(handlersItem);
        return super.asWidget();
    }

    @Override
    public void edit(T bean) {
        super.edit(bean);
        this.handlersItem.setValueMap(bridge.getAssignedHandlers(bean));
        this.handlersItem.clearSelection();
    }

}
