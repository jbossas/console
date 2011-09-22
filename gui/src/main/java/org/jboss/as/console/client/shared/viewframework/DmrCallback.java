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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * Helper class that automatically logs the failure of an asynchronous call.
 * Furthermore, it will break down a successful async call into a successful 
 * or failed DMR response.  By default, it will just log a failed response to
 * the console.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public abstract class DmrCallback implements AsyncCallback<DMRResponse> {

    @Override
    public void onFailure(Throwable caught) {
        Log.error(Console.CONSTANTS.common_error_unknownError(), caught);
    }

    @Override
    public void onSuccess(DMRResponse result) {
        ModelNode response = ModelNode.fromBase64(result.getResponseText());
        if (response.get(OUTCOME).asString().equals(SUCCESS)) {
            onDmrSuccess(response);
        } else {
            onDmrFailure(response);
        }
    }
    
    /**
     * Be default, just log a failed DMR operation to the console.
     * Override this if you want more elaborate handling.
     * @param response The full response as a ModelNode.
     */
    public void onDmrFailure(ModelNode response) {
        Console.error(response.get(FAILURE_DESCRIPTION).asString());
    }
    
    /**
     * Handle a successful DMR operation.
     * @param response The full response as a ModelNode.
     */
    public abstract void onDmrSuccess(ModelNode response);
}
