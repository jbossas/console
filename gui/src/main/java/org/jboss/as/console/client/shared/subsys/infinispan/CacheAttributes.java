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
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.viewframework.AttributeMetadata;
import org.jboss.as.console.client.shared.viewframework.EntityAttributes;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.CheckBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.ComboBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.NumberBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.TextBoxItemFactory;
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.TextItemFactory;
import org.jboss.dmr.client.ModelType;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheAttributes extends EntityAttributes {
    
    CacheAttributes() {
        super(makeBaseAttributes());
        
        setGroupedAttributes("Locking", makeLockingAttributes());
        setGroupedAttributes("Eviction", makeEvictionAttributes());
        setGroupedAttributes("Expiration", makeExpirationAttributes());
    }
    
    private static List<AttributeMetadata> makeBaseAttributes() {
        List<AttributeMetadata> attrs = new ArrayList<AttributeMetadata>();
        attrs.add(new AttributeMetadata("name", "name", ModelType.STRING, "", new TextBoxItemFactory(), new TextItemFactory(), Console.CONSTANTS.common_label_name(), true));
        return attrs;
    }
    
    private static List<AttributeMetadata> makeLockingAttributes() {
        List<AttributeMetadata> attrs = new ArrayList<AttributeMetadata>();
        
        String[] isolationTypes = new String[] {"REPEATABLE_READ"};
        attrs.add(new AttributeMetadata("isolation", "name", ModelType.STRING, "REPEATABLE_READ", new ComboBoxItemFactory(isolationTypes), Console.CONSTANTS.subsys_infinispan_isolation(), false));
        attrs.add(new AttributeMetadata("striping", "striping", ModelType.BOOLEAN, "false",  new CheckBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_striping(), false));
        attrs.add(new AttributeMetadata("acquireTimeout", "acquire-timeout", ModelType.LONG, "15000", new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_striping(), false));
        attrs.add(new AttributeMetadata("concurrencyLevel", "concurrency-level", ModelType.INT, "1000", new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_concurrencyLevel(), false));
        
        return attrs;
    }
    
    private static List<AttributeMetadata> makeEvictionAttributes() {
        List<AttributeMetadata> attrs = new ArrayList<AttributeMetadata>();
        
        String[] strategyTypes = new String[] {"NONE", "LRU"};
        attrs.add(new AttributeMetadata("evictionStrategy", "strategy", ModelType.STRING, "NONE", new ComboBoxItemFactory(strategyTypes), Console.CONSTANTS.subsys_infinispan_evictionStrategy(), false));
        attrs.add(new AttributeMetadata("maxEntries", "max-entries", ModelType.INT, "10000",  new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_concurrencyLevel(), false));

        return attrs;
    }
    
    private static List<AttributeMetadata> makeExpirationAttributes() {
        List<AttributeMetadata> attrs = new ArrayList<AttributeMetadata>();
        
        attrs.add(new AttributeMetadata("maxIdle", "max-idle", ModelType.LONG, "-1", new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_striping(), false));
        attrs.add(new AttributeMetadata("lifespan", "lifespan", ModelType.LONG, "-1", new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_striping(), false));
        attrs.add(new AttributeMetadata("interval", "interval", ModelType.LONG, "5000", new NumberBoxItemFactory(), Console.CONSTANTS.subsys_infinispan_striping(), false));
        
        return attrs;
    }
}
