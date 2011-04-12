/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.dmr.client;

import org.junit.Test;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;
import static org.junit.Assert.*;


/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class DispatchAPITest {

    @Test
    public void showcase() {

        // create a dispatcher
        Dispatcher dispatcher = new SimpleDispatcher("http://localhost:9990/domain-api");

        // create an operation (in this case: reading profile names)
        ModelNode operation = new ModelNode();
        operation.get(OP).set(ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION);
        operation.get("child-type").set("profile");
        operation.get(ModelDescriptionConstants.ADDRESS).setEmptyList();

        // execute the operation
        DispatchResult result = dispatcher.execute(operation);
        assertEquals(200, result.getResponseStatus());

        // evaluate the response
        ModelNode response = result.as();
        assertEquals("success", response.get(OUTCOME).asString());

    }
}
