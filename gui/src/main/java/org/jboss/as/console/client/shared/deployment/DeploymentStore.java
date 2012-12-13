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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.deployment.model.ContentRepository;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
import org.jboss.as.console.client.shared.deployment.model.DeploymentDataType;
import org.jboss.as.console.client.shared.deployment.model.DeploymentEjbSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentJpaSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebserviceSubsystem;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.model.ModelAdapter;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.jboss.as.console.client.shared.deployment.model.DeploymentDataType.*;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Responsible for loading deployment data
 * and turning it a usable representation.
 *
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 1/31/11
 */
public class DeploymentStore
{
    private final ServerGroupStore serverGroupStore;
    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final boolean isStandalone;
    private final EntityAdapter<DeploymentRecord> deploymentEntityAdapter;
    private final EntityAdapter<DeploymentEjbSubsystem> deploymentEjbSubsystemEntityAdapter;
    private final EntityAdapter<DeploymentJpaSubsystem> deploymentJpaSubsystemEntityAdapter;
    private final EntityAdapter<DeploymentWebserviceSubsystem> deploymentWebserviceSubsystemEntityAdapter;
    private final EntityAdapter<DeploymentWebSubsystem> deploymentWebSubsystemnEntityAdapter;
    private final EntityAdapter<DeployedEjb> deployedEjbEntityAdapter;
    private final EntityAdapter<DeployedPersistenceUnit> deployedPersistenceUnitEntityAdapter;
    private final EntityAdapter<DeployedEndpoint> deployedEndpointEntityAdapter;
    private final EntityAdapter<DeployedServlet> deployedServletEntityAdapter;
    private final EntityAdapter<ServerGroupRecord> serverGroupRecordEntityAdapter;


    @Inject
    public DeploymentStore(ServerGroupStore serverGroupStore, DispatchAsync dispatcher, BeanFactory factory,
            ApplicationProperties bootstrap, ApplicationMetaData applicationMetaData)
    {
        this.serverGroupStore = serverGroupStore;
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.isStandalone = bootstrap.getProperty(ApplicationProperties.STANDALONE).equals("true");

        deploymentEntityAdapter = new EntityAdapter<DeploymentRecord>(DeploymentRecord.class, applicationMetaData);
        deploymentEjbSubsystemEntityAdapter = new EntityAdapter<DeploymentEjbSubsystem>(DeploymentEjbSubsystem.class,
                applicationMetaData);
        deployedEjbEntityAdapter = new EntityAdapter<DeployedEjb>(DeployedEjb.class, applicationMetaData);
        deploymentJpaSubsystemEntityAdapter = new EntityAdapter<DeploymentJpaSubsystem>(DeploymentJpaSubsystem.class,
                applicationMetaData);
        deployedPersistenceUnitEntityAdapter = new EntityAdapter<DeployedPersistenceUnit>(DeployedPersistenceUnit.class,
                applicationMetaData);
        deploymentWebserviceSubsystemEntityAdapter = new EntityAdapter<DeploymentWebserviceSubsystem>(
                DeploymentWebserviceSubsystem.class, applicationMetaData);
        deployedEndpointEntityAdapter = new EntityAdapter<DeployedEndpoint>(DeployedEndpoint.class,
                applicationMetaData);
        deploymentWebSubsystemnEntityAdapter = new EntityAdapter<DeploymentWebSubsystem>(DeploymentWebSubsystem.class,
                applicationMetaData);
        deployedServletEntityAdapter = new EntityAdapter<DeployedServlet>(DeployedServlet.class, applicationMetaData);
        serverGroupRecordEntityAdapter = new EntityAdapter<ServerGroupRecord>(ServerGroupRecord.class,
                applicationMetaData);
    }


    // ------------------------------------------------------ content repository

    /**
     * Maps the server groups to the assigned deployments. If there are no assigned deployments for a server group
     * an empty list is provided.
     *
     * @throws IllegalStateException if called in standalone mode
     */
    public void loadContentRepository(final AsyncCallback<ContentRepository> callback)
    {
        if (isStandalone)
        {
            throw new IllegalStateException(
                    "DeploymentStore.loadContentRepository() must not be called in standalone mode!");
        }
        final ContentRepository contentRepository = new ContentRepository();

        // load deployments, server groups and assignments in one composite
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).setEmptyList();
        operation.get(OP).set(COMPOSITE);
        List<ModelNode> steps = new LinkedList<ModelNode>();

        ModelNode deploymentsOp = new ModelNode();
        deploymentsOp.get(ADDRESS).setEmptyList();
        deploymentsOp.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        deploymentsOp.get(CHILD_TYPE).set("deployment");
        steps.add(deploymentsOp);

        ModelNode serverGroupsOp = new ModelNode();
        serverGroupsOp.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        serverGroupsOp.get(CHILD_TYPE).set("server-group");
        steps.add(serverGroupsOp);

        ModelNode assignmentOp = new ModelNode();
        assignmentOp.get(ADDRESS).add("server-group", "*");
        assignmentOp.get(ADDRESS).add("deployment", "*");
        assignmentOp.get(OP).set(READ_RESOURCE_OPERATION);
        steps.add(assignmentOp);

        operation.get(STEPS).set(steps);
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>()
        {
            @Override
            public void onFailure(final Throwable caught)
            {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(final DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    ModelNode stepsResult = response.get(RESULT);
                    List<ModelNode> nodes = stepsResult.get("step-1").get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        DeploymentRecord deployment = mapDeployment(emptyAddress(), null, node);
                        contentRepository.addDeployment(deployment);
                    }
                    nodes = stepsResult.get("step-2").get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        Property property = node.asProperty();
                        ModelNode serverGroupNode = property.getValue().asObject();
                        ServerGroupRecord serverGroup = serverGroupRecordEntityAdapter.fromDMR(serverGroupNode);
                        serverGroup.setName(property.getName());
                        contentRepository.addServerGroup(serverGroup);
                    }
                    nodes = stepsResult.get("step-3").get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        String groupName = node.get(ADDRESS).asList().get(0).get("server-group").asString();
                        String deploymentName = node.get(ADDRESS).asList().get(1).get("deployment").asString();
                        // The state of the deployment (enabled/disabled) is taken from this step!
                        DeploymentRecord dr = contentRepository.getDeployment(deploymentName);
                        dr.setEnabled(node.get(RESULT).get("enabled").asBoolean());
                        contentRepository.assignDeploymentToServerGroup(deploymentName, groupName);
                    }
                }
                callback.onSuccess(contentRepository);
            }
        });
    }


    // ------------------------------------------------------ deployment related methods

    public void loadDeployments(final AsyncCallback<List<DeploymentRecord>> callback)
    {
        loadDeployments(emptyAddress(), callback);
    }

    public void loadDeployments(final ModelNode baseAddress, final AsyncCallback<List<DeploymentRecord>> callback)
    {
        // /<baseAddress>/:read-children-resources(child-type=deployment)
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(baseAddress);
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("deployment");
        loadDeployments(baseAddress, operation, null, callback);
    }

    public void loadSubdeployments(final DeploymentRecord deployment,
            final AsyncCallback<List<DeploymentRecord>> callback)
    {
        // /<relativeTo>/deployment=<deployment.getName()>:read-children-resources(child-type=subdeployment)
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(deployment.getBaseAddress());
        operation.get(ADDRESS).add("deployment", deployment.getName());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("subdeployment");
        loadDeployments(deployment.getBaseAddress(), operation, deployment, callback);
    }

    private void loadDeployments(final ModelNode baseAddress, final ModelNode operation, final DeploymentRecord parent,
            final AsyncCallback<List<DeploymentRecord>> callback)
    {
        final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    List<ModelNode> nodes = response.get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        deployments.add(mapDeployment(baseAddress, parent, node));
                    }
                }
                callback.onSuccess(deployments);
            }
        });
    }

    private DeploymentRecord mapDeployment(final ModelNode baseAddress, final DeploymentRecord parent, final ModelNode node)
    {
        ModelNode deploymentNode = node.asProperty().getValue().asObject();
        DeploymentRecord deployment = deploymentEntityAdapter.fromDMR(deploymentNode);
        deployment.setName(node.asProperty().getName()); // for subdeployments
        deployment.setType(parent == null ? DeploymentDataType.deployment : subdeployment);
        try
        {
            ModelNode property = deploymentNode.get("content");
            if (property.isDefined())
            {
                List<ModelNode> contentList = deploymentNode.get("content").asList();
                if (!contentList.isEmpty())
                {
                    // TODO: strange concept (list.size() always 1)
                    ModelNode content = contentList.get(0);
                    if (content.has("path")) { deployment.setPath(content.get("path").asString()); }
                    if (content.has("relative-to")) { deployment.setRelativeTo(content.get("relative-to").asString()); }
                    if (content.has("archive")) { deployment.setArchive(content.get("archive").asBoolean()); }
                    if (content.has("hash")) { deployment.setSha(content.get("hash").asString()); }
                }
            }
            deployment.setParent(parent);
            deployment.setSubdeployment(parent != null);
            deployment.setHasSubdeployments(deploymentNode.get("subdeployment").isDefined());
            deployment.setHasSubsystems(deploymentNode.get("subsystem").isDefined());
            deployment.setBaseAddress(baseAddress);
        }
        catch (IllegalArgumentException e)
        {
            Log.error("Failed to parse data source representation", e);
        }
        return deployment;
    }

    public void loadSubsystems(final DeploymentRecord deployment,
            final AsyncCallback<List<DeploymentSubsystem>> callback)
    {
        final List<DeploymentSubsystem> subsystems = new ArrayList<DeploymentSubsystem>();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(deployment.getBaseAddress());
        if (deployment.isSubdeployment())
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subdeployment=<subdeployment>:read-children-resources(child-type=subsystems)
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>:read-children-resources(child-type=subsystems)
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    List<ModelNode> nodes = response.get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        DeploymentSubsystem subsystem = null;
                        Property property = node.asProperty();
                        String name = property.getName();
                        ModelNode subsystemNode = property.getValue().asObject();
                        DeploymentDataType type = DeploymentDataType.valueOf(name);
                        switch (type)
                        {
                            case ejb3:
                                subsystem = deploymentEjbSubsystemEntityAdapter.fromDMR(subsystemNode);
                                break;
                            case jpa:
                                subsystem = deploymentJpaSubsystemEntityAdapter.fromDMR(subsystemNode);
                                break;
                            case web:
                                subsystem = deploymentWebSubsystemnEntityAdapter.fromDMR(subsystemNode);
                                break;
                            case webservices:
                                subsystem = deploymentWebserviceSubsystemEntityAdapter.fromDMR(subsystemNode);
                                break;
                        }
                        if (subsystem != null)
                        {
                            subsystem.setName(name);
                            subsystem.setType(type);
                            subsystem.setDeployment(deployment);
                            subsystems.add(subsystem);
                        }
                    }
                }
                callback.onSuccess(subsystems);
            }
        });
    }

    public void loadEjbs(final DeploymentSubsystem subsystem, final AsyncCallback<List<DeployedEjb>> callback)
    {
        final int stepCount = 5;
        final List<DeployedEjb> ejbs = new ArrayList<DeployedEjb>();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();
        List<ModelNode> steps = new LinkedList<ModelNode>();
        steps.add(ejbOp(subsystem, "entity-bean"));
        steps.add(ejbOp(subsystem, "message-driven-bean"));
        steps.add(ejbOp(subsystem, "singleton-bean"));
        steps.add(ejbOp(subsystem, "stateless-session-bean"));
        steps.add(ejbOp(subsystem, "stateful-session-bean"));
        operation.get(STEPS).set(steps);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    ModelNode steps = response.get(RESULT);
                    for (int i = 1; i <= stepCount; i++)
                    {
                        List<ModelNode> nodes = steps.get("step-" + i).get(RESULT).asList();
                        for (ModelNode node : nodes)
                        {
                            if (ModelAdapter.wasSuccess(node))
                            {
                                DeployedEjb ejb = deployedEjbEntityAdapter.fromDMR(node.get(RESULT));
                                List<ModelNode> address = node.get(ADDRESS).asList();
                                Property property = address.get(address.size() - 1).asProperty();
                                String ejbName = property.getValue().asString();
                                ejb.setName(ejbName);
                                ejb.setSubsystem(subsystem);
                                String beanType = property.getName();
                                if ("entity-bean".equals(beanType))
                                {
                                    ejb.setType(entityBean);
                                }
                                else if ("message-driven-bean".equals(beanType))
                                {
                                    ejb.setType(messageDrivenBean);
                                }
                                else if ("singleton-bean".equals(beanType))
                                {
                                    ejb.setType(singletonBean);
                                }
                                else if ("stateless-session-bean".equals(beanType))
                                {
                                    ejb.setType(statelessSessionBean);
                                }
                                else if ("stateful-session-bean".equals(beanType))
                                {
                                    ejb.setType(statefulSessionBean);
                                }
                                ejbs.add(ejb);
                            }
                        }
                    }
                }
                callback.onSuccess(ejbs);
            }
        });
    }

    private ModelNode ejbOp(final DeploymentSubsystem subsystem, final String name)
    {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(subsystem.getDeployment().getBaseAddress());
        DeploymentRecord deployment = subsystem.getDeployment();
        if (deployment.isSubdeployment())
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subdeployment=<subdeployment>/subsystem=ejb3/<name>=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subsystem=ejb3/<name>=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(ADDRESS).add("subsystem", subsystem.getName()).add(name, "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);
        return operation;
    }

    public void loadPersistenceUnits(final DeploymentSubsystem subsystem,
            final AsyncCallback<List<DeployedPersistenceUnit>> callback)
    {
        final List<DeployedPersistenceUnit> pus = new ArrayList<DeployedPersistenceUnit>();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(subsystem.getDeployment().getBaseAddress());
        DeploymentRecord deployment = subsystem.getDeployment();
        if (deployment.isSubdeployment())
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subdeployment=<subdeployment>/subsystem=jpa/hibernate-persistence-unit=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subsystem=jpa/hibernate-persistence-unit=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(ADDRESS).add("subsystem", "jpa").add("hibernate-persistence-unit", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    List<ModelNode> nodes = response.get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        if (ModelAdapter.wasSuccess(node))
                        {
                            List<ModelNode> address = node.get(ADDRESS).asList();
                            String name = address.get(address.size() - 1).asProperty().getValue().asString();
                            int index = name.indexOf("#");
                            if (index != -1)
                            {
                                name = name.substring(index + 1);
                            }
                            ModelNode puNode = node.get(RESULT);
                            DeployedPersistenceUnit pu = deployedPersistenceUnitEntityAdapter.fromDMR(puNode);
                            pu.setName(name);
                            pu.setType(persistenceUnit);
                            pu.setSubsystem(subsystem);
                            if (puNode.get("entity").isDefined())
                            {
                                List<ModelNode> entityNodes = puNode.get("entity").asList();
                                List<String> names = new ArrayList<String>(entityNodes.size());
                                for (ModelNode entityNode : entityNodes)
                                {
                                    String entityName = entityNode.asProperty().getName();
                                    names.add(entityName);
                                }
                                pu.setEntities(names);
                            }
                            pus.add(pu);
                        }
                    }
                }
                callback.onSuccess(pus);
            }
        });
    }

    public void loadServlets(final DeploymentSubsystem subsystem, final AsyncCallback<List<DeployedServlet>> callback)
    {
        final List<DeployedServlet> servlets = new ArrayList<DeployedServlet>();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(subsystem.getDeployment().getBaseAddress());
        DeploymentRecord deployment = subsystem.getDeployment();
        if (deployment.isSubdeployment())
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subdeployment=<subdeployment>/subsystem=web/servlet=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subsystem=web/servlet=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(ADDRESS).add("subsystem", "web").add("servlet", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    List<ModelNode> nodes = response.get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        if (ModelAdapter.wasSuccess(node))
                        {
                            ModelNode servletNode = node.get(RESULT);
                            DeployedServlet servlet = deployedServletEntityAdapter.fromDMR(servletNode);
                            servlet.setName(servletNode.get("servlet-name").asString());
                            servlet.setType(DeploymentDataType.servlet);
                            servlet.setSubsystem(subsystem);
                            servlets.add(servlet);
                        }
                    }
                }
                callback.onSuccess(servlets);
            }
        });
    }

    public void loadEndpoints(final DeploymentSubsystem subsystem, final AsyncCallback<List<DeployedEndpoint>> callback)
    {
        final List<DeployedEndpoint> endpoints = new ArrayList<DeployedEndpoint>();

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(subsystem.getDeployment().getBaseAddress());
        DeploymentRecord deployment = subsystem.getDeployment();
        if (deployment.isSubdeployment())
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subdeployment=<subdeployment>/subsystem=webservices/servlet=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /<deployment.getBaseAddress()>/deployment=<deployment>/subsystem=web/servlet=*:read-resource
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(ADDRESS).add("subsystem", "webservices").add("endpoint", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>()
        {
            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (ModelAdapter.wasSuccess(response))
                {
                    List<ModelNode> nodes = response.get(RESULT).asList();
                    for (ModelNode node : nodes)
                    {
                        if (ModelAdapter.wasSuccess(node))
                        {
                            ModelNode endpointNode = node.get(RESULT);
                            DeployedEndpoint endpoint = deployedEndpointEntityAdapter.fromDMR(endpointNode);
                            endpoint.setType(webserviceEndpoint);
                            endpoint.setSubsystem(subsystem);
                            endpoints.add(endpoint);
                        }
                    }
                }
                callback.onSuccess(endpoints);
            }
        });
    }


    // ------------------------------------------------------ unrelated

    private ModelNode emptyAddress() {return new ModelNode().get(ADDRESS).setEmptyList();}

    @Deprecated
    public void loadServerGroupDeploymentsAsList(final AsyncCallback<List<DeploymentRecord>> callback)
    {
        // /server-group=*/deployment=*/:read-resource
        final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("server-group", "*");
        operation.get(ADDRESS).add("deployment", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result)
            {
                ModelNode response = result.get();
                if (!response.get(RESULT).isDefined())
                {
                    callback.onFailure(new Exception("Unexpected dmr result=" + response.toString()));
                }

                List<ModelNode> payload = response.get(RESULT).asList();
                for (ModelNode deployment : payload)
                {
                    String serverGroup = deployment.get("address").asList().get(0).get("server-group").asString();
                    ModelNode resultNode = deployment.get(RESULT);
                    DeploymentRecord rec = factory.deployment().as();
                    rec.setName(resultNode.get("name").asString());
                    rec.setType(DeploymentDataType.deployment);
                    rec.setSubdeployment(false);
                    rec.setServerGroup(serverGroup);
                    rec.setRuntimeName(resultNode.get("runtime-name").asString());
                    rec.setEnabled(resultNode.get("enabled").asBoolean());
                    rec.setPersistent(true);
                    deployments.add(rec);
                }
                callback.onSuccess(deployments);
            }
        });
    }

    public void removeContent(DeploymentRecord deploymentRecord, AsyncCallback<DMRResponse> callback)
    {
        doDeploymentCommand(makeOperation("remove", null, deploymentRecord), callback);
    }

    public void removeDeploymentFromGroup(DeploymentRecord deployment,
            AsyncCallback<DMRResponse> callback)
    {
        doDeploymentCommand(makeOperation("remove", deployment.getServerGroup(), deployment), callback);
    }

    public void enableDisableDeployment(DeploymentRecord deployment,
            final AsyncCallback<DMRResponse> callback)
    {
        String command = "deploy";
        if (deployment.isEnabled())
        {
            command = "undeploy";
        }
        doDeploymentCommand(makeOperation(command, deployment.getServerGroup(), deployment), callback);
    }

    public void addToServerGroups(Set<String> serverGroups,
            boolean enable,
            DeploymentRecord deploymentRecord,
            AsyncCallback<DMRResponse> callback)
    {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        for (String group : serverGroups)
        {
            steps.add(makeOperation(ADD, group, deploymentRecord));
            if (enable)
            {
                steps.add(makeOperation("deploy", group, deploymentRecord));
            }
        }

        operation.get(STEPS).set(steps);

        doDeploymentCommand(operation, callback);
    }

    private ModelNode makeOperation(String command, String serverGroup, DeploymentRecord deployment)
    {
        ModelNode operation = new ModelNode();
        if ((serverGroup != null) && (!serverGroup.equals("")))
        {
            operation.get(ADDRESS).add("server-group", serverGroup);
        }

        operation.get(ADDRESS).add("deployment", deployment.getName());
        operation.get(OP).set(command);
        return operation;
    }

    private void doDeploymentCommand(ModelNode operation,
            final AsyncCallback<DMRResponse> callback)
    {
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>()
        {

            @Override
            public void onFailure(Throwable caught)
            {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result)
            {
                callback.onSuccess(result);
            }
        });
    }

    public void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback)
    {
    }
}
