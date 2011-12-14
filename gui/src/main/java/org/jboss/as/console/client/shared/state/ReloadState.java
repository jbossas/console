package org.jboss.as.console.client.shared.state;

import org.jboss.as.console.client.Console;

import javax.inject.Singleton;

/**
 * @author Heiko Braun
 * @date 12/14/11
 */
@Singleton
public class ReloadState {
    boolean reloadRequired = false;

    public boolean isReloadRequired() {
        return reloadRequired;
    }

    public void setReloadRequired(boolean willBeRequired) {

        if(willBeRequired && this.reloadRequired!=willBeRequired)
        {
            // state update, fire notification
            Console.warning("A server reload is required!", true);
        }

        this.reloadRequired = willBeRequired;
    }
}
