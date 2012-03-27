package org.jboss.as.console.client.core;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import org.jboss.as.console.client.Console;

/**
 * @author Heiko Braun
 * @date 2/13/12
 */
public class LogoutCmd implements Command {
    @Override
    public void execute() {
        String logoutUrl = Console.getBootstrapContext().getLogoutUrl();
        clearMsie();
        Window.Location.replace(logoutUrl);
    }

    public static native String clearMsie() /*-{
        try {
            document.execCommand('ClearAuthenticationCache');
        } catch (error) {
        }

    }-*/;
}
