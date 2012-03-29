package org.jboss.as.console.client.extension;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.extension.model.DataModel;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;

/**
 * @author Heiko Braun
 * @date 3/29/12
 */
public class HelloWorldView extends SuspendableViewImpl implements HelloWorldPresenter.MyView {
    private HelloWorldPresenter presenter;
    private Label label;

    @Override
    public void setPresenter(HelloWorldPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setData(DataModel data) {
        label.setText(data.getGreeting());
    }

    @Override
    public Widget createWidget() {

        label = new Label();

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Extension Example")
                .setHeadlineWidget(label)
                .setDescription("A simple extension example that shows the most important coding patterns.");

        return layout.build();
    }
}
