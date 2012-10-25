package org.jboss.as.console.client.debug;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

/**
 * @author Heiko Braun
 * @date 6/15/12
 */
public class DebugPresenter extends PresenterWidget<DebugPresenter.MyView> {

    private boolean hasBeenRevealed;

    public interface MyView extends PopupView {
        void setPresenter(DebugPresenter presenter);
    }

    @Inject
    public DebugPresenter(
            EventBus eventBus, MyView view) {
        super(eventBus, view);

    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReveal() {
        if(!hasBeenRevealed)
        {
            hasBeenRevealed = true;
            onRefresh();
        }
    }

    public void onRefresh() {

    }
}

