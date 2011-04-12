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

import static org.junit.Assert.assertEquals;

/**
 * @author Heiko Braun
 * @date 4/6/11
 */
public class Base64Test {

    @Test
    public void testJBAS9165() throws Exception
    {
        ModelNode value = new ModelNode();
        value.get("name").set("http-port");
        value.get("port").set(8080);

        String base64 = value.toBase64String();
        //System.out.println(base64);

        ModelNode reverse = ModelNode.fromBase64(base64);
        assertEquals(8080, reverse.get("port").asInt());
    }

    @Test
    public void testJBAS9165_part2() throws Exception
    {
        ModelNode value = new ModelNode();
        value.get("name").set("http-port");
        value.get("port").set(32768);

        String base64 = value.toBase64String();
        //System.out.println(base64);

        ModelNode reverse = ModelNode.fromBase64(base64);
        assertEquals(32768, reverse.get("port").asInt());
    }

}
