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
package org.jboss.as.console.client.shared.subsys.logging.model;

import java.util.List;
import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for a Logger
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=logging/logger={0}")
public interface Logger extends NamedEntity {
    @Override
    @Binding(detypedName="category", key=true)
    @FormItem(defaultValue="",
             localLabel="common_label_name",
             required=true,
             formItemTypeForEdit="TEXT",
             formItemTypeForAdd="TEXT_BOX")
    public String getName();
    @Override
    public void setName(String name);

    @Binding(detypedName="level")
    @FormItem(defaultValue="INFO",
             localLabel="subsys_logging_logLevel",
             required=true,
             formItemTypeForEdit="COMBO_BOX",
             formItemTypeForAdd="COMBO_BOX")
    String getLevel();
    void setLevel(String level);

    /* Filters not implemented yet
    public String getFilter();
    public void setFilter(String filter);
    */

    @Binding(detypedName="use-parent-handlers")
    @FormItem(defaultValue="true",
            localLabel="subsys_logging_useParentHandlers",
            required=true,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public boolean isUseParentHandlers();
    public void setUseParentHandlers(boolean useParentHandlers);
    
    @Binding(detypedName="handlers", 
             listType="java.lang.String")
    @FormItem(defaultValue="",
             localLabel="subsys_logging_handlers",
             required=false,
             formItemTypeForEdit="STRING_LIST_EDITOR",
             formItemTypeForAdd="STRING_LIST_EDITOR",
             tabName="subsys_logging_handlers")
    public List<String> getHandlers();
    public void setHandlers(List<String> handlers);
}
