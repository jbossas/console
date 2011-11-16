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

import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.deployment.DeploymentViewRefresher;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class encapsulates all the data needed to refresh the DeploymentListView.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class StandaloneDeploymentInfo implements DeploymentViewRefresher {

  private DeploymentListPresenter presenter;
  private DeploymentStore deploymentStore;
  private List<DeploymentRecord> allDeployments = Collections.EMPTY_LIST;

  StandaloneDeploymentInfo(DeploymentListPresenter presenter, DeploymentStore deploymentStore) {
    this.presenter = presenter;
    this.deploymentStore = deploymentStore;
  }
  
  @Override
  public List<String> getAllDeploymentNames() {
      List<String> deploymentNames = new ArrayList<String>(allDeployments.size());
      for (DeploymentRecord record : allDeployments) {
          deploymentNames.add(record.getName());
      }
      return deploymentNames;
  }

  @Override
  public void refreshView(final DeploymentRecord... targets) {
    // load deployments
    deploymentStore.loadDeploymentContent(new SimpleCallback<List<DeploymentRecord>>() {

      @Override
      public void onSuccess(List<DeploymentRecord> result) {
        allDeployments = result;
        presenter.getView().updateDeploymentInfo(result);
      }
    });
  }
}
