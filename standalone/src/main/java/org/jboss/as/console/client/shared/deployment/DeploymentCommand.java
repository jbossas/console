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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.widgets.Feedback;

/**
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public enum DeploymentCommand {
    ENABLE_DISABLE("enable/disable", "Enable or Disable", "for"),
    REMOVE_FROM_GROUP("remove from group", "Remove", "from"),
    ADD_TO_GROUP("add to selected server group", "Add", "to"),
    REMOVE_CONTENT("remove content", "Remove", "from");
    
    private String label;
    private String verb;
    private String preposition;
    
    private DeploymentCommand(String label, String verb, String preposition) {
      this.label = label;
      this.verb = verb;
      this.preposition = preposition;
    }
    
    public void execute(DeployCommandDelegate delegate, DeploymentRecord record) {
      String target = record.getServerGroup();
      if (this == ADD_TO_GROUP) target = delegate.getSelectedServerGroup();
      confirm(delegate, record, target);
    }
    
    private void confirm(final DeployCommandDelegate delegate, final DeploymentRecord record, String target) {
      Feedback.confirm("Are you sure?", confirmMessage(record, target), new Feedback.ConfirmationHandler() {
        @Override
        public void onConfirmation(boolean isConfirmed) {
          if (isConfirmed) doCommand(delegate, record);
        }
      });
    }
    
    private String confirmMessage(DeploymentRecord record, String target) {
      String action = verb;
      if (this == ENABLE_DISABLE && record.isEnabled()) action = "Disable";
      if (this == ENABLE_DISABLE && !record.isEnabled()) action = "Enable";
      return action + " " + record.getName() + " " + preposition + " " + target + ".";
    }
    
    private void doCommand(DeployCommandDelegate delegate, DeploymentRecord record) {
      String selectedGroup = delegate.getSelectedServerGroup();
      switch (this) {
        case ENABLE_DISABLE: delegate.enableDisableDeployment(record);       break;
        case REMOVE_FROM_GROUP: delegate.removeDeploymentFromGroup(record);  break;
        case ADD_TO_GROUP: delegate.addToServerGroup(selectedGroup, record); break;
        case REMOVE_CONTENT: delegate.removeContent(record);                 break;
      }
    }
    
    public void displaySuccessMessage(DeploymentRecord record, String target) {
      Console.MODULES.getMessageCenter().notify(
                new Message("Success: " + confirmMessage(record, target), Message.Severity.Info)
        );
    }
    
    public void displayFailureMessage(DeploymentRecord record, String target, Throwable t) {
      Console.MODULES.getMessageCenter().notify(
                new Message("Failure: " + confirmMessage(record, target) + "; " + t.getMessage(), Message.Severity.Error)
        );
    }
    
    public String getLabel() {
      return this.label;
    }
  }
