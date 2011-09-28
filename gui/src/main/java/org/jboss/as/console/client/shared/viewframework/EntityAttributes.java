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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Aggregator for AttributeMetadata instances.  Be careful to only create one instance of
 * this class for each Entity.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityAttributes {
    
    private List<AttributeMetadata> baseAttributes;
    private Map<String, List<AttributeMetadata>> groupedAttributes = new LinkedHashMap<String, List<AttributeMetadata>>();
    
    public EntityAttributes(List<AttributeMetadata> baseAttributes) {
        this.baseAttributes = baseAttributes;
    }

    public List<AttributeMetadata> getBaseAttributes() {
        return Collections.unmodifiableList(this.baseAttributes);
    }
    
    public void setGroupedAttributes(String groupName, List<AttributeMetadata> attributes) {
        groupedAttributes.put(groupName, attributes);
    }
    
    public List<AttributeMetadata> getGroupedAttribtes(String groupName) {
        return groupedAttributes.get(groupName);
    }
    
    /**
     * Returns all group names.  Calling iterator() on the returned Set
     * will give you an iterator that maintains the names in the order they were added.
     * 
     * @return The group names.
     */
    public Set<String> getGroupNames() {
        return groupedAttributes.keySet();
    }
    
    /**
     * Find an AttributeMetadata with the given bean property.
     * @param beanPropName The name of the bean property.
     * @return The AttributeMetaData
     * @throws IllegalArgumentException if the AttributeMetadata is not found.
     */
    public AttributeMetadata findAttribute(String beanPropName) {
        for (AttributeMetadata attrib : baseAttributes) {
            if (attrib.getBeanPropName().equals(beanPropName)) return attrib;
        }
        
        for (Map.Entry<String, List<AttributeMetadata>> entry : groupedAttributes.entrySet()) {
            for (AttributeMetadata attrib : entry.getValue()) {
                if (attrib.getBeanPropName().equals(beanPropName)) return attrib;
            }
        }
        
        throw new IllegalArgumentException("Unknown Attribute with beanPropName name " + beanPropName);
    }
    
}
