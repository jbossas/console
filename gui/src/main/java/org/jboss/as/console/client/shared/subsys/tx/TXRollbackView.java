package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TXRollbackView {


    private TransactionPresenter presenter;

    public TXRollbackView(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();
        layout.setStyleName("rhs-content-panel");
        return layout;
    }
}
