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

package org.jboss.as.console.client.shared.dispatch;

import org.jboss.dmr.client.ModelNode;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/22/11
 */
public class InvocationMetrics {

    private Map<String, Double> numInvocations = new HashMap<String, Double>();

    public void addInvocation(ModelNode operation)
    {
        final String token = getToken(operation);

        Double value = numInvocations.get(token);
        if(null==value)
        {
            numInvocations.put(token , 1.0);
        }
        else
        {
            numInvocations.put(token, ++value);
        }
    }

    public static String getToken(ModelNode operation) {

        StringBuffer sb = new StringBuffer();
        if(operation.get(OP).asString().equals(COMPOSITE))
        {
            for(ModelNode step : operation.get(STEPS).asList())
            {
                sb.append("_").append(getOpToken(step));
            }
        }
        else
        {
            sb.append(getOpToken(operation));
        }
        return sb.toString();
    }

    private static String getOpToken(ModelNode operation) {
        StringBuffer sb = new StringBuffer();
        sb.append(operation.get(ADDRESS).asString())
                .append(":")
                .append(operation.get(OP))
                .append(";")
                .append(operation.get(CHILD_TYPE).asString())
                .append(";")
                .append(operation.get(NAME).asString());
        return sb.toString();
    }

    public void reset() {
        numInvocations.clear();
    }

    public Map<String, Double> getStats() {
        return numInvocations;
    }

    public boolean hasMetrics()
    {
        return !numInvocations.isEmpty();
    }

    public void dump() {

        for(String key : numInvocations.keySet())
        {
            System.out.println(numInvocations.get(key) +":\t"+key.hashCode() + " \t "+key);
        }

    }
}
