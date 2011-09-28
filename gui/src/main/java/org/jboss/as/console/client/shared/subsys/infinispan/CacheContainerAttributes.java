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
import org.jboss.as.console.client.shared.viewframework.FormItemFactories.TextItemFactory;
import org.jboss.dmr.client.ModelType;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class CacheContainerAttributes extends EntityAttributes {
    
    CacheContainerAttributes() {
        super(makeBaseAttributes());
    }
    
    private static List<AttributeMetadata> makeBaseAttributes() {
        List<AttributeMetadata> attrs = new ArrayList<AttributeMetadata>();
        attrs.add(new AttributeMetadata("name", "name", ModelType.STRING, "", new TextItemFactory(), new TextItemFactory(), Console.CONSTANTS.common_label_name(), true));
        attrs.add(new AttributeMetadata("jndiName", "jndi-name", ModelType.STRING, "", new TextItemFactory(), Console.CONSTANTS.subsys_infinispan_jndiName(), false));
        attrs.add(new AttributeMetadata("listenerExecutor", "listener-executor", ModelType.STRING, "", new TextItemFactory(), Console.CONSTANTS.subsys_infinispan_listenerExecutor(), false));
        attrs.add(new AttributeMetadata("evictionExecutor", "eviction-executor", ModelType.STRING, "", new TextItemFactory(), Console.CONSTANTS.subsys_infinispan_evictionExecutor(), false));
        attrs.add(new AttributeMetadata("replicationQueueExecutor", "replication-queue-executor", ModelType.STRING, "", new TextItemFactory(), Console.CONSTANTS.subsys_infinispan_replicationQueueExecutor(), false));
        attrs.add(new AttributeMetadata("defaultCache", "default-cache", ModelType.STRING, "", new TextItemFactory(), Console.CONSTANTS.subsys_infinispan_default_cache(), true));
        return attrs;
    }
}
