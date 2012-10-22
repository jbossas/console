package org.jboss.as.console.client.widgets.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * TODO Cleanup
 * TODO Integrate pager icons
 *
 * @author Heiko Braun
 * @date 7/26/11
 */
public interface ConsoleIcons extends ClientBundle {

    public static final ConsoleIcons INSTANCE =  GWT.create(ConsoleIcons.class);

    @Source("star.png")
    ImageResource star();

    @Source("tablePicker.png")
    ImageResource tablePicker();
}
