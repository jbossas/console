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

package org.jboss.as.console.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public abstract class TestCallback<T> implements AsyncCallback<T> {

    protected boolean didCallback = false;

    @Override
    public void onFailure(Throwable caught) {
        didCallback = true;
        throw new RuntimeException(caught);
    }

    public boolean hasBeenExecuted() {
        return didCallback;
    }
}
