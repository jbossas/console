package org.jboss.as.console.client.shared.subsys.naming;

import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.ballroom.client.layout.RHSContentPanel;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class JndiView extends DisposableViewImpl implements JndiPresenter.MyView {

    private JndiPresenter presenter;
    private VerticalPanel container;

    @Override
    public void setPresenter(JndiPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        RHSContentPanel layout = new RHSContentPanel("JNDI View");
        container = new VerticalPanel();
        container.setStyleName("fill-layout");

        layout.add(container);

        return layout;
    }

    @Override
    public void setJndiTree(CellTree tree) {
        container.clear();
        container.add(tree);
    }
}
