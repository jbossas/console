package org.jboss.as.console.client.auth;


import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.jboss.as.console.client.Build;

public class SignInPageView extends ViewWithUiHandlers<SignInPageUIHandlers> implements
        SignInPagePresenter.MyView {

    private static String html = "<div class='loginForm'>"
            + "<table align=\"center\" cellspacing=0 cellpadding=0>"
            + "  <tr>"
            + "    <td class='loginForm-header' colspan=\"2\" style=\"font-weight:bold;\">JBoss Management</td>"
            + "  </tr>"
            + "  <tr>"
            + "    <td>Username</td>"
            + "    <td id=\"userNameFieldContainer\"></td>"
            + "  </tr>"
            + "  <tr>"
            + "    <td>Password</td>"
            + "    <td id=\"passwordFieldContainer\"></td>"
            + "  </tr>"
            + "  <tr>"
            + "    <td></td>"
            + "    <td id=\"signInButtonContainer\"></td>"
            + "  </tr>"
            + "</table>"
            + "</div>"+
            "<div id='dev-options'><div>";

    HTMLPanel panel = new HTMLPanel(html);

    private final TextBox userNameField;
    private final PasswordTextBox passwordField;
    private final Button signInButton;
    private SignInPagePresenter presenter;

    private CheckBox checkbox;

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

        // dev options
        /*checkbox = new CheckBox();
        checkbox.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent event) {
                presenter.setBootStandalone(checkbox.getValue());
            }
        });
        */
        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "margin-top:20px; vertical-align:bottom;");
        options.getElement().setAttribute("align", "center");

        HTML version = new HTML(Build.VERSION);
        version.getElement().setAttribute("style", "color:#cccccc;font-size:10px; align:center");
        options.add(version);
        panel.add(options);

    }

    @Override
    public void setPresenter(SignInPagePresenter presenter) {
        this.presenter = presenter;
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