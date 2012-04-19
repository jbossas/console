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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.general.model.LocalSocketBinding;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.shared.general.model.SocketBinding;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.ComboBox;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.StatusItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class SocketBindingView extends DisposableViewImpl implements SocketBindingPresenter.MyView {

    private SocketBindingPresenter presenter;

    private SocketList sockets;
    private RemoteSocketList remoteSockets;
    private LocalSocketList localSockets;

    @Override
    public Widget createWidget() {


        sockets = new SocketList(presenter);
        remoteSockets = new RemoteSocketList(presenter);
        localSockets = new LocalSocketList(presenter);

        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(sockets.asWidget(), "Inbound", true);
        tabLayoutpanel.add(remoteSockets.asWidget(), "Outbound Remote", true);
        tabLayoutpanel.add(localSockets.asWidget(), "Outbound Local", true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;

    }

    @Override
    public void setPresenter(SocketBindingPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateGroups(List<String> groups) {
        sockets.updateGroups(groups);
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
    public void setRemoteSockets(List<RemoteSocketBinding> entities) {
        remoteSockets.setRemoteSocketBindings(entities);
    }

    @Override
    public void setLocalSockets(List<LocalSocketBinding> entities) {
        localSockets.setLocalSocketBindings(entities);
    }
}
