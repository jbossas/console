package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyRecord;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class ConnectionProperties {

    private DataSourcePresenter presenter;
    private PropertyEditor propEditor = null;
    private Widget widget;

    public ConnectionProperties(DataSourcePresenter presenter) {
        this.presenter = presenter;
        propEditor = new PropertyEditor(presenter, true);
        propEditor.setHelpText("JDBC connection properties");
        widget = propEditor.asWidget();
        propEditor.setAllowEditProps(false);
    }

    Widget asWidget() {
       return widget;
    }

    public void setProperties(String reference, List<PropertyRecord> properties) {

        if(propEditor!=null)
            propEditor.setProperties(reference, properties);
    }


    public void clearProperties() {
        propEditor.clearValues();
    }
}
