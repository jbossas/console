package org.jboss.as.console.client.shared.general;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * @author Heiko Braun
 * @date 2/27/12
 */
public interface SuggestionManagement {

    void requestSuggestions(SuggestOracle.Request request, SuggestOracle.Callback callback);
}
