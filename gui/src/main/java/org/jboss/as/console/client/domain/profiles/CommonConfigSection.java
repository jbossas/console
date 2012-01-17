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

package org.jboss.as.console.client.domain.profiles;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

/**
 * @author Heiko Braun
 * @date 2/15/11
 */
class CommonConfigSection {

    private DisclosurePanel panel;
    private LHSNavTree commonTree;

    public CommonConfigSection() {
        super();

        panel = new DisclosureStackPanel(Console.CONSTANTS.common_label_generalConfig()).asWidget();
        commonTree = new LHSNavTree("profiles");
        panel.setContent(commonTree);

        //LHSNavTreeItem paths = new LHSNavTreeItem(Console.CONSTANTS.common_label_paths(), "domain/paths");
        LHSNavTreeItem interfaces = new LHSNavTreeItem(Console.CONSTANTS.common_label_interfaces(), NameTokens.InterfacePresenter);
        LHSNavTreeItem sockets = new LHSNavTreeItem(Console.CONSTANTS.common_label_socketBinding(), NameTokens.SocketBindingPresenter);
        LHSNavTreeItem properties = new LHSNavTreeItem(Console.CONSTANTS.common_label_systemProperties(), NameTokens.PropertiesPresenter);

        //commonTree.addItem(paths);
        commonTree.addItem(interfaces);
        commonTree.addItem(sockets);
        commonTree.addItem(properties);
    }

    public Widget asWidget() {
        return panel;
    }

}
