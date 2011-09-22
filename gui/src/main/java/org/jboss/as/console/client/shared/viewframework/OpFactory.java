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

import org.jboss.dmr.client.ModelNode;

/**
 * Helper factory used to create ModelNode instances.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface OpFactory {
    /**
     * Create a ModelNode with an operation that will update an Entity.
     * 
     * @param operation The name of the DMR operation.
     * @param namedEntity The Entity.
     * @return The ModelNode for the operation.
     */
    public ModelNode makeUpdateOp(String operation, NamedEntity namedEntity);
    
    /**
     * Create a read-resource operation.
     * 
     * @return ModelNode a read-resource operation.
     */
    public ModelNode makeReadResource();
}
