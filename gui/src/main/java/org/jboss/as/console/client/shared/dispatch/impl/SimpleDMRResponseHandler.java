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

import static org.jboss.dmr.client.ModelDescriptionConstants.FAILURE_DESCRIPTION;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUCCESS;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.jboss.as.console.client.Console;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class SimpleDMRResponseHandler implements AsyncCallback<DMRResponse> {
    private final String operation;
    private final String entityName;
    private final String id;
    private final Command callback;

    public SimpleDMRResponseHandler(String operationName, String entityName, String id, Command callback) {
        this.operation = operationName;
        this.entityName = entityName;
        this.id = id;
        this.callback = callback;
    }

    @Override
    public void onFailure(Throwable caught) {
        Console.error(Console.CONSTANTS.common_error_failure() + " " + operation + " " + entityName, caught.getMessage());
    }

    @Override
    public void onSuccess(DMRResponse result) {
        ModelNode response = ModelNode.fromBase64(result.getResponseText());
        boolean success = response.get(OUTCOME).asString().equals(SUCCESS);
        if (success)
            Console.info(Console.CONSTANTS.common_label_success() + " " + operation + " " + entityName + ": " + id);
        else
            Console.error(Console.CONSTANTS.common_error_failure() + " " + operation + " " + entityName + ": " + id,
                response.get(FAILURE_DESCRIPTION).asString());

        Console.schedule(callback);
    }
}
