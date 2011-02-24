package org.jboss.as.console.client.core;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * A default {@link SuspendableView} implementation.
 * Subclasses can override {@link #onResume()} but need to
 * provide {@link #createWidget()}
 *
 * @author Heiko Braun
 * @date 2/10/11
 */
public abstract class SuspendableViewImpl extends ViewImpl implements SuspendableView {

    protected Widget widgetInstance = null;

    @Override
    public Widget asWidget() {
        if(null==widgetInstance)
            widgetInstance = createWidget();
        else
            onResume();

        return widgetInstance;
    }

    @Override
    public void onResume() {

    }
}
