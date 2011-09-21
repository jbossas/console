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

import java.util.Collections;
import java.util.List;

/**
 * Aggregator for AttributeMetadata instances.  Be careful to only create one instance of
 * this class for each Entity.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityAttributes {
    
    private List<AttributeMetadata> attributes;
    
    public EntityAttributes(List<AttributeMetadata> attributes) {
        this.attributes = attributes;
    }
    
    /**
     * Find an AttributeMetadata with the given bean property.
     * @param beanPropName The name of the bean property.
     * @return The AttributeMetaData
     * @throws IllegalArgumentException if the AttributeMetadata is not found.
     */
    public AttributeMetadata findAttribute(String beanPropName) {
        for (AttributeMetadata attrib : attributes) {
            if (attrib.getBeanPropName().equals(beanPropName)) return attrib;
        }
        
        throw new IllegalArgumentException("Unknown Attribute with beanPropName name " + beanPropName);
    }
    
    public List<AttributeMetadata> getAllAttributes() {
        return Collections.unmodifiableList(this.attributes);
    }
}
