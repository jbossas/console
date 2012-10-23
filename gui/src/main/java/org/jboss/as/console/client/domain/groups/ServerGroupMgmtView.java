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

package org.jboss.as.console.client.domain.groups;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ServerGroupMgmtView extends SuspendableViewImpl implements ServerGroupMgmtPresenter.MyView {


    private ServerGroupMgmtPresenter presenter;
    private SplitLayoutPanel layout;
    private LayoutPanel contentCanvas;
    private LHSServerGroupNavigation lhsNavigation;

    public ServerGroupMgmtView() {
        super();

        layout = new SplitLayoutPanel(10);

        contentCanvas = new LayoutPanel();
        lhsNavigation = new LHSServerGroupNavigation();

        layout.addWest(lhsNavigation.asWidget(), 197);
        layout.add(contentCanvas);
    }


    @Override
    public void setPresenter(ServerGroupMgmtPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        return layout;
    }

     @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == ServerGroupMgmtPresenter.TYPE_MainContent) {
            if(content!=null)
                setContent(content);

        }
    }

    private void setContent(Widget newContent) {
        contentCanvas.clear();
        contentCanvas.add(newContent);
    }

    @Override
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {
        lhsNavigation.updateFrom(serverGroupRecords);
    }
}
