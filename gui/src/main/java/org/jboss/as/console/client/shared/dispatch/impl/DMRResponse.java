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


import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.Result;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRResponse implements Result<ModelNode> {
    private String responseText;
    private String contentType;

    public DMRResponse(String responseText, String contentType) {
        this.responseText = responseText;
        this.contentType = contentType;
    }

    @Override
    public ModelNode get() {
        ModelNode response = ModelNode.fromBase64(responseText);

        boolean hasReloadFlag = false;

        if(response.hasDefined("response-headers"))
        {
            List<Property> headers = response.get("response-headers").asPropertyList();
            for(Property header : headers) {
                if("process-state".equals(header.getName())) {
                    if("reload-required".equals(header.getValue().asString())) {
                        hasReloadFlag=true;
                    }
                }
            }


        }

        // update the state anytime
        Console.MODULES.getReloadState().setReloadRequired(hasReloadFlag);

        return response;
    }
}
