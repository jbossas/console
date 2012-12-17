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
package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.HostInformationStore;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.ContentRepository;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;

/**
 * @author Harald Pehl
 * @date 12/12/2012
 */
public class DomainDeploymentView extends SuspendableViewImpl implements DomainDeploymentPresenter.MyView
{
    private final DeploymentStore deploymentStore;
    private final HostInformationStore hostInfoStore;
    private ContentRepositoryPanel contentRepositoryPanel;
    private ServerGroupDeploymentPanel serverGroupDeploymentPanel;

    @Inject
    public DomainDeploymentView(DeploymentStore deploymentStore, HostInformationStore hostInfoStore)
    {
        this.deploymentStore = deploymentStore;
        this.hostInfoStore = hostInfoStore;
    }

    @Override
    public Widget createWidget()
    {
        DefaultTabLayoutPanel tabLayoutPanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");
        tabLayoutPanel.add(contentRepositoryPanel, "Content Repository", true);
        tabLayoutPanel.add(serverGroupDeploymentPanel, "Server Groups", true);
        return tabLayoutPanel;
    }

    @Override
    public void setPresenter(final DomainDeploymentPresenter presenter)
    {
        // As long as the presenter is application scoped, this should only be called once
        this.contentRepositoryPanel = new ContentRepositoryPanel(presenter);
        this.serverGroupDeploymentPanel = new ServerGroupDeploymentPanel(presenter, deploymentStore, hostInfoStore);
    }

    @Override
    public void reset(final ContentRepository contentRepository)
    {
        contentRepositoryPanel.reset(contentRepository);
        serverGroupDeploymentPanel.reset(contentRepository);
    }
}
