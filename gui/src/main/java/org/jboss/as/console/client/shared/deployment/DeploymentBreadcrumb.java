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
package org.jboss.as.console.client.shared.deployment;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import org.jboss.as.console.client.core.EnumLabelLookup;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystemElement;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Harald Pehl
 * @date 11/29/2012
 */
public class DeploymentBreadcrumb extends Composite
{
    private final FlowPanel panel;

    public DeploymentBreadcrumb()
    {
        panel = new FlowPanel();
        initWidget(panel);
        setStyleName("console-DeploymentBreadcrumb");
    }

    public void setDeploymentData(DeploymentData deploymentData)
    {
        List<String> types = new LinkedList<String>();
        if (deploymentData instanceof DeploymentRecord)
        {
            DeploymentRecord deployment = (DeploymentRecord) deploymentData;
            collectTypes(deployment, types);
        }
        else if (deploymentData instanceof DeploymentSubsystem)
        {
            DeploymentSubsystem subsystem = (DeploymentSubsystem) deploymentData;
            collectTypes(subsystem.getDeployment(), types);
            types.add(EnumLabelLookup.labelFor("DeploymentData", subsystem.getType()));
        }
        else if (deploymentData instanceof DeploymentSubsystemElement)
        {
            DeploymentSubsystemElement element = (DeploymentSubsystemElement) deploymentData;
            collectTypes(element.getSubsystem().getDeployment(), types);
            types.add(EnumLabelLookup.labelFor("DeploymentData", element.getSubsystem().getType()));
            types.add(EnumLabelLookup.labelFor("DeploymentData", element.getType()));
        }

        panel.clear();
        for (String type : types)
        {
            Label label = new InlineLabel(type);
            label.setStyleName("console-DeploymentBreadcrumb-label");
            panel.add(label);
        }
    }

    public void empty()
    {
        panel.clear();
        Label label = new InlineLabel("Deployment");
        label.setStyleName("console-DeploymentBreadcrumb-label");
        panel.add(label);
    }

    private void collectTypes(DeploymentRecord deployment, List<String> types)
    {
        if (deployment.isSubdeployment())
        {
            types.add(EnumLabelLookup.labelFor("DeploymentData", deployment.getParent().getType()));
        }
        types.add(EnumLabelLookup.labelFor("DeploymentData", deployment.getType()));
    }
}
