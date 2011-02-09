package org.jboss.as.console.client.auth;


import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class SignInPageView extends ViewWithUiHandlers<SignInPageUIHandlers> implements
    SignInPagePresenter.MyView {

  private static String html = "<div class='loginForm'>\n"
    + "<table align=\"center\" cellspacing=0 cellpadding=0>\n"
    + "  <tr>\n"
    + "    <td class='table-header' colspan=\"2\" style=\"font-weight:bold;\">JBoss Management</td>\n"
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

    userNameField.setText("admin");

    panel.add(userNameField, "userNameFieldContainer");
    panel.add(passwordField, "passwordFieldContainer");
    panel.add(signInButton, "signInButtonContainer");
  }

  @Override
  public Widget asWidget() {
    return panel;
  }


  public String getUserName() {
    return userNameField.getText();
  }


  public String getPassword() {
    return passwordField.getText();
  }

  public Button getSignInButton() {
    return signInButton;
  }

  @Override
  public void resetAndFocus() {
    userNameField.setFocus(true);
    userNameField.selectAll();
  }
}
