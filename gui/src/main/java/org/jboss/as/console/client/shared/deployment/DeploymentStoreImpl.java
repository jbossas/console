/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.deployment.model.DeployedEjb;
import org.jboss.as.console.client.shared.deployment.model.DeployedEndpoint;
import org.jboss.as.console.client.shared.deployment.model.DeployedPersistenceUnit;
import org.jboss.as.console.client.shared.deployment.model.DeployedServlet;
import org.jboss.as.console.client.shared.deployment.model.DeploymentEjbSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentJpaSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.as.console.client.shared.deployment.model.DeploymentSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebSubsystem;
import org.jboss.as.console.client.shared.deployment.model.DeploymentWebserviceSubsystem;
import org.jboss.as.console.client.shared.deployment.model.SubsystemType;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/18/11
 */
public class DeploymentStoreImpl implements DeploymentStore
{
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


    @Inject
    public DeploymentStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationProperties bootstrap,
            ApplicationMetaData applicationMetaData)
    {
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
    }

    @Override
    public void loadDeployments(final AsyncCallback<List<DeploymentRecord>> callback)
    {
        // TODO Domain mode
        // /:read-children-resources(child-type=deployment)

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("deployment");
        loadDeployments(operation, null, callback);
    }

    @Override
    public void loadSubdeployments(final DeploymentRecord deployment,
            final AsyncCallback<List<DeploymentRecord>> callback)
    {
        // TODO Domain mode
        // /deployment=<deployment.getName()>:read-children-resources(child-type=subdeployment)

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("deployment", deployment.getName());
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("subdeployment");
        loadDeployments(operation, deployment, callback);
    }

    private void loadDeployments(final ModelNode operation, final DeploymentRecord parent,
            final AsyncCallback<List<DeploymentRecord>> callback)
    {
        final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();
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
                if (response.get("result").isDefined())
                {
                    List<ModelNode> nodes = response.get("result").asList();
                    for (ModelNode node : nodes)
                    {
                        deployments.add(mapDeployment(parent, node));
                    }
                }
                callback.onSuccess(deployments);
            }
        });
    }

    private DeploymentRecord mapDeployment(final DeploymentRecord parent, final ModelNode node)
    {
        ModelNode deploymentNode = node.asProperty().getValue().asObject();
        DeploymentRecord deployment = deploymentEntityAdapter.fromDMR(deploymentNode);
        deployment.setName(node.asProperty().getName()); // for subdeployments
        try
        {
            if (!isStandalone)
            {
                deployment.setEnabled(true);
                deployment.setPersistent(true);
            }
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
        }
        catch (IllegalArgumentException e)
        {
            Log.error("Failed to parse data source representation", e);
        }
        return deployment;
    }

    @Override
    public void loadSubsystems(final DeploymentRecord deployment,
            final AsyncCallback<List<DeploymentSubsystem>> callback)
    {
        // TODO Domain mode!
        final List<DeploymentSubsystem> subsystems = new ArrayList<DeploymentSubsystem>();

        ModelNode operation = new ModelNode();
        if (deployment.isSubdeployment())
        {
            // /deployment=<deployment>/subdeployment=<subdeployment>:read-children-resources(child-type=subsystems)
            operation.get(ADDRESS).add("deployment", deployment.getParent().getName())
                    .add("subdeployment", deployment.getName());
        }
        else
        {
            // /deployment=<deployment>:read-children-resources(child-type=subsystems)
            operation.get(ADDRESS).add("deployment", deployment.getName());
        }
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("subsystem");

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
                if (response.get("result").isDefined())
                {
                    List<ModelNode> nodes = response.get("result").asList();
                    for (ModelNode node : nodes)
                    {
                        DeploymentSubsystem subsystem = mapSubsystem(deployment, node);
                        subsystems.add(subsystem);
                    }
                }
                callback.onSuccess(subsystems);
            }
        });
    }

    private DeploymentSubsystem mapSubsystem(final DeploymentRecord deployment, ModelNode node)
    {
        DeploymentSubsystem subsystem = null;
        Property property = node.asProperty();
        String name = property.getName();
        ModelNode subsystemNode = property.getValue().asObject();
        SubsystemType type = SubsystemType.valueOf(name);
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
        subsystem.setName(name);
        subsystem.setType(type);
        subsystem.setDeployment(deployment);
        return subsystem;
    }

    @Override
    public void loadEjbs(final DeploymentEjbSubsystem subsystem,
            final AsyncCallback<List<DeployedEjb>> callback)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadPersistenceUnits(final DeploymentJpaSubsystem subsystem,
            final AsyncCallback<List<DeployedPersistenceUnit>> callback)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadServlets(final DeploymentWebSubsystem subsystemn,
            final AsyncCallback<List<DeployedServlet>> callback)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadEndpoints(final DeploymentWebserviceSubsystem subsystem,
            final AsyncCallback<List<DeployedEndpoint>> callback)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadServerGroupDeployments(final AsyncCallback<List<DeploymentRecord>> callback)
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
                if (!response.get("result").isDefined())
                {
                    callback.onFailure(new Exception("Unexpected dmr result=" + response.toString()));
                }

                List<ModelNode> payload = response.get("result").asList();
                for (ModelNode deployment : payload)
                {
                    String serverGroup = deployment.get("address").asList().get(0).get("server-group").asString();
                    ModelNode resultNode = deployment.get("result");
                    try
                    {
                        DeploymentRecord rec = factory.deployment().as();
                        rec.setName(resultNode.get("name").asString());
                        rec.setServerGroup(serverGroup);
                        rec.setRuntimeName(resultNode.get("runtime-name").asString());
                        rec.setEnabled(resultNode.get("enabled").asBoolean());
                        rec.setPersistent(true);
                        deployments.add(rec);
                    }
                    catch (IllegalArgumentException e)
                    {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                callback.onSuccess(deployments);
            }
        });
    }

    @Override
    public void removeContent(DeploymentRecord deploymentRecord, AsyncCallback<DMRResponse> callback)
    {
        doDeploymentCommand(makeOperation("remove", null, deploymentRecord), callback);
    }

    @Override
    public void removeDeploymentFromGroup(DeploymentRecord deployment,
            AsyncCallback<DMRResponse> callback)
    {
        doDeploymentCommand(makeOperation("remove", deployment.getServerGroup(), deployment), callback);
    }

    @Override
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

    @Override
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

    @Override
    public void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback)
    {
    }
}
