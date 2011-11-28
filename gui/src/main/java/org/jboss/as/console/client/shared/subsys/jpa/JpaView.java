package org.jboss.as.console.client.shared.subsys.jpa;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class JpaView extends DisposableViewImpl implements JpaPresenter.MyView {
    private JpaPresenter presenter;

    @Override
    public Widget createWidget() {
        return new HTML();
    }

    @Override
    public void setPresenter(JpaPresenter presenter) {
        this.presenter = presenter;
    }
}
