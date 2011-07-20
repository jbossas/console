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

import com.google.gwt.autobean.shared.AutoBean;
import java.util.List;
import org.jboss.as.console.client.widgets.forms.FormAdapter;

/**
 * Implementors of this class know how to perform CRUD and other
 * operations on the given type T.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface EntityBridge<T> {
    
    /**
     * Add the entity from the form.
     */
    public void onAdd(FormAdapter<T> form);

    /**
     * Assign the form-selected handler to the entity.
     * 
     * @param form 
     */
    public void onAssignHandler(FormAdapter<T> form);
    
    /**
     * Unassign the form-selected handler to the entity.
     * 
     * @param form 
     */
    public void onUnassignHandler(FormAdapter<T> form);
    
    /**
     * Prepare for editing.
     */
    public void onEdit();

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
     * Create an empty entity.  Used for add operation.
     * 
     * @return A new instance of the entity.
     */
    public AutoBean<T> newEntity();
    
    /**
     * Determine if a handler can be assigned to the given entity.
     * @param entity The entity.
     * @return <code>true</code> if a handler can be assigned, <code>false</code> otherwise.
     */
    public boolean isAssignHandlerAllowed(T entity);
    
    /**
     * Get the names of the handlers assigned to the entity.
     * @param entity The entity.
     * @return The Handler names.
     */
    public List<String> getAssignedHandlers(T entity);
}
