package org.jboss.as.console.client.shared.general;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.SuggestOracle;

public class SimpleSuggestion implements SuggestOracle.Suggestion, IsSerializable {
    private String displayString;
    private String replacementString;

    /**
     * Constructor used by RPC.
     */
    public SimpleSuggestion() {
    }

    /**
     * Constructor for <code>MultiWordSuggestion</code>.
     *
     * @param replacementString the string to enter into the SuggestBox's text
     *          box if the suggestion is chosen
     * @param displayString the display string
     */
    public SimpleSuggestion(String replacementString, String displayString) {
        this.replacementString = replacementString;
        this.displayString = displayString;
    }

    public String getDisplayString() {
        return displayString;
    }

    public String getReplacementString() {
        return replacementString;
    }
}
