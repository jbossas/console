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

package org.jboss.as.console.client.domain.hosts.general;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.general.InterfaceEditor;
import org.jboss.as.console.client.shared.general.InterfaceManagement;
import org.jboss.as.console.client.shared.general.model.Interface;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class HostInterfacesView extends DisposableViewImpl implements HostInterfacesPresenter.MyView{

    private HostInterfacesPresenter presenter;

    private InterfaceEditor editor;


    public HostInterfacesView() {
        this.editor = new InterfaceEditor("Host Interfaces");
        editor.setDescription("Specific rules to bind interfaces on a host. A server configuration will reference an interface by name.");
    }

    @Override
    public Widget createWidget() {
        return editor.asWidget();
    }

    @Override
    public void setPresenter(HostInterfacesPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDelegate(InterfaceManagement delegate) {
        editor.setPresenter(delegate);
    }

    @Override
    public void setInterfaces(List<Interface> interfaces) {
       editor.setInterfaces(interfaces);
    }
}
