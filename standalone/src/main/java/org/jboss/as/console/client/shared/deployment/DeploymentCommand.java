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

    ENABLE_DISABLE(new EnableDisableMessageMaker()),
    REMOVE_FROM_GROUP(new RemoveMessageMaker("server group")),
    ADD_TO_GROUP(new AddToGroupMessageMaker()),
    REMOVE_FROM_DOMAIN(new RemoveMessageMaker("domain")),
    REMOVE_FROM_STANDALONE(new RemoveMessageMaker("server"));
    
    private MessageMaker messageMaker;

    private DeploymentCommand(MessageMaker messageMaker) {
        this.messageMaker = messageMaker;
    }

    public void execute(DeployCommandExecutor executor, DeploymentRecord record) {
        String target = record.getServerGroup();
        if (this == ADD_TO_GROUP) {
            target = executor.getSelectedServerGroup();
        }
        confirm(executor, record, target);
    }

    private void confirm(final DeployCommandExecutor executor, final DeploymentRecord record, String target) {
        Feedback.confirm("Are you sure?", messageMaker.makeConfirmMessage(record), new Feedback.ConfirmationHandler() {

            @Override
            public void onConfirmation(boolean isConfirmed) {
                if (isConfirmed) {
                    doCommand(executor, record);
                }
            }
        });
    }

    private void doCommand(DeployCommandExecutor executor, DeploymentRecord record) {
        String selectedGroup = executor.getSelectedServerGroup();
        switch (this) {
            case ENABLE_DISABLE:
                executor.enableDisableDeployment(record);
                break;
            case REMOVE_FROM_GROUP:
                executor.removeDeploymentFromGroup(record);
                break;
            case ADD_TO_GROUP:
                executor.addToServerGroup(selectedGroup, record);
                break;
            case REMOVE_FROM_DOMAIN:
                executor.removeContent(record);
                break;
            case REMOVE_FROM_STANDALONE:
                executor.removeContent(record);
                break;
        }
    }

    public void displaySuccessMessage(DeploymentRecord record) {
        Console.MODULES.getMessageCenter().notify(
                new Message(messageMaker.makeSuccessMessage(record), Message.Severity.Info));
    }

    public void displayFailureMessage(DeploymentRecord record, Throwable t) {
        Console.MODULES.getMessageCenter().notify(
                new Message(messageMaker.makeFailureMessage(record) + "; " + t.getMessage(), Message.Severity.Error));
    }

    public String getLabel(DeploymentRecord record) {
        return this.messageMaker.makeLabel(record);
    }

    private interface MessageMaker {
        String makeLabel(DeploymentRecord record);
        String makeSuccessMessage(DeploymentRecord record);
        String makeFailureMessage(DeploymentRecord record);
        String makeConfirmMessage(DeploymentRecord record);
    }

    private static class RemoveMessageMaker implements MessageMaker {

        private String target;
        
        RemoveMessageMaker(String target) {
            this.target = target;
        }
        
        @Override
        public String makeConfirmMessage(DeploymentRecord record) {
            return "Remove " + record.getName() + " from " + target + "?";
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record) {
            return "Failed to remove " + record.getName() + " from " + target + ".";
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            return "Remove";
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record) {
            return "Removed " + record.getName() + " from " + target + ".";
        }
    }
    
    private static class EnableDisableMessageMaker implements MessageMaker {

        @Override
        public String makeConfirmMessage(DeploymentRecord record) {
            return makeLabel(record) + " " + record.getName() + "?";
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record) {
            return "Failed to " + makeLabel(record) + " " + record.getName() + ".";
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            if (record.isEnabled()) return "Disable";
            return "Enable";
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record) {
            return "Success: " + makeLabel(record) + "d " + record.getName() + ".";
        }
    }
    
    private static class AddToGroupMessageMaker implements MessageMaker {

        @Override
        public String makeConfirmMessage(DeploymentRecord record) {
            return "Add " + record.getName() + " to selected server group?";
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record) {
            return "Failed to add " + record.getName() + " to server group.";
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            return "Add to Group";
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record) {
            return "Added " + record.getName() + " to server group.";
        }
        
    }
    
}
