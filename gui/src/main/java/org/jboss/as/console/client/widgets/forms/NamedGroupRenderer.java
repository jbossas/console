package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.forms.DefaultGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.GroupRenderer;
import org.jboss.ballroom.client.widgets.forms.PlainFormView;
import org.jboss.ballroom.client.widgets.forms.RenderMetaData;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/16/11
 */
public class NamedGroupRenderer implements GroupRenderer {

    private DefaultGroupRenderer delegate;

    public NamedGroupRenderer() {
        this.delegate = new DefaultGroupRenderer();
    }

    @Override
    public Widget render(RenderMetaData metaData, String groupName, Map<String, FormItem> groupItems) {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        Label label = new Label(groupName);
        label.setStyleName("form-group-label");
        panel.add(label);
        panel.add(delegate.render(metaData, groupName, groupItems));
        return panel;
    }

    @Override
    public Widget renderPlain(RenderMetaData metaData, String groupName, PlainFormView plainView) {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        Label label = new Label(groupName);
        label.setStyleName("form-group-label");
        panel.add(label);
        panel.add(delegate.renderPlain(metaData, groupName, plainView));
        return panel;
    }
}