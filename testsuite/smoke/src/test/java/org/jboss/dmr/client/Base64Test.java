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

package org.jboss.dmr.client;

import org.junit.Test;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void testEncoding() throws Exception {

        ModelNode operation = new ModelNode();
        operation.get(OP).set("add-system-property");
        operation.get(ADDRESS).add("server-group", "mygroup");
        operation.get("name").set("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9797");
        operation.get("value").set("valueString");
        operation.get("boot-time").set(true);


        String base64 = operation.toBase64String();
        assertNotNull(base64);
    }

    /**
     * TODO: IIRC this base64 string contains illegal characters?
     * @throws Exception
     */
    @Test
    public void testDSDeletionResponse() throws Exception
    {

        String base64 = "bwAAAAMAB291dGNvbWVzAAdzdWNjZXNzAAZyZXN1bHRvAAAAAQANc2VydmVyLWdyb3Vwc28AAAAC\n" +
                "ABFtYWluLXNlcnZlci1ncm91cG8AAAACAApzZXJ2ZXItdHdvbwAAAAIABGhvc3RzAAVsb2NhbAAI\n" +
                "cmVzcG9uc2VvAAAAAgAHb3V0Y29tZXMABmZhaWxlZAATZmFpbHVyZS1kZXNjcmlwdGlvbnMAqE5v\n" +
                "IGhhbmRsZXIgZm9yIHJlbW92ZSBhdCBhZGRyZXNzIFsKICAgICgiaG9zdCIgPT4gImxvY2FsIiks\n" +
                "CiAgICAoInNlcnZlciIgPT4gInNlcnZlci10d28iKSwKICAgICgic3Vic3lzdGVtIiA9PiAiZGF0\n" +
                "YXNvdXJjZXMiKSwKICAgICgiZGF0YS1zb3VyY2UiID0+ICJkYXRhYmFzZS9NeURTIikKXQAKc2Vy\n" +
                "dmVyLW9uZW8AAAACAARob3N0cwAFbG9jYWwACHJlc3BvbnNlbwAAAAIAB291dGNvbWVzAAZmYWls\n" +
                "ZWQAE2ZhaWx1cmUtZGVzY3JpcHRpb25zAKhObyBoYW5kbGVyIGZvciByZW1vdmUgYXQgYWRkcmVz\n" +
                "cyBbCiAgICAoImhvc3QiID0+ICJsb2NhbCIpLAogICAgKCJzZXJ2ZXIiID0+ICJzZXJ2ZXItb25l\n" +
                "IiksCiAgICAoInN1YnN5c3RlbSIgPT4gImRhdGFzb3VyY2VzIiksCiAgICAoImRhdGEtc291cmNl\n" +
                "IiA9PiAiZGF0YWJhc2UvTXlEUyIpCl0AEm90aGVyLXNlcnZlci1ncm91cG8AAAABAAxzZXJ2ZXIt\n" +
                "dGhyZWVvAAAAAgAEaG9zdHMABWxvY2FsAAhyZXNwb25zZW8AAAACAAdvdXRjb21lcwAGZmFpbGVk\n" +
                "ABNmYWlsdXJlLWRlc2NyaXB0aW9ucwB0b3JnLmpib3NzLm1zYy5zZXJ2aWNlLkR1cGxpY2F0ZVNl\n" +
                "cnZpY2VFeGNlcHRpb246IFNlcnZpY2UgamJvc3MuZGF0YS1zb3VyY2UuamF2YTovZGF0YWJhc2Uv\n" +
                "TXlEUyBpcyBhbHJlYWR5IHJlZ2lzdGVyZWQAFmNvbXBlbnNhdGluZy1vcGVyYXRpb25vAAAACgAJ\n" +
                "b3BlcmF0aW9ucwADYWRkAAdhZGRyZXNzbAAAAANwAAdwcm9maWxlcwAHZGVmYXVsdHAACXN1YnN5\n" +
                "c3RlbXMAC2RhdGFzb3VyY2VzcAALZGF0YS1zb3VyY2VzAA1kYXRhYmFzZS9NeURTAA5jb25uZWN0\n" +
                "aW9uLXVybHMABG5vbmUACWpuZGktbmFtZXMADWRhdGFiYXNlL015RFMAC2RyaXZlci1uYW1lcwAC\n" +
                "aDIACXBvb2wtbmFtZXMACW15RFNfUG9vbAAQdXNlLWphdmEtY29udGV4dFoBAAdlbmFibGVkWgAA\n" +
                "CXVzZXItbmFtZXMAAnNhAAhwYXNzd29yZHMAAA==";

        ModelNode modelNode = null;

        try {
            modelNode = ModelNode.fromBase64(base64);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        assertNotNull(modelNode);
    }

    @Test
    public void testAS7912() throws Exception {
        String base64 = "bwAAAAMAB291dGNvbWVzAAdzdWNjZXNzAAZyZXN1bHRvAAAABAAKYWN0aXZhdGlvbnMABGxhenkA\n" +
                "DWNvbmZpZ3VyYXRpb25vAAAAAgADcGlkcwA4b3JnLmFwYWNoZS5mZWxpeC53ZWJjb25zb2xlLmlu\n" +
                "dGVybmFsLnNlcnZsZXQuT3NnaU1hbmFnZXIAGGNvbmZpZ3VyYXRpb25fcHJvcGVydGllc28AAAAB\n" +
                "AAxtYW5hZ2VyLnJvb3RzAApqYm9zcy1vc2dpAApwcm9wZXJ0aWVzbwAAAAIAHW9yZy5qYm9zcy5v\n" +
                "c2dpLnN5c3RlbS5tb2R1bGVzcwS8b3JnLmFwYWNoZS5jb21tb25zLmxvZ2dpbmcsCiAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICBvcmcuYXBhY2hlLmxvZzRqLAogICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgb3JnLmpib3NzLmFzLm9zZ2ksCiAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICBvcmcuc2xmNGosAChvcmcub3NnaS5mcmFtZXdvcmsu\n" +
                "c3lzdGVtLnBhY2thZ2VzLmV4dHJhcwneb3JnLmFwYWNoZS5jb21tb25zLmxvZ2dpbmc7dmVyc2lv\n" +
                "bj0xLjEuMSwKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIG9yZy5hcGFjaGUu\n" +
                "bG9nNGo7dmVyc2lvbj0xLjIsCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBv\n" +
                "cmcuamJvc3MuYXMub3NnaS5zZXJ2aWNlO3ZlcnNpb249Ny4wLAogICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgb3JnLmpib3NzLm9zZ2kuc3BpLmNhcGFiaWxpdHk7dmVyc2lvbj0x\n" +
                "LjAsCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBvcmcuamJvc3Mub3NnaS5z\n" +
                "cGkudXRpbDt2ZXJzaW9uPTEuMCwKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "IG9yZy5qYm9zcy5vc2dpLnRlc3Rpbmc7dmVyc2lvbj0xLjAsCiAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAg\n" +
                "ICAgICAgICAgICAgICAgICBvcmcuc2xmNGo7dmVyc2lvbj0xLjUuMTAsAAdtb2R1bGVzbwAAAAoA\n" +
                "IW9yZy5qYm9zcy5hcy5hcnF1aWxsaWFuLmFnZ3JlZ2F0ZXUAEW9yZy5qYm9zcy5sb2dnaW5ndQAU\n" +
                "b3JnLmFwYWNoZS5hcmllcy5qbXhvAAAAAQAFc3RhcnRzAAR0cnVlABVvcmcuYXBhY2hlLmFyaWVz\n" +
                "LnV0aWx1ABxvcmcuYXBhY2hlLmZlbGl4LmNvbmZpZ2FkbWlubwAAAAEABXN0YXJ0cwAEdHJ1ZQAU\n" +
                "b3JnLmFwYWNoZS5mZWxpeC5sb2dvAAAAAQAFc3RhcnRzAAR0cnVlAB1vcmcuamJvc3MuYXMub3Nn\n" +
                "aS5jb25maWdhZG1pbm8AAAABAAVzdGFydHMABHRydWUAEm9yZy5qYm9zcy5vc2dpLmpteG8AAAAB\n" +
                "AAVzdGFydHMABHRydWUAFm9yZy5qYm9zcy5vc2dpLmxvZ2dpbmdvAAAAAQAFc3RhcnRzAAR0cnVl\n" +
                "ABNvcmcub3NnaS5jb21wZW5kaXVtdQAWY29tcGVuc2F0aW5nLW9wZXJhdGlvbnU=";

        ModelNode modelNode = null;

        try {
            modelNode = ModelNode.fromBase64(base64);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        assertNotNull(modelNode);
    }


    @Test
    public void testIntegerValues() throws Exception {

        ModelNode operation = new ModelNode();
        operation.get(OP).set("operation-name");
        operation.get(ADDRESS).add("socket-binding", "jgroups-udp-fd");
        operation.get("port").set(54200);

        String base64 = operation.toBase64String();
        assertNotNull(base64);

        ModelNode revers = ModelNode.fromBase64(base64);
        assertEquals(54200, revers.get("port").asInt());

    }

}
