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
package org.jboss.as.console.client.shared.viewframework;

import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;

/**
 * Factory to provide a ModelNode with proper address
 * for a given subsystem.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class SubsystemOpFactory implements OpFactory {
    
    private String subsystemName;
    private String entityName;
    
    public SubsystemOpFactory(String subsystemName, String entityName) {
        this.subsystemName = subsystemName;
        this.entityName = entityName;
    }
    
    public String getSubsystemName() {
        return this.subsystemName;
    }
    
    public String getEntityName() {
        return this.entityName;
    }
    
    /**
     * Make a ModelNode for the operation.
     * 
     * @param operation A management operation from ModelDescriptionConstants.
     * @return A ModelNode with the operation for the subsystem.
     */
    @Override
    public ModelNode makeUpdateOp(String operation, NamedEntity namedEntity) {
        ModelNode node = new ModelNode();
        node.get(OP).set(operation);
        node.get(ADDRESS).set(Baseadress.get());
        node.get(ADDRESS).add("subsystem", this.subsystemName);
        node.get(ADDRESS).add(entityName, namedEntity.getName());
        return node;
    }

    @Override
    public ModelNode makeReadResource() {
        ModelNode node = new ModelNode();
        node.get(OP).set(READ_RESOURCE_OPERATION);
        node.get(ADDRESS).set(Baseadress.get());
        node.get(ADDRESS).add("subsystem", this.subsystemName);
        node.get(RECURSIVE).set(true);
        return node;
    }
    
}
