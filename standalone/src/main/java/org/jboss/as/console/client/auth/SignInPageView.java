package org.jboss.as.console.client.auth;


import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SignInPageView extends ViewWithUiHandlers<SignInPageUIHandlers> implements
        SignInPagePresenter.MyView {

    private static String html = "<div class='loginForm'>\n"
            + "<table align=\"center\" cellspacing=0 cellpadding=0>\n"
            + "  <tr>\n"
            + "    <td class='loginForm-header' colspan=\"2\" style=\"font-weight:bold;\">JBoss Management</td>\n"
            + "  </tr>\n"
            + "  <tr>\n"
            + "    <td>Username</td>\n"
            + "    <td id=\"userNameFieldContainer\"></td>\n"
            + "  </tr>\n"
            + "  <tr>\n"
            + "    <td>Password</td>\n"
            + "    <td id=\"passwordFieldContainer\"></td>\n"
            + "  </tr>\n"
            + "  <tr>\n"
            + "    <td></td>\n"
            + "    <td id=\"signInButtonContainer\"></td>\n"
            + "  </tr>\n"
            + "</table>\n"
            + "</div>\n";

    HTMLPanel panel = new HTMLPanel(html);

    private final TextBox userNameField;
    private final PasswordTextBox passwordField;
    private final Button signInButton;

    @Inject
    public SignInPageView() {
        userNameField = new TextBox();
        passwordField = new PasswordTextBox();
        signInButton = new Button("Sign in");
        signInButton.setStyleName("default-button");

        userNameField.setText("admin");

        panel.add(userNameField, "userNameFieldContainer");
        panel.add(passwordField, "passwordFieldContainer");
        panel.add(signInButton, "signInButtonContainer");

        panel.sinkEvents(Event.ONKEYDOWN);

    }

    @Override
    public Widget asWidget() {
        return panel;
    }


    public TextBox getUserName() {
        return userNameField;
    }


    public TextBox getPassword() {
        return passwordField;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public void resetAndFocus() {
        userNameField.setFocus(true);
        userNameField.selectAll();
    }

}