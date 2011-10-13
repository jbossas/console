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

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HelpWidgetFactory {
    private HelpWidgetFactory() {} // don't allow instance
    
    public static Widget makeHelpWidget(final AddressBinding address, FormAdapter form) {
        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {

                    @Override
                    public org.jboss.dmr.client.ModelNode getAddress() {
                        String wildCards[] = new String[address.getNumWildCards()];
                        for (int i = 0; i < wildCards.length; i++) {
                            wildCards[i] = "*";
                        }

                        ModelNode addressAsResource = address.asResource(wildCards);
                        ModelNode addressNode = Baseadress.get();
                        for (Property address : addressAsResource.get(ADDRESS).asPropertyList()) {
                            addressNode.add(address.getName(), address.getValue());
                        }

                        return addressNode;
                    }
                }, form);

        return helpPanel.asWidget();
    }
}
