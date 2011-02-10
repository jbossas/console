package org.jboss.as.console.client.components;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * A default {@link DisposableView} implementation.
 * Simply delegates to {@link #createWidget()} every time
 * the view should be revealed.
 *
 * @see SuspendableViewImpl
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public abstract class DisposableViewImpl extends ViewImpl implements DisposableView {

    @Override
    public Widget asWidget() {
        return createWidget();
    }
}
