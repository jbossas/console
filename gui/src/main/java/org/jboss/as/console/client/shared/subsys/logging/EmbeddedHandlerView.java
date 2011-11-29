/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
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
import org.jboss.as.console.client.Console;

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
        return Console.CONSTANTS.subsys_logging_handlers();
    }

    @Override
    public Widget asWidget() {
        this.listView = new EmbeddedListView(Console.CONSTANTS.subsys_logging_handlers(), 5, true, this);
        this.listView.setValueColumnHeader(Console.CONSTANTS.common_label_name());
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