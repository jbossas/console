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
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.DispatchResult;
import org.jboss.dmr.client.SimpleDispatcher;

import java.io.IOException;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class TestDMRHandler implements ActionHandler<DMRAction, DMRResponse> {

    private static final String DOMAIN_API_URL = "http://localhost:9990/domain-api";
    private static final String APPLICATION_DMR_ENCODED = "application/dmr-encoded";

    @Override
    public DispatchRequest execute(DMRAction action, AsyncCallback<DMRResponse> callback) {


        try {
            DispatchResult result = new SimpleDispatcher(DOMAIN_API_URL).execute(action.getOperation());
            callback.onSuccess(new DMRResponse(result.getResponseText(), APPLICATION_DMR_ENCODED) );

        } catch (Exception e) {
            callback.onFailure(e);
        }

        return new DispatchRequest()
        {
            @Override
            public void cancel() {

            }

            @Override
            public boolean isPending() {
                return false;
            }
        };
    }

    @Override
    public DispatchRequest undo(DMRAction action, DMRResponse result, AsyncCallback<Void> callback) {
        throw new RuntimeException("Undo not implemented");
    }
}
