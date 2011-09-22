package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 7/20/11
 */
public class AdapterStep2 implements PropertyManagement {

    private NewAdapterWizard parent;
    private PropertyEditor propEditor;
    private List<PropertyRecord> properties;
    private BeanFactory factory = GWT.create(BeanFactory.class);

    public AdapterStep2(NewAdapterWizard parent) {
        this.parent = parent;
        this.properties = new ArrayList<PropertyRecord>();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        properties.remove(prop);
        propEditor.setProperties("", properties);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }
    
    @Override
    public void launchNewPropertyDialoge(String reference) {
        PropertyRecord proto = factory.property().as();
        proto.setKey("name");
        proto.setValue("value");

        properties.add(proto);
        propEditor.setProperties("", properties);
    }

    @Override
    public void closePropertyDialoge() {

    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h3>"+ Console.CONSTANTS.subsys_jca_ra_step2()+"</h3>"));

        propEditor = new PropertyEditor(this, true);

        Widget widget = propEditor.asWidget();
        layout.add(widget);

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.onCompleteStep2(properties);
            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.getPresenter().closeDialoge();
            }
        };

        DialogueOptions options = new DialogueOptions( submitHandler, cancelHandler);

        return new WindowContentBuilder(layout,options).build();
    }
}
