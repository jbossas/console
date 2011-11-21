package org.jboss.as.console.client.shared.subsys.logging;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.logging.model.HasHandlers;
import org.jboss.as.console.client.shared.viewframework.EmbeddedListView;
import org.jboss.as.console.client.shared.viewframework.FrameworkPresenter;
import org.jboss.as.console.client.shared.viewframework.SingleEntityView;
import org.jboss.ballroom.client.widgets.forms.ListManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/21/11
 */
class EmbeddedHandlerView<T extends HasHandlers> implements SingleEntityView<T>, ListManagement<String> {

    private EmbeddedListView listView;
    private T editedEntity;
    private FrameworkPresenter presenter;

    EmbeddedHandlerView(FrameworkPresenter presenter) {
        this.presenter = presenter;
    }

    public T getEditedEntity() {
        return editedEntity;
    }

    public EmbeddedListView getListView() {
        return listView;
    }

    public void updatedEntity(T entity) {
        this.editedEntity = entity;
        this.listView.setValue(entity.getHandlers());
    }

    @Override
    public String getTitle() {
        return "Handler";
    }

    @Override
    public Widget asWidget() {
        this.listView = new EmbeddedListView("Handler", 5, true, this);
        this.listView.setValueColumnHeader("Type");
        Widget widget = listView.asWidget();
        widget.addStyleName("fill-layout-width");
        return widget;
    }

    @Override
    public void onCreateItem(String item) {
        List<String> values = this.listView.getValue();
        values.add(item);

        T entity = this.editedEntity;
        entity.setHandlers(values);

        Map<String, Object> changes = new HashMap<String,Object>();
        changes.put("handlers", values);

        presenter.getEntityBridge().onSaveDetails(entity, changes);

    }

    @Override
    public void onDeleteItem(String item) {
        List<String> values = this.listView.getValue();
        values.remove(item);

        T entity = this.editedEntity;
        entity.setHandlers(values);

        Map<String, Object> changes = new HashMap<String,Object>();
        changes.put("handlers", values);

        presenter.getEntityBridge().onSaveDetails(entity, changes);

    }

    @Override
    public void launchNewItemDialoge() {
        this.listView.launchNewItemDialoge();
    }

    @Override
    public void closeNewItemDialoge() {
        this.listView.closeNewItemDialoge();
    }
}