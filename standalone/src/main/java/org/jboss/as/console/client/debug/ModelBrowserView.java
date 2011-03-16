package org.jboss.as.console.client.debug;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 3/16/11
 */
public class ModelBrowserView extends SuspendableViewImpl implements ModelBrowserPresenter.MyView {

    private ModelBrowserPresenter presenter;

    @Override
    public void setPresenter(ModelBrowserPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Model Browser");

        Button btn = new DefaultButton("Request Root Model");
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.requestRootModel();
            }
        });

        layout.add(btn);
        return layout;
    }

    @Override
    public void setRoot(ModelNode modelNode) {
        System.out.println("> "+ modelNode.asString());
    }
}
