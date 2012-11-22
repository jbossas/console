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

package org.jboss.as.console.client.standalone.deployment;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;


/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/14/11
 */
public class DeploymentBrowserView extends SuspendableViewImpl implements DeploymentBrowserPresenter.MyView
{
    private DeploymentBrowserPresenter presenter;
    private DefaultCellTable<DeploymentRecord> deploymentTable;
    private ListDataProvider<DeploymentRecord> dataProvider;
    private DeploymentFilter filter;

    @Override
    public Widget createWidget()
    {
        DeploymentBrowser browser = new DeploymentBrowser();

        SimpleLayout layout = new SimpleLayout()
                .setTitle(Console.CONSTANTS.common_label_deployments())
                .setHeadline(Console.CONSTANTS.common_label_deployments())
                .setDescription("Currently deployed application components.")
                .addContent("browser", browser.asWidget());
        return layout.build();
    }

    @Override
    public void setPresenter(DeploymentBrowserPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public void updateDeploymentInfo(List<DeploymentRecord> deployments)
    {
    }
}
