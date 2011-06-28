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

import org.jboss.as.console.client.widgets.forms.Form;

/**
 * Implementors of this class know how to perform specific CRUD 
 * operations on the given type T.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public interface LoggingCmdAdapter<T> {
    
    /**
     * Add the entity from the form.
     */
    public void onAdd(Form<T> form);
    
    /**
     * Prepare for editing.
     */
    public void onEdit();

    /**
     * Save the edited form.
     * 
     * @param form  The form.
     */
    public void onSaveDetails(Form<T> form);
    
    /**
     * Remove the entity in the form.
     * 
     * @param form The form.
     */
    public void onRemove(Form<T> form);
    
    /**
     * Get the name of the given entity.  We need this because
     * GWT doesn't (easily) support reflection.
     * 
     * @param entity The entity.
     * @return The name.
     */
    public String getName(T entity);
}
