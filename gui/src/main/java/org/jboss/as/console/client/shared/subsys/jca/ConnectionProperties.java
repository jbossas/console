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
    private PropertyEditor propEditor;

    public ConnectionProperties(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        propEditor = new PropertyEditor(presenter, true);
        propEditor.setHelpText("JDBC connection properties");
        return propEditor.asWidget();

    }

    public void setProperties(String reference, List<PropertyRecord> properties) {
        propEditor.setProperties(reference, properties);
    }
}
