package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterConfigDetails implements PropertyManagement{

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        PropertyEditor propertyEditor = new PropertyEditor(this, true);

        layout.add(propertyEditor.asWidget());
        return layout;
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void launchNewPropertyDialoge(String reference) {

    }

    @Override
    public void closePropertyDialoge() {

    }
}
