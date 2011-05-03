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
package org.jboss.as.console.client.server.deployment;

import java.util.Collections;
import java.util.List;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.deployment.DeploymentViewRefresher;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;

/**
 * This class encapsulates all the data needed to refresh the DeploymentListView.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class StandaloneDeploymentInfo implements DeploymentViewRefresher {

  private DeploymentListPresenter presenter;
  private DeploymentStore deploymentStore;
  private List<DeploymentRecord> deployments = Collections.EMPTY_LIST;

  StandaloneDeploymentInfo(DeploymentListPresenter presenter, DeploymentStore deploymentStore) {
    this.presenter = presenter;
    this.deploymentStore = deploymentStore;
  }

  List<DeploymentRecord> getDeployments() {
    return this.deployments;
  }

  public void refreshView() {
    // load deployments
    deploymentStore.loadDeploymentContent(new SimpleCallback<List<DeploymentRecord>>() {

      @Override
      public void onSuccess(List<DeploymentRecord> result) {
        presenter.getView().updateDeploymentInfo(result);
      }
    });
  }
}
