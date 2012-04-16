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

import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.model.DeploymentRecord;

import java.util.List;

/**
 * Implementers of this interface carry out sending the actual command to the
 * server.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public interface DeployCommandExecutor {
  /**
   * Toggle the enabled/disabled flag on the deployment.
   *
   * @param record The deployment.
   */
  public void enableDisableDeployment(DeploymentRecord record);

  /**
   * Update the deployment.
   *
   * @param record The deployment.
   */
  public void updateDeployment(DeploymentRecord record);

  /**
   * Remove a deployment form its server group.  The record must contain
   * the server group it is to be removed from.
   *
   * @param record The deployment.
   * @throws UnsupportedOperationException if in standalone mode.
   */
  public void removeDeploymentFromGroup(DeploymentRecord record);

  /**
   * Add the deployment to a server group.
   *
   * @param record The deployment.
   * @param enable Enable after adding to group.
   * @param selectedGroups The selected server groups.
   * @throws UnsupportedOperationException if in standalone mode.
   */
  public void addToServerGroup(DeploymentRecord record, boolean enable, String... selectedGroups);

  /**
   * Remove a deployment from the server.
   *
   * @param record The deployment.
   */
  public void removeContent(DeploymentRecord record);

  /**
   * Get the server groups that a deployment might be assigned to.  This returns all
   * known server groups except those that the deployment is already assigned to.
   * @param record The deployment.
   * @return The server groups that the deployment could be assigned to.
   */
  public List<ServerGroupRecord> getPossibleGroupAssignments(DeploymentRecord record);

  /**
   * Display a dialog for selecting server groups that the deployment could be assigned to.  The
   * prompt will also submit the selections for execution.
   *
   * @param record The deployment record.
   */
  public void promptForGroupSelections(DeploymentRecord record);
}
