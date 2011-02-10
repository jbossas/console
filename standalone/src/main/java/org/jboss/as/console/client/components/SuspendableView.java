package org.jboss.as.console.client.components;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.View;

/**
 * A suspendable view retains a specific widget instance
 * when the view is hidden and notify the view through {@link #onResume()}
 * when it's going to be revealed again.
 *
 * @see DisposableView
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public interface SuspendableView extends View {

    /**
     * Called upon first creation of the widget
     * @return
     */
    Widget createWidget();

    /**
     * Called every time a suspended view should resume
     * (is revealed again)
     */
    void onResume();


}
