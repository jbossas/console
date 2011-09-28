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
package org.jboss.as.console.client.shared.subsys.infinispan;

import java.util.List;
import org.jboss.as.console.client.shared.viewframework.EntityAttributes;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheBridge implements EntityToDmrBridge {

    @Override
    public Object findEntity(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EntityAttributes getEntityAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List getEntityList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName(Object entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getNameOfLastEdited() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadEntities(String nameEditedOrAdded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object newEntity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onAdd(FormAdapter form) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onEdit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onRemove(FormAdapter form) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onSaveDetails(FormAdapter form) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
