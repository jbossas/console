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

package org.jboss.as.console.client.shared.general;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.general.model.LocalSocketBinding;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class SocketBindingView extends DisposableViewImpl implements SocketBindingPresenter.MyView {

    private SocketBindingPresenter presenter;

    private SocketList sockets;
    private RemoteSocketList remoteSockets;
    private LocalSocketList localSockets;
    private PagedView panel;
    private SocketGroupList socketGroups;

    @Override
    public Widget createWidget() {

        socketGroups = new SocketGroupList(presenter, NameTokens.SocketBindingPresenter);
        sockets = new SocketList(presenter);
        remoteSockets = new RemoteSocketList(presenter);
        localSockets = new LocalSocketList(presenter);

        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Socket Bindings");
        layout.add(titleBar);

        panel = new PagedView();

        panel.addPage(Console.CONSTANTS.common_label_back(), socketGroups.asWidget());
        panel.addPage("Inbound", sockets.asWidget()) ;
        panel.addPage("Outbound Remote", remoteSockets.asWidget()) ;
        panel.addPage("Outbound Local", localSockets.asWidget()) ;


        // default page
        panel.showPage(0);


        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(panelWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;

    }

    @Override
    public void setPresenter(SocketBindingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelectedGroup(String selectedGroup) {


        if(null==selectedGroup)
        {
            panel.showPage(0);
        }
        else{

            presenter.loadDetails(selectedGroup);

            // move to first page if still showing overview
            if(0==panel.getPage())
                panel.showPage(1);
        }
    }

    @Override
    public void updateGroups(List<String> groups) {
        socketGroups.setGroups(groups);
    }

    @Override
    public void setBindings(String groupName, List<SocketBinding> bindings) {
        sockets.setBindings(groupName, bindings);
    }

    @Override
    public void setEnabled(boolean b) {
        sockets.setEnabled(b);
    }

    @Override
    public void setRemoteSockets(String groupName, List<RemoteSocketBinding> entities) {
        remoteSockets.setRemoteSocketBindings(groupName, entities);
    }

    @Override
    public void setLocalSockets(String groupName, List<LocalSocketBinding> entities) {
        localSockets.setLocalSocketBindings(groupName, entities);
    }
}
