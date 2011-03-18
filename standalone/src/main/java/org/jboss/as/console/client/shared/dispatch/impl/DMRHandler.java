package org.jboss.as.console.client.shared.dispatch.impl;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.dispatch.ActionHandler;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/17/11
 */
public class DMRHandler implements ActionHandler<DMRAction, DMRResponse> {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private final RequestBuilder requestBuilder;

    @Inject
    public DMRHandler(BootstrapContext bootstrap) {
        requestBuilder = new RequestBuilder(
                RequestBuilder.POST,
                bootstrap.getProperty(BootstrapContext.DOMAIN_API)
        );
    }

    @Override
    public DispatchRequest execute(DMRAction action, final AsyncCallback<DMRResponse> resultCallback) {

        assert action.getOperation()!=null;

        final ModelNode operation = action.getOperation();

        Request requestHandle = null;
        try {
            requestHandle = requestBuilder.sendRequest(operation.toJSONString(true), new RequestCallback() {
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
