package org.jboss.as.console.client.shared.subsys.threads;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.subsys.threads.model.BoundedQueueThreadPool;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;

class PoolProperties implements SingleEntityView<BoundedQueueThreadPool> {

    private PropertyEditor editor;
    private BoundedQueueThreadPool entity;

    PoolProperties(PropertyManagement presenter) {
        this.editor = new PropertyEditor(presenter, true);
    }

    @Override
    public void updatedEntity(BoundedQueueThreadPool entity) {
        this.entity = entity;
        editor.setProperties(entity.getName(), entity.getProperties());
    }

    public BoundedQueueThreadPool getEntity() {
        if(null==entity)
            throw new RuntimeException("Edited entity is not set!");

        return entity;
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