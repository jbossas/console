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

import com.google.gwt.cell.client.ActionCell;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;

/**
 * The delegate that is activated when the DeploymentCommandCell is clicked.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class DeploymentCommandDelegate implements ActionCell.Delegate<DeploymentRecord> {

    private DeploymentCommand command;
    private DeployCommandExecutor executor;

    /**
     * Create a new DeploymentCommandDelegate
     * 
     * @param executor The delegate that knows how to execute the command on the server.
     * @param command The command that will be invoked.
     */
    public DeploymentCommandDelegate(DeployCommandExecutor executor, DeploymentCommand command) {
        this.command = command;
        this.executor = executor;
    }

    @Override
    public void execute(DeploymentRecord record) {
        command.execute(executor, record);
    }
}
