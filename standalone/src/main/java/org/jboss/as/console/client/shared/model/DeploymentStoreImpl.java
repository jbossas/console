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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/18/11
 */
public class DeploymentStoreImpl implements DeploymentStore {

  private DispatchAsync dispatcher;
  private BeanFactory factory;

  @Inject
  public DeploymentStoreImpl(DispatchAsync dispatcher, BeanFactory factory) {
    this.dispatcher = dispatcher;
    this.factory = factory;
  }

  @Override
  public void loadDomainDeployments(final AsyncCallback<List<DeploymentRecord>> callback) {
    // /:read-children-names(child-type=deployment)
    final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();
    
    ModelNode operation = new ModelNode();
    operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
    operation.get(CHILD_TYPE).set("deployment");
    
    dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

        @Override
        public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(DMRResponse result) {
          ModelNode response = ModelNode.fromBase64(result.getResponseText());

          if (response.get("result").isDefined()) {
            List<ModelNode> payload = response.get("result").asList();

            for (ModelNode name : payload) {
              DeploymentRecord rec = factory.deployment().as();
              rec.setName(name.asString());

              deployments.add(rec);
            }

          }

          callback.onSuccess(deployments);
        }
      });
  }

  @Override
  public void loadDeployments(
          final List<ServerGroupRecord> serverGroups,
          final AsyncCallback<List<DeploymentRecord>> callback) {

    // /server-group=main-server-group:read-children-names(child-type=deployment)

    // TODO: replace with composite operation
    final List<DeploymentRecord> deployments = new ArrayList<DeploymentRecord>();

    final Iterator<ServerGroupRecord> iterator = serverGroups.iterator();
    while (iterator.hasNext()) {
      final ServerGroupRecord group = iterator.next();

      ModelNode operation = new ModelNode();
      operation.get(ADDRESS).add("server-group", group.getGroupName());
      operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
      operation.get(CHILD_TYPE).set("deployment");

      dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

        @Override
        public void onFailure(Throwable caught) {
          callback.onFailure(caught);
        }

        @Override
        public void onSuccess(DMRResponse result) {
          ModelNode response = ModelNode.fromBase64(result.getResponseText());

          if (response.get("result").isDefined()) {
            List<ModelNode> payload = response.get("result").asList();

            for (ModelNode name : payload) {
              DeploymentRecord rec = factory.deployment().as();
              rec.setName(name.asString());
              rec.setServerGroup(group.getGroupName());

              deployments.add(rec);
            }

          }

          // exit if all server group are parsed
          if (!iterator.hasNext()) {
            callback.onSuccess(deployments);
          }
        }
      });
    }

  }

  @Override
  public void deleteDeployment(DeploymentRecord deploymentRecord, AsyncCallback<Boolean> callback) {
  }
}
