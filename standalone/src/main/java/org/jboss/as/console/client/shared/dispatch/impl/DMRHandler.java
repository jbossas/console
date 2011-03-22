package org.jboss.as.console.client.shared.dispatch.impl;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.as.console.client.shared.dispatch.InvocationMetrics;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRHandler implements ActionHandler<DMRAction, DMRResponse> {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String DMR_ENCODED = "application/dmr-encoded";

    private final RequestBuilder requestBuilder;

    private boolean trackInvocations = true; //!GWT.isScript(); TODO
    private InvocationMetrics metrics;

    @Inject
    public DMRHandler(BootstrapContext bootstrap, InvocationMetrics metrics) {

        this.metrics = metrics;

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

        if(trackInvocations)
        {
            metrics.addInvocation(operation);
        }

        Request requestHandle = null;
        try {
            requestHandle = requestBuilder.sendRequest(operation.toBase64String(), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {

                    if(200==response.getStatusCode())
                    {
                        resultCallback.onSuccess(
                                new DMRResponse(
                                        response.getText(),
                                        response.getHeader(HEADER_CONTENT_TYPE)
                                )
                        );
                    }
                    else
                    {
                        resultCallback.onFailure(
                                new Exception("Unexpected HTTP status " + response.getStatusCode()+
                                ": "+operation.asString())
                        );
                    }
                }

                @Override
                public void onError(Request request, Throwable e) {
                    resultCallback.onFailure(e);
                }
            });
        } catch (RequestException e) {
            resultCallback.onFailure(e);
        }

        return new DispatchRequestHandle(requestHandle);
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
}
