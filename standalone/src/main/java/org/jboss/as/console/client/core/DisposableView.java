package org.jboss.as.console.client.core;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.View;

/**
 * A disposable view will create a widget instance every time
 * the view is going to be revealed.
 *
 * @see SuspendableView
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public interface DisposableView extends View {

    /**
     * Called when the view is  going to be revealed.
     *
     * @return a widget instance
     */
    Widget createWidget();
}
