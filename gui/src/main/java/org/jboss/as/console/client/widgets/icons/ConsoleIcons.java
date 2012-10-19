package org.jboss.as.console.client.widgets.icons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Heiko Braun
 * @date 7/26/11
 */
public interface ConsoleIcons extends ClientBundle {

    public static final ConsoleIcons INSTANCE =  GWT.create(ConsoleIcons.class);

    @Source("caret-down.png")
    ImageResource caretDown();

    @Source("caret-left.png")
    ImageResource caretLeft();

    @Source("caret-right.png")
    ImageResource caretRight();

    @Source("caret-up.png")
    ImageResource caretUp();

    @Source("connect.png")
    ImageResource resourceAdapter();

    @Source("off.png")
    ImageResource off();

    @Source("ok.png")
    ImageResource ok();

    @Source("play.png")
    ImageResource play();

    @Source("refresh.png")
    ImageResource refresh();

    @Source("star.png")
    ImageResource star();

    @Source("step-forward.png")
    ImageResource stepForward();

    @Source("stop.png")
    ImageResource stop();

    @Source("tablePicker.png")
    ImageResource tablePicker();
}
