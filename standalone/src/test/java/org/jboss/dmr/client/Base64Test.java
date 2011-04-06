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
        System.out.println(base64);

        ModelNode reverse = ModelNode.fromBase64(base64);
        assertEquals(8080, reverse.get("port").asInt());
    }

}
