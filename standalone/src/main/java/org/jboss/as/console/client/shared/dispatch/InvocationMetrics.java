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

package org.jboss.as.console.client.shared.dispatch;

import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public class InvocationMetrics {

    private Map<String, Double> numInvocations = new HashMap<String, Double>();

    public void addInvocation(ModelNode operation)
    {

        String key = deriveKey(operation);

        Double value = numInvocations.get(key);
        if(null==value)
            numInvocations.put(key, 1.0);
        else
        {
            numInvocations.put(key, ++value);
        }
    }

    private String deriveKey(ModelNode operation) {
        String key = operation.get(ModelDescriptionConstants.OP_ADDR) + "::" +
                operation.get(ModelDescriptionConstants.OP);

        String childType = operation.get(ModelDescriptionConstants.CHILD_TYPE) == null ?
                "" : " (child-type="+operation.get(ModelDescriptionConstants.CHILD_TYPE).asString() +")";
        key+=childType;

        return key;
    }

    public Map<String, Double> getNumInvocations() {
        return numInvocations;
    }
}
