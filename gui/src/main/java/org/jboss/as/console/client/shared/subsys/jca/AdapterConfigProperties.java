package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.ResourceAdapter;

/**
 * @author Heiko Braun
 * @date 7/19/11
 */
public class AdapterConfigProperties implements PropertyManagement {

    private ResourceAdapterPresenter presenter;
    private PropertyEditor propertyEditor;
    private ResourceAdapter currentSelection;

    public AdapterConfigProperties(ResourceAdapterPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        propertyEditor = new PropertyEditor(this, true);
        layout.add(propertyEditor.asWidget());
        return layout;
    }

    public void setAdapter(ResourceAdapter selectedRa) {
        this.currentSelection = selectedRa;
        propertyEditor.setProperties(selectedRa.getArchive(), selectedRa.getProperties());
    }

    public void setEnabled(boolean b) {
        propertyEditor.setEnabled(b);
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        presenter.createProperty(currentSelection, prop);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        presenter.onDeleteProperty(currentSelection, prop);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        presenter.launchNewPropertyDialoge(currentSelection);
    }

    @Override
    public void closePropertyDialoge() {
        presenter.closePropertyDialoge();
    }
}
