package org.jboss.as.console.client.shared.subsys.threads;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;

class PoolProperties implements SingleEntityView<BoundedQueueThreadPool> {

    private PropertyEditor editor;

    PoolProperties(PropertyManagement presenter) {
        this.editor = new PropertyEditor(presenter, true);
    }

    @Override
    public void updatedEntity(BoundedQueueThreadPool entity) {
        editor.setProperties(entity.getName(), entity.getProperties());
    }

    @Override
    public String getTitle() {
        return "Properties";
    }

    @Override
    public Widget asWidget() {
        return editor.asWidget();
    }
}