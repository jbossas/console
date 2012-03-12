package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
public class EmbeddedPropertyView<T extends HasProperties, NamedEntity>
        implements PropertyManagement, SingleEntityView<T> {


    private FrameworkPresenter presenter;
    private DefaultWindow propertyWindow;
    private PropertyEditor editor;
    private T entity;

    public EmbeddedPropertyView(FrameworkPresenter presenter) {
        this.presenter = presenter;
        this.editor = new PropertyEditor(this, true);
    }

    @Override
    public void updatedEntity(T entity) {
        this.entity = entity;
        this.editor.setProperties("no-ref", entity.getProperties());
    }

    public PropertyEditor getEmbeddedPropertyEditor() {
        return editor;
    }

    @Override
    public String getTitle() {
        return "Properties";
    }

    @Override
    public Widget asWidget() {
        Widget widget = editor.asWidget();
        editor.setAllowEditProps(false);
        return widget;

    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

        closePropertyDialoge();

        List<PropertyRecord> props = (entity.getProperties() != null) ?
                entity.getProperties() : new ArrayList<PropertyRecord>();
        props.add(prop);

        Map<String,Object> changeset = new HashMap<String,Object>();
        changeset.put("properties", props);
        presenter.getEntityBridge().onSaveDetails(entity, changeset);
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {

        List<PropertyRecord> updatedProperties = new ArrayList<PropertyRecord>();
        for(PropertyRecord property : entity.getProperties())
        {
            if(!property.getKey().equals(prop.getKey()))
                updatedProperties.add(property);
        }

        Map<String,Object> changeset = new HashMap<String,Object>();
        changeset.put("properties", updatedProperties);
        presenter.getEntityBridge().onSaveDetails(entity, changeset);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        propertyWindow = new DefaultWindow(Console.MESSAGES.createTitle("Thread Pool Property"));
        propertyWindow.setWidth(320);
        propertyWindow.setHeight(240);

        propertyWindow.trapWidget(
                new NewPropertyWizard(this, reference).asWidget()
        );

        propertyWindow.setGlassEnabled(true);
        propertyWindow.center();
    }

    @Override
    public void closePropertyDialoge() {
        propertyWindow.hide();
    }


}

