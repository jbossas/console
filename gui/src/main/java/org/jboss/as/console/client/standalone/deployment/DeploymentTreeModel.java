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

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystemElement;
import org.jboss.ballroom.client.widgets.icons.Icons;

import java.util.List;

/**
 * @author Harald Pehl
 * @date 11/23/2012
 */
public class DeploymentTreeModel implements TreeViewModel
{
    static final DeploymentTemplates DEPLOYMENT_TEMPLATES = GWT.create(DeploymentTemplates.class);

    private final DeploymentNodeInfoFactory nodeInfoFactory;
    private final ListDataProvider<DeploymentRecord> deploymentDataProvider;
    private final DefaultNodeInfo<DeploymentRecord> level0;


    public DeploymentTreeModel(final DeploymentBrowserPresenter presenter, final DeploymentStore deploymentStore)
    {
        this.nodeInfoFactory = new DeploymentNodeInfoFactory(presenter, deploymentStore);
        this.deploymentDataProvider = new ListDataProvider<DeploymentRecord>();
        this.level0 = new DefaultNodeInfo<DeploymentRecord>(deploymentDataProvider, new MainDeploymentCell(),
                new DeploymentDataSelectionModel<DeploymentRecord>(presenter), null);
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(final T value)
    {
        if (value == null)
        {
            return level0;
        }
        else
        {
            return nodeInfoFactory.nodeInfoFor(value);
        }
    }

    @Override
    public boolean isLeaf(final Object value)
    {
        return value instanceof DeploymentSubsystemElement;
    }

    public void updateDeployments(List<DeploymentRecord> deployments)
    {
        deploymentDataProvider.setList(deployments);
    }


    interface DeploymentTemplates extends SafeHtmlTemplates
    {
        @Template(
                "<div style=\"padding-right:20px;position:relative;zoom:1;\"><div>{0}</div><div style=\"margin-top:-9px;position:absolute;top:50%;right:0;line-height:0px;\">{1}</div></div>")
        SafeHtml deployment(String depployment, SafeHtml icon);
    }


    private static class MainDeploymentCell extends AbstractCell<DeploymentRecord>
    {
        @Override
        public void render(final Context context, final DeploymentRecord value, final SafeHtmlBuilder sb)
        {
            ImageResource res = null;
            if ("FAILED".equalsIgnoreCase(value.getStatus()))
            {
                res = Icons.INSTANCE.status_warn();
            }
            else if (value.isEnabled())
            {
                res = Icons.INSTANCE.status_good();
            }
            else
            {
                res = Icons.INSTANCE.status_bad();
            }
            AbstractImagePrototype proto = AbstractImagePrototype.create(res);
            SafeHtml imageHtml = SafeHtmlUtils.fromTrustedString(proto.getHTML());
            sb.append(DEPLOYMENT_TEMPLATES.deployment(value.getName(), imageHtml));
        }
    }
}
