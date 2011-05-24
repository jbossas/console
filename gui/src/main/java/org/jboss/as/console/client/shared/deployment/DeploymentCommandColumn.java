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

import com.google.gwt.user.cellview.client.Column;
import org.jboss.as.console.client.shared.model.DeploymentRecord;

/**
 * A column that renders cells giving the user the ability to fire off a
 * DeploymentCommand.
 *
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 */
public class DeploymentCommandColumn extends Column<DeploymentRecord, DeploymentRecord> {

    /**
     * Create a new Command Column.
     * 
     * @param executor The delegate that knows how to execute the command on the server.
     * @param command The command that will be fired.
     */
    public DeploymentCommandColumn(DeployCommandExecutor executor, DeploymentCommand command) {
        super(new DeploymentCommandCell(executor, command));
    }
    
    @Override
    public DeploymentRecord getValue(DeploymentRecord record) {
        return record;
    }
}
