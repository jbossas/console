package org.jboss.as.console.client.auth;

/**
 * @author Heiko Braun
 * @date 2/7/11
 */
public class CurrentUser {

    private boolean loggedIn;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
