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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.shared.deployment.DeploymentBrowser;
import org.jboss.as.console.client.shared.deployment.DeploymentDataCell;
import org.jboss.as.console.client.shared.deployment.DeploymentDataKeyProvider;
import org.jboss.as.console.client.shared.deployment.DeploymentDataProvider;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
import org.jboss.as.console.client.shared.deployment.model.DeploymentData;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Harald Pehl
 * @date 11/28/2012
 */
public class DeploymentNodeInfoFactory
{
    private final DeploymentBrowser deploymentBrowser;
    private final DeploymentStore deploymentStore;
    private final Map<String, DeploymentNodeInfo<? extends DeploymentData>> nodeInfos;


    public DeploymentNodeInfoFactory(final DeploymentBrowser deploymentBrowser, final DeploymentStore deploymentStore)
    {
        this.deploymentBrowser = deploymentBrowser;
        this.deploymentStore = deploymentStore;
        this.nodeInfos = new HashMap<String, DeploymentNodeInfo<? extends DeploymentData>>();
    }

    public <T extends DeploymentData> DeploymentNodeInfo<T> nodeInfoFor(T node)
    {
        DeploymentNodeInfo<T> nodeInfo = (DeploymentNodeInfo<T>) nodeInfos.get(node.getClass().getName());
        if (nodeInfo == null)
        {
            // lazily create the node infos together with the data providers and cells
            nodeInfo = createNodeInfo(node);
        }

        // set the command which executes the relevant DeploymentStore methid for the selected node.
        // the command must not be cached!
        final DeploymentNodeInfo<T> finalNodeInfo = nodeInfo;
        if (node instanceof DeploymentRecord)
        {
            final DeploymentRecord deployment = (DeploymentRecord) node;
            if (deployment.isHasSubdeployments())
            {
                nodeInfo.setCommand(new Command()
                {
                    @Override
                    public void execute()
                    {
                        deploymentStore
                                .loadSubdeployments(deployment, finalNodeInfo.dataProvider.new UpdateRowsCallback());
                    }
                });
            }
            else if (deployment.isHasSubsystems())
            {
                nodeInfo.setCommand(new Command()
                {
                    @Override
                    public void execute()
                    {
                        deploymentStore.loadSubsystems(deployment, finalNodeInfo.dataProvider.new UpdateRowsCallback());
                    }
                });
            }
        }
        else if (node instanceof DeploymentSubsystem)
        {
            final DeploymentSubsystem subsystem = (DeploymentSubsystem) node;
            switch (subsystem.getType())
            {
                case ejb3:
                    nodeInfo.setCommand(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore.loadEjbs(subsystem, finalNodeInfo.dataProvider.new UpdateRowsCallback());
                        }
                    });
                    break;
                case jpa:
                    nodeInfo.setCommand(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore.loadPersistenceUnits(subsystem,
                                    finalNodeInfo.dataProvider.new UpdateRowsCallback());
                        }
                    });
                    break;
                case web:
                    nodeInfo.setCommand(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore
                                    .loadServlets(subsystem, finalNodeInfo.dataProvider.new UpdateRowsCallback());
                        }
                    });
                    break;
                case webservices:
                    nodeInfo.setCommand(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore
                                    .loadEndpoints(subsystem, finalNodeInfo.dataProvider.new UpdateRowsCallback());
                        }
                    });
                    break;
            }
        }
        return nodeInfo;
    }

    private <T extends DeploymentData> DeploymentNodeInfo<T> createNodeInfo(final T node)
    {
        DeploymentNodeInfo<T> nodeInfo = null;
        if (node instanceof DeploymentRecord)
        {
            DeploymentRecord deployment = (DeploymentRecord) node;
            if (deployment.isHasSubdeployments())
            {
                nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeploymentRecord>(
                        new DeploymentDataProvider<DeploymentRecord>(),
                        new DeploymentDataCell<DeploymentRecord>(deploymentBrowser));
                cache(nodeInfo, node.getClass().getName() + "#subdeployments");
            }
            else if (deployment.isHasSubsystems())
            {
                nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeploymentSubsystem>(
                        new DeploymentDataProvider<DeploymentSubsystem>(),
                        new DeploymentDataCell<DeploymentSubsystem>(deploymentBrowser));
                cache(nodeInfo, node.getClass().getName() + "#subsystems");
            }
        }
        else if (node instanceof DeploymentSubsystem)
        {
            // level 2/3: return the contents of the selected subsystem
            final DeploymentSubsystem subsystem = (DeploymentSubsystem) node;
            switch (subsystem.getType())
            {
                case ejb3:
                    nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeployedEjb>(
                            new DeploymentDataProvider<DeployedEjb>(),
                            new DeploymentDataCell<DeployedEjb>(deploymentBrowser));
                    break;
                case jpa:
                    nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeployedPersistenceUnit>(
                            new DeploymentDataProvider<DeployedPersistenceUnit>(),
                            new DeploymentDataCell<DeployedPersistenceUnit>(deploymentBrowser));
                    break;
                case web:
                    nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeployedServlet>(
                            new DeploymentDataProvider<DeployedServlet>(),
                            new DeploymentDataCell<DeployedServlet>(deploymentBrowser));
                    break;
                case webservices:
                    nodeInfo = (DeploymentNodeInfo<T>) new DeploymentNodeInfo<DeployedEndpoint>(
                            new DeploymentDataProvider<DeployedEndpoint>(),
                            new DeploymentDataCell<DeployedEndpoint>(deploymentBrowser));
                    break;
            }
            cache(nodeInfo, node);
        }
        return nodeInfo;
    }

    private <T extends DeploymentData> void cache(final DeploymentNodeInfo<?> nodeInfo, final T node)
    {
        cache(nodeInfo, node.getClass().getName());
    }

    private <T extends DeploymentData> void cache(final DeploymentNodeInfo<T> nodeInfo, final String key)
    {
        nodeInfos.put(key, nodeInfo);
    }


    static class DeploymentNodeInfo<T extends DeploymentData> extends TreeViewModel.DefaultNodeInfo<T>
    {
        final DeploymentDataProvider dataProvider;


        public DeploymentNodeInfo(final DeploymentDataProvider<T> dataProvider,
                final Cell<T> cell)
        {
            super(dataProvider, cell, new SingleSelectionModel<T>(new DeploymentDataKeyProvider<T>()), null);
            this.dataProvider = dataProvider;
        }

        void setCommand(Command command)
        {
            dataProvider.exec(command);
        }
    }
}
