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
package org.jboss.as.console.client.shared.model;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @author Stan Silvert
 * @date 3/18/11
 */
public class DeploymentStoreImpl implements DeploymentStore {

    private DispatchAsync dispatcher;
    private BeanFactory factory;
    private boolean isStandalone ;
    @Inject
    public DeploymentStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationProperties bootstrap) {
        this.dispatcher = dispatcher;
        this.factory = factory;
        this.isStandalone = bootstrap.getProperty(ApplicationProperties.STANDALONE).equals("true");
    }

    @Override
    public void loadDeploymentContent(final AsyncCallback<List<DeploymentRecord>> callback) {
        // /:read-children-resources(child-type=deployment)
        final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set("deployment");

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (response.get("result").isDefined()) {
                    List<ModelNode> payload = response.get("result").asList();

                    for (ModelNode item : payload) {
                        Property property = item.asProperty();
                        ModelNode handler = property.getValue().asObject();
                        String name = property.getName();

                        try {
                            DeploymentRecord rec = factory.deployment().as();
                            rec.setName(name);
                            rec.setRuntimeName(handler.get("runtime-name").asString());
                            if (isStandalone) rec.setEnabled(handler.get("enabled").asBoolean());
                            if (!isStandalone) rec.setEnabled(true);
                            if (isStandalone) rec.setPersistent(handler.get("persistent").asBoolean());
                            if (!isStandalone) rec.setPersistent(true);
                            rec.setServerGroup(null);
                            deployments.add(rec);
                        } catch (IllegalArgumentException e) {
                            Log.error("Failed to parse data source representation", e);
                        }
                    }

                }

                callback.onSuccess(deployments);
            }
        });
    }

    @Override
    public void loadServerGroupDeployments(final AsyncCallback<List<DeploymentRecord>> callback) {

        // /server-group=*/deployment=*/:read-resource
        final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).add("server-group", "*");
        operation.get(ADDRESS).add("deployment", "*");
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (!response.get("result").isDefined()) {
                    callback.onFailure(new Exception("Unexpected dmr result=" + response.toString()));
                }

                List<ModelNode> payload = response.get("result").asList();
                for (ModelNode deployment : payload) {
                    String serverGroup = deployment.get("address").asList().get(0).get("server-group").asString();
                    ModelNode resultNode = deployment.get("result");
                    try {
                        DeploymentRecord rec = factory.deployment().as();
                        rec.setName(resultNode.get("name").asString());
                        rec.setServerGroup(serverGroup);
                        rec.setRuntimeName(resultNode.get("runtime-name").asString());
                        rec.setEnabled(resultNode.get("enabled").asBoolean());
                        rec.setPersistent(true);
                        deployments.add(rec);
                    } catch (IllegalArgumentException e) {
                        Log.error("Failed to parse data source representation", e);
                    }
                }

                callback.onSuccess(deployments);
            }
        });
    }

    @Override
    public void removeContent(DeploymentRecord deploymentRecord, AsyncCallback<DMRResponse> callback) {
        doDeploymentCommand(makeOperation("remove", null, deploymentRecord), callback);
    }

    @Override
    public void removeDeploymentFromGroup(DeploymentRecord deployment,
                                          AsyncCallback<DMRResponse> callback) {
        doDeploymentCommand(makeOperation("remove", deployment.getServerGroup(), deployment), callback);
    }

    @Override
    public void enableDisableDeployment(DeploymentRecord deployment,
                                        final AsyncCallback<DMRResponse> callback) {
        String command = "deploy";
        if (deployment.isEnabled()) {
            command = "undeploy";
        }
        doDeploymentCommand(makeOperation(command, deployment.getServerGroup(), deployment), callback);
    }

    @Override
    public void addToServerGroups(String[] serverGroups,
                                  boolean enable,
                                  DeploymentRecord deploymentRecord,
                                  AsyncCallback<DMRResponse> callback) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(COMPOSITE);
        operation.get(ADDRESS).setEmptyList();

        List<ModelNode> steps = new ArrayList<ModelNode>();

        for(String group : serverGroups)
        {
            steps.add(makeOperation(ADD, group, deploymentRecord));
            if (enable) {
                steps.add(makeOperation("deploy", group, deploymentRecord));
            }
        }

        operation.get(STEPS).set(steps);

        doDeploymentCommand(operation, callback);
    }

    private ModelNode makeOperation(String command, String serverGroup, DeploymentRecord deployment) {
        ModelNode operation = new ModelNode();
        if ((serverGroup != null) && (!serverGroup.equals(""))) {
            operation.get(ADDRESS).add("server-group", serverGroup);
        }

        operation.get(ADDRESS).add("deployment", deployment.getName());
        operation.get(OP).set(command);
        return operation;
    }

    private void doDeploymentCommand(ModelNode operation,
                                     final AsyncCallback<DMRResponse> callback) {
        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback) {
    }
}
