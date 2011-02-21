package org.jboss.as.console.client.components;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;

/**
 * A title bar to be displayed at the top of a content view -
 * contains a label and/or an icon.
 *
 * @author Heiko Braun
 */
public class TitleBar extends HTML {


    public TitleBar(String title) {
        super(title);
        setStyleName("title-bar");
    }

    public void setIcon(ImageResource icon) {

    }

}
