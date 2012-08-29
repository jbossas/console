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

package org.jboss.as.console.client.shared.dispatch.impl;

import org.jboss.as.console.client.shared.dispatch.Action;
import org.jboss.as.console.client.shared.dispatch.ActionType;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRAction implements Action<DMRResponse> {

    private ModelNode operation;
    private boolean cachable = true;

    public DMRAction(ModelNode operation) {
        this.operation = operation;
    }

    public DMRAction(ModelNode operation, boolean cachable) {
        this.operation = operation;
        this.cachable = cachable;
    }

    @Override
    public ActionType getType() {
        return ActionType.DMR;
    }

    @Override
    public Object getAddress() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean isSecured() {
        return false;
    }

    public ModelNode getOperation()
    {
        return this.operation;
    }

    public void setCachable(boolean cachable) {
        this.cachable = cachable;
    }

    public boolean isCachable() {
        return cachable;
    }
}


