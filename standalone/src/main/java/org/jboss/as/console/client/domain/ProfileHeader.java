package org.jboss.as.console.client.domain;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Heiko Braun
 * @date 2/28/11
 */
public class ProfileHeader extends HorizontalPanel {

    HTML prefix;

    public ProfileHeader(String title) {

        prefix = new HTML("");
        prefix.setStyleName("header-content");
        HTML changeButton = new HTML("(change)");

        changeButton.setStyleName("html-link");
        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.alert("Changing profiles not implemented yet!");
            }
        });

        add(prefix);
        add(changeButton);

        changeButton.getElement().getParentElement().setAttribute("style", "vertical-align:middle");

        setProfileName(title);
    }

    private void setProfileName(String title) {
        prefix.setHTML("Profile: "+ title + "&nbsp;&nbsp;");
    }


}
