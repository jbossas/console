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

import com.google.gwt.user.client.Command;
import com.google.gwt.view.client.TreeViewModel;
import org.jboss.as.console.client.shared.deployment.DeploymentStore;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
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
    private final DeploymentBrowserPresenter presenter;
    private final DeploymentStore deploymentStore;
    private final Map<String, TreeViewModel.NodeInfo> nodeInfos;


    public DeploymentNodeInfoFactory(final DeploymentBrowserPresenter presenter, final DeploymentStore deploymentStore)
    {
        this.presenter = presenter;
        this.deploymentStore = deploymentStore;
        this.nodeInfos = new HashMap<String, TreeViewModel.NodeInfo>();
    }

    public <T> TreeViewModel.NodeInfo<T> nodeInfoFor(T node)
    {
        TreeViewModel.NodeInfo<T> nodeInfo = nodeInfos.get(node.getClass().getName());
        if (nodeInfo == null)
        {
            if (node instanceof DeploymentRecord)
            {
                final DeploymentRecord deployment = (DeploymentRecord) node;
                if (deployment.isHasSubdeployments())
                {
                    final DeploymentDataProvider<DeploymentRecord> dataProvider = new DeploymentDataProvider<DeploymentRecord>();
                    dataProvider.exec(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore.loadSubdeployments(deployment, dataProvider.new UpdateRowsCallback());
                        }
                    });
                    nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeploymentRecord>(
                            dataProvider, new DeploymentDataCell<DeploymentRecord>(),
                            new DeploymentDataSelectionModel<DeploymentRecord>(presenter), null);
                    cache(nodeInfo, node.getClass().getName() + "#subdeployments");
                }
                else if (deployment.isHasSubsystems())
                {
                    // level 1 or 2: return the subsystems
                    final DeploymentDataProvider<DeploymentSubsystem> dataProvider = new DeploymentDataProvider<DeploymentSubsystem>();
                    dataProvider.exec(new Command()
                    {
                        @Override
                        public void execute()
                        {
                            deploymentStore.loadSubsystems(deployment, dataProvider.new UpdateRowsCallback());
                        }
                    });
                    nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeploymentSubsystem>(
                            dataProvider, new DeploymentDataCell<DeploymentSubsystem>(),
                            new DeploymentDataSelectionModel<DeploymentSubsystem>(presenter), null);
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
                    {
                        final DeploymentDataProvider<DeployedEjb> dataProvider = new DeploymentDataProvider<DeployedEjb>();
                        dataProvider.exec(new Command()
                        {
                            @Override
                            public void execute()
                            {
                                deploymentStore.loadEjbs(subsystem, dataProvider.new UpdateRowsCallback());
                            }
                        });
                        nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeployedEjb>(
                                dataProvider, new DeploymentDataCell<DeployedEjb>(),
                                new DeploymentDataSelectionModel<DeployedEjb>(presenter), null);
                        cache(nodeInfo, node);
                        break;
                    }
                    case jpa:
                    {
                        final DeploymentDataProvider<DeployedPersistenceUnit> dataProvider = new DeploymentDataProvider<DeployedPersistenceUnit>();
                        dataProvider.exec(new Command()
                        {
                            @Override
                            public void execute()
                            {
                                deploymentStore.loadPersistenceUnits(subsystem, dataProvider.new UpdateRowsCallback());
                            }
                        });
                        nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeployedPersistenceUnit>(
                                dataProvider, new DeploymentDataCell<DeployedPersistenceUnit>(),
                                new DeploymentDataSelectionModel<DeployedPersistenceUnit>(presenter), null);
                        cache(nodeInfo, node);
                        break;
                    }
                    case web:
                    {
                        final DeploymentDataProvider<DeployedServlet> dataProvider = new DeploymentDataProvider<DeployedServlet>();
                        dataProvider.exec(new Command()
                        {
                            @Override
                            public void execute()
                            {
                                deploymentStore.loadServlets(subsystem, dataProvider.new UpdateRowsCallback());
                            }
                        });
                        nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeployedServlet>(
                                dataProvider, new DeploymentDataCell<DeployedServlet>(),
                                new DeploymentDataSelectionModel<DeployedServlet>(presenter), null);
                        cache(nodeInfo, node);
                        break;
                    }
                    case webservices:
                    {
                        final DeploymentDataProvider<DeployedEndpoint> dataProvider = new DeploymentDataProvider<DeployedEndpoint>();
                        dataProvider.exec(new Command()
                        {
                            @Override
                            public void execute()
                            {
                                deploymentStore.loadEndpoints(subsystem, dataProvider.new UpdateRowsCallback());
                            }
                        });
                        nodeInfo = (TreeViewModel.NodeInfo<T>) new TreeViewModel.DefaultNodeInfo<DeployedEndpoint>(
                                dataProvider, new DeploymentDataCell<DeployedEndpoint>(),
                                new DeploymentDataSelectionModel<DeployedEndpoint>(presenter), null);
                        cache(nodeInfo, node);
                        break;
                    }
                }
            }
        }
        return nodeInfo;
    }

    private <T> void cache(final TreeViewModel.NodeInfo<T> nodeInfo, final T node)
    {
        cache(nodeInfo, node.getClass().getName());
    }

    private <T> void cache(final TreeViewModel.NodeInfo<T> nodeInfo, final String key)
    {
        nodeInfos.put(key, nodeInfo);
    }
}
