package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterConnectionProperties  {

    private ResourceAdapterPresenter presenter;
    private PropertyEditor propertyEditor;
    private ResourceAdapter currentSelection;
    private PropertyManagement delegate;

    public AdapterConnectionProperties(ResourceAdapterPresenter presenter, PropertyManagement delegate) {
        this.presenter = presenter;
        this.delegate = delegate;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        propertyEditor = new PropertyEditor(delegate, true);
        layout.add(propertyEditor.asWidget());
        return layout;
    }

    public void setEnabled(boolean b) {
        propertyEditor.setEnabled(b);
    }

    public void updateFrom(List<PropertyRecord> properties) {
        propertyEditor.setProperties("", properties);
    }
}
