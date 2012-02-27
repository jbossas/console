package org.jboss.as.console.client.shared.general;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * @author Heiko Braun
 * @date 2/27/12
 */
public class DelegatingOracle extends SuggestOracle {

    private SuggestionManagement delegate;

    public DelegatingOracle(SuggestionManagement delegate) {
        this.delegate = delegate;
    }

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        delegate.requestSuggestions(request,callback);
    }
}
