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
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * Enumeration of commands used to manipulate deployments on the client side.  
 * These commands delegate to an executor that knows how to contact the server
 * side and carry out the command.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public enum DeploymentCommand {

    ENABLE_DISABLE(new EnableDisableMessageMaker()),
    REMOVE_FROM_GROUP(new RemoveMessageMaker()),
    ADD_TO_GROUP(new AddToGroupMessageMaker()),
    REMOVE_FROM_DOMAIN(new RemoveMessageMaker()),
    REMOVE_FROM_STANDALONE(new RemoveMessageMaker());
    
    private MessageMaker messageMaker;

    private DeploymentCommand(MessageMaker messageMaker) {
        this.messageMaker = messageMaker;
    }

    /**
     * Throw up an "Are you sure" dialog and then select the correct method
     * on the executor.
     * 
     * @param executor An implementation that can execute the command on the back end.
     * @param record The deployment to be manipulated.
     */
    public void execute(final DeployCommandExecutor executor, final DeploymentRecord record) {
        if  ((this == DeploymentCommand.ADD_TO_GROUP)) {
            executor.promptForGroupSelections(record);
            return;
        }
        
        Feedback.confirm(Console.CONSTANTS.common_label_areYouSure(), 
                         messageMaker.makeConfirmMessage(record, executor), 
                         new Feedback.ConfirmationHandler() {

            @Override
            public void onConfirmation(boolean isConfirmed) {
                if (isConfirmed) {
                    doCommand(executor, record);
                }
            }
        });
    }

    private void doCommand(DeployCommandExecutor executor, DeploymentRecord record) {
        switch (this) {
            case ENABLE_DISABLE:
                executor.enableDisableDeployment(record);
                break;
            case REMOVE_FROM_GROUP:
                executor.removeDeploymentFromGroup(record);
                break;
            case REMOVE_FROM_DOMAIN:
                executor.removeContent(record);
                break;
            case REMOVE_FROM_STANDALONE:
                executor.removeContent(record);
                break;
        }
    }

    public void displaySuccessMessage(DeployCommandExecutor executor, DeploymentRecord record) {
        Console.getMessageCenter().notify(
                new Message(messageMaker.makeSuccessMessage(record, executor), Message.Severity.Info));
    }

    public void displayFailureMessage(DeployCommandExecutor executor, DeploymentRecord record, Throwable t) {
        Console.getMessageCenter().notify(
                new Message(messageMaker.makeFailureMessage(record, executor), t.getMessage(), Message.Severity.Error));
    }

    public String getLabel(DeploymentRecord record) {
        return this.messageMaker.makeLabel(record);
    }

    private interface MessageMaker {
        String makeLabel(DeploymentRecord record);
        String makeSuccessMessage(DeploymentRecord record, DeployCommandExecutor executor);
        String makeFailureMessage(DeploymentRecord record, DeployCommandExecutor executor);
        String makeConfirmMessage(DeploymentRecord record, DeployCommandExecutor executor);
    }

    private static class RemoveMessageMaker implements MessageMaker {
        private static boolean isStandalone = Console.getBootstrapContext().getProperty(BootstrapContext.STANDALONE).equals("true");
        
        private String findTarget(DeploymentRecord record) {
            if (record.getServerGroup() != null) return record.getServerGroup();
            if (isStandalone) return Console.CONSTANTS.common_label_server();
            return Console.CONSTANTS.common_label_domain();
        }
        
        @Override
        public String makeConfirmMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.removeFromConfirm(record.getName(), findTarget(record));
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.failedToRemoveFrom(record.getName(), findTarget(record));
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            return Console.CONSTANTS.common_label_remove();
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.removedFrom(record.getName(), findTarget(record));
        }
    }
    
    private static class EnableDisableMessageMaker implements MessageMaker {

        @Override
        public String makeConfirmMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            if (record.isEnabled()) return Console.MESSAGES.disableConfirm(record.getName());
            return Console.MESSAGES.enableConfirm(record.getName());
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            if (record.isEnabled()) return Console.MESSAGES.failedToDisable(record.getName());
            return Console.MESSAGES.failedToDisable(record.getName());
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            if (record.isEnabled()) return Console.CONSTANTS.common_label_disable();
            return Console.CONSTANTS.common_label_enable();
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            // At this point record is not changed because we haven't yet refreshed.
            // So if record.isEnabled then we successfully disabled it.
            if (record.isEnabled()) return Console.MESSAGES.successDisabled(record.getName());
            return Console.MESSAGES.successEnabled(record.getName());
        }
    }
    
    private static class AddToGroupMessageMaker implements MessageMaker {
       
        @Override
        public String makeConfirmMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.addConfirm(record.getName(), Console.CONSTANTS.common_label_selectedGroups());
        }

        @Override
        public String makeFailureMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.failedToAdd(record.getName(), Console.CONSTANTS.common_label_selectedGroups());
        }

        @Override
        public String makeLabel(DeploymentRecord record) {
            return Console.CONSTANTS.common_label_addToGroups();
        }

        @Override
        public String makeSuccessMessage(DeploymentRecord record, DeployCommandExecutor executor) {
            return Console.MESSAGES.successAdd(record.getName(), Console.CONSTANTS.common_label_selectedGroups());
        }
    }
}
