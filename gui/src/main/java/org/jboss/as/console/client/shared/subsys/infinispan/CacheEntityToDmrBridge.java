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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.viewframework.DmrCallback;
import org.jboss.dmr.client.Property;
import static org.jboss.dmr.client.ModelDescriptionConstants.*;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.infinispan.model.LocalCache;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.dmr.client.ModelNode;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheEntityToDmrBridge<T extends LocalCache> extends EntityToDmrBridgeImpl<T> {

    public CacheEntityToDmrBridge(ApplicationMetaData propertyMetadata, Class<? extends T> type, FrameworkView view,
                                 DispatchAsync dispatcher) {
        super(propertyMetadata, type, view, dispatcher);
    }
    
    @Override
    public String getName(T entity) {
        return  entity.getCacheContainer() + "/" + entity.getName();
    }
    
    @Override
    protected ModelNode getResourceAddress(String name) {
        String[] compoundName = name.split("/");
        return address.asResource(Baseadress.get(), compoundName[0], compoundName[1]);
    }
    
    @Override
    public void loadEntities(String nameEditedOrAdded) {
        this.nameOfLastEdited = nameEditedOrAdded;
        ModelNode operation = address.asResource(Baseadress.get(), "*", "*");
        
        operation.get(OP).set(READ_RESOURCE_OPERATION);

        if (formMetaData.isFlattened()) {
            operation.get(RECURSIVE).set(true);
        } else {
            // Runtime information is only available in the DMR on non-recursive reads
            operation.get(INCLUDE_RUNTIME).set(true);
        }

        dispatcher.execute(new DMRAction(operation), new DmrCallback() {
            @Override
            public void onDmrSuccess(ModelNode response) {
               onLoadEntitiesSuccess(response);
            }
        });
        
    }
    
    @Override
    protected void onLoadEntitiesSuccess(ModelNode response) {
        List<T> entities = new ArrayList<T>();
        for (ModelNode entity : response.get(RESULT).asList()) {
            for (Property addressProp : entity.get(ADDRESS).asPropertyList()) {
                entity.get(RESULT).get(addressProp.getName()).set(addressProp.getValue());
            }
            
            entities.add(entityAdapter.fromDMR(entity.get(RESULT)));
        }
        
        entityList = sortEntities(entities);
        view.refresh();
    }

    
    
}
