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
package org.jboss.as.console.client.shared.viewframework;

import java.util.List;

import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

/**
 * Implementors of this class know how to perform CRUD and other
 * operations on the given type T.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface EntityToDmrBridge<T> {

    /**
     * Get the FormMetaData associated with the type.
     * @return The FormMetaData
     */
    public FormMetaData getEntityAttributes();

    /**
     * Query the server for the full entity list.  If successful, this method should call
     * FrameworkView.refresh();
     */
    public void loadEntities(String nameEditedOrAdded);

    /**
     * Get the name of the Entity just edited/added.  This is used to keep the selection on the
     * proper entity when the list of entities is refreshed.  Note that during refresh, the list
     * contains new objects.
     * @return The name, or <code>null</code> if no entity has been edited.
     */
    public String getNameOfLastEdited();

    /**
     * Get the list of all entities as of the last time refreshView() was called.
     * @return The entity list.
     */
    public List<T> getEntityList();

    /**
     * Find the entity given its name.
     * @param name
     * @return The entity, or <code>null</code> if not found.
     */
    public T findEntity(String name);

    /**
     * Add the entity from the add form.
     */
    public void onAdd(FormAdapter<T> form);

    /**
     * Prepare for editing.
     */
    public void onEdit();

    /**
     * Cancel an edit
     */
    public void onCancel();

    /**
     * Save the edited form.
     *
     * @param form  The form.
     */
    public void onSaveDetails(FormAdapter<T> form);

    /**
     * Remove the entity in the form.
     *
     * @param form The form.
     */
    public void onRemove(FormAdapter<T> form);

    /**
     * Get the name of the given entity.  We need this because
     * GWT doesn't (easily) support reflection.
     *
     * @param entity The entity.
     * @return The name.
     */
    public String getName(T entity);

    /**
     * Create a new entity.  Used during add operation to put a new instance
     * of an entity in an Add dialog.
     *
     * @return A new instance of the entity.
     */
    public T newEntity();

}
