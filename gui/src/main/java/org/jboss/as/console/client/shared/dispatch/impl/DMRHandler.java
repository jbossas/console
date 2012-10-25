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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.debug.Diagnostics;
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.dmr.client.ModelNode;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRHandler implements ActionHandler<DMRAction, DMRResponse> {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String DMR_ENCODED = "application/dmr-encoded";
    private static final String HEADER_CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "Keep-Alive";

    private final RequestBuilder requestBuilder;

    private boolean trackInvocations = Diagnostics.isEnabled();

    private UIConstants constants;

    private static long idCounter = 0;

    private static enum Type {
        BEGIN("begin"),
        END("end"),
        SEND("requestSent"),
        RECEIVE("responseReceived"),
        SERIALIZED("requestSerialized"),
        DESERIALIZED("responseDeserialized");

        private String classifier;

        private Type(String classifier) {
            this.classifier = classifier;
        }

        public String getClassifier() {
            return classifier;
        }
    }

    @Inject
    public DMRHandler(BootstrapContext bootstrap, UIConstants constants) {

        this.constants = constants;

        requestBuilder = new RequestBuilder(
                RequestBuilder.POST,
                bootstrap.getProperty(BootstrapContext.DOMAIN_API)
        );

        requestBuilder.setHeader(HEADER_ACCEPT, DMR_ENCODED);
        requestBuilder.setHeader(HEADER_CONTENT_TYPE, DMR_ENCODED);

    }

    @Override
    public DispatchRequest execute(DMRAction action, final AsyncCallback<DMRResponse> resultCallback) {

        assert action.getOperation()!=null;

        final ModelNode operation = action.getOperation();

        Request request = executeRequest(resultCallback, operation);
        DispatchRequest handle = new DispatchRequestHandle(request);

        return handle;

    }

    private Request executeRequest(final AsyncCallback<DMRResponse> resultCallback, final ModelNode operation) {

        if(idCounter==Long.MAX_VALUE)
            idCounter = 0;

        Request requestHandle = null;
        try {

            final String id = String.valueOf(idCounter++);
            trace(Type.BEGIN, id, operation);

            String requestData = operation.toBase64String();
            trace(Type.SERIALIZED, id, operation);

            requestHandle = requestBuilder.sendRequest(requestData, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {


                    trace(Type.RECEIVE, id, operation);

                    int statusCode = response.getStatusCode();

                    if(200== statusCode)
                    {
                        resultCallback.onSuccess(
                                new DMRResponse(
                                        response.getText(),
                                        response.getHeader(HEADER_CONTENT_TYPE)
                                )
                        );
                    }
                    else if(401 == statusCode || 0 == statusCode)
                    {
                        resultCallback.onFailure( new Exception("Authentication required."));

                    }
                    else if(307 == statusCode)
                    {
                        String location = response.getHeader("Location");
                        Log.error("Redirect '"+location+"'. Could not execute "+operation.toString());
                        redirect(location);
                    }
                    else if(503 == statusCode)
                    {
                        resultCallback.onFailure(
                                new Exception("Service temporarily unavailable. Is the server is still booting?" )
                        );
                    }
                    else
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(constants.common_error_unexpectedHttpResponse()).append(": ").append(statusCode);
                        sb.append("\n\n");
                        sb.append("Request\n");
                        sb.append(operation.toString());

                        sb.append("\n\nResponse\n\n");
                        sb.append(response.getStatusText()).append("\n");

                        String payload = response.getText().equals("") ? constants.common_error_detailsMissing() :
                                ModelNode.fromBase64(response.getText()).toString();

                        sb.append(payload);
                        resultCallback.onFailure( new Exception(sb.toString()));
                    }

                    trace(Type.END, id, operation);
                }

                @Override
                public void onError(Request request, Throwable e) {

                    trace(Type.RECEIVE, id, operation);
                    resultCallback.onFailure(e);
                    trace(Type.END, id, operation);
                }
            });

            trace(Type.SEND, id, operation);

        } catch (RequestException e) {
            resultCallback.onFailure(e);
        }
        return requestHandle;
    }

    private void trace(Type type, String id, ModelNode operation) {

        if(!trackInvocations) return;

        //String token = getToken(operation);
        if(Type.BEGIN.equals(type))
        {
            Diagnostics.logRpc(type.getClassifier(), id, System.currentTimeMillis(), getToken(operation));
        }
        else
        {
            Diagnostics.logRpc(type.getClassifier(), id, System.currentTimeMillis());
        }

    }

    public static String getToken(ModelNode operation) {

        StringBuffer sb = new StringBuffer();
        if(operation.get(OP).asString().equals(COMPOSITE))
        {
            for(ModelNode step : operation.get(STEPS).asList())
            {
                sb.append(" _").append(getOpToken(step));
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
                .append(": ")
                .append(operation.get(OP))
                .append("; ")
                .append(operation.get(CHILD_TYPE).asString())
                .append("; ");

        if(operation.get(NAME).isDefined())
                sb.append(operation.get(NAME).asString());

        return sb.toString();
    }


    @Override
    public DispatchRequest undo(DMRAction action, DMRResponse result, AsyncCallback<Void> callback) {
        throw new RuntimeException("Not implemented yet.");
    }

    class DispatchRequestHandle implements DispatchRequest
    {
        private Request delegate;

        DispatchRequestHandle(Request delegate) {
            this.delegate = delegate;
        }

        @Override
        public void cancel() {
            if(delegate!=null) delegate.cancel();
        }

        @Override
        public boolean isPending() {
            return delegate!=null ? delegate.isPending() : false;
        }
    }

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;
}
