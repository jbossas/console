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

package org.jboss.as.console.server.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author: Heiko Braun <hbraun@redhat.com>
 * @date: Apr 21, 2010
 */
public class ProxyConfig
{
    private Map<String,Object> rootConfig;

    public final static String SERVICES = "services";
    public final static String ID = "id";
    public final static String URL = "url";
    public final static String CONTENT_TYPE = "contentType";
    public final static String PASSTHROUGH = "passthrough";

    protected ProxyConfig(Map<String,Object> rootConfig)
    {
        this.rootConfig = rootConfig;
    }

    private static ProxyConfig parse(String json)
    {
        /*JSONDecoder decoder = new JSONDecoder(json);
    ProxyConfig config = new ProxyConfig((Map<String,Object>)decoder.parse());
    return config;   */

       throw new RuntimeException("Not implemented");
    }

    public static ProxyConfig parse(InputStream in)
    {
        return parse(inputStreamToString(in));
    }

    public List<Map<String,Object>> getServices()
    {
        Map<String,Object> root = (Map<String,Object>)rootConfig.get("xhp");
        return (List)root.get(SERVICES);
    }

    private static String inputStreamToString(InputStream in)
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            bufferedReader.close();
            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to parse input stream", e);
        }
    }
}
