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
package org.jboss.as.console.client.domain.groups.deployment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.domain.model.ServerGroupStore;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.model.DeploymentStore;

/**
 * This class encapsulates all the data needed to refresh the DeploymentsOverview.
 * Right now, it always gets all deployment data whenever a refresh is called.  At some
 * point this needs to be refactored so that it only gets the specific data needed.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class DomainDeploymentInfo {

  private DeploymentsPresenter.MyView view;
  private ServerGroupStore serverGroupStore;
  private List<ServerGroupRecord> serverGroupRecords;
  private DeploymentStore deploymentStore;
  private List<String> serverGroupNames = Collections.EMPTY_LIST;
  private List<DeploymentRecord> domainDeployments = Collections.EMPTY_LIST;
  private Map<String, List<DeploymentRecord>> serverGroupDeployments = Collections.EMPTY_MAP;

  DomainDeploymentInfo(DeploymentsPresenter.MyView view, ServerGroupStore serverGroupStore, DeploymentStore deploymentStore) {
    this.view = view;
    this.serverGroupStore = serverGroupStore;
    this.deploymentStore = deploymentStore;
  }

  List<String> getServerGroupNames() {
    return this.serverGroupNames;
  }

  List<DeploymentRecord> getDomainDeployments() {
    return this.domainDeployments;
  }

  Map<String, List<DeploymentRecord>> getServerGroupDeployments() {
    return this.serverGroupDeployments;
  }

  void refreshView() {
    serverGroupStore.loadServerGroups(new SimpleCallback<List<ServerGroupRecord>>() {

      @Override
      public void onSuccess(List<ServerGroupRecord> serverGroups) {
        DomainDeploymentInfo.this.serverGroupRecords = serverGroups;
        List<String> groupNames = new ArrayList();
        for (ServerGroupRecord record : serverGroups) {
          groupNames.add(record.getGroupName());
        }

        DomainDeploymentInfo.this.serverGroupNames = groupNames;
        
        // load deployments
        deploymentStore.loadDeployments(serverGroups, new SimpleCallback<List<DeploymentRecord>>() {

          @Override
          public void onSuccess(List<DeploymentRecord> result) {
            
            // initialize HashMap
            Map<String,List<DeploymentRecord>> serverGroupDeployments = new HashMap<String, List<DeploymentRecord>>();
            for(String groupName : DomainDeploymentInfo.this.serverGroupNames) {
              serverGroupDeployments.put(groupName, new ArrayList());
            }
            
            // sort records
            for(DeploymentRecord record : result) {
              List<DeploymentRecord> deploymentList = serverGroupDeployments.get(record.getServerGroup());
              deploymentList.add(record);
            }
            
            DomainDeploymentInfo.this.serverGroupDeployments = serverGroupDeployments;
            
            deploymentStore.loadDomainDeployments(new SimpleCallback<List<DeploymentRecord>>() {
              @Override
              public void onSuccess(List<DeploymentRecord> result) {
                DomainDeploymentInfo.this.domainDeployments = result;
                DomainDeploymentInfo.this.view.updateDeploymentInfo(DomainDeploymentInfo.this);
              }
            });
          }
        });
      }
    });
  }
}
