package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterConfigDetails {

    private ResourceAdapterPresenter presenter;
    private PropertyEditor propertyEditor;

    public AdapterConfigDetails(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        propertyEditor = new PropertyEditor(presenter, true);
        layout.add(propertyEditor.asWidget());
        return layout;
    }

    public void setAdapter(ResourceAdapter selectedRa) {
        propertyEditor.setProperties(selectedRa.getArchive(), selectedRa.getProperties());
    }

    public void setEnabled(boolean b) {
        propertyEditor.setEnabled(b);
    }
}
