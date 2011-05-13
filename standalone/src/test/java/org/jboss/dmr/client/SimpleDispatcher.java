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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public class SimpleDispatcher implements Dispatcher {

    private String domainApiUrl = "http://localhost:9990/domain-api";
    private static final String APPLICATION_DMR_ENCODED = "application/dmr-encoded";

    public SimpleDispatcher(String DOMAIN_API_URL) {
        this.domainApiUrl = DOMAIN_API_URL;
    }

    public SimpleDispatcher() {
        this.domainApiUrl = "http://localhost:9990/domain-api";
    }

    @Override
    public DispatchResult execute(ModelNode operation)
    {
        try {
            HttpURLConnection connection = createConnection();

            OutputStreamWriter out = new OutputStreamWriter( connection.getOutputStream());
            out.write(operation.toBase64String());
            safeClose(out);

            InputStream inputStream = connection.getResponseCode()==200 ?
                    connection.getInputStream() : connection.getErrorStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            safeClose(in);

            DispatchResult dispatchResult = new DispatchResult(response.toString());
            dispatchResult.setResponseStatus(connection.getResponseCode());

            return dispatchResult;

        } catch (Exception e) {
            throw new RuntimeException("failed to execute operation", e);
        }

    }

    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(domainApiUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", APPLICATION_DMR_ENCODED);
        connection.setRequestProperty("Content-Type", APPLICATION_DMR_ENCODED);
        return connection;
    }

    private void safeClose(Closeable me)
    {
        try {
            me.close();
        } catch (IOException e) {
            // ignore
            System.out.println("Failed to close stream (ignored): "+e.getMessage());
        }
    }
}
