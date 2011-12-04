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
 * Async Handler Entity
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=logging/async-handler={0}")
public interface AsyncHandler extends NamedEntity, HasLevel, HasHandlers {
    
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(defaultValue="",
              localLabel="common_label_name",
              required=true,
              formItemTypeForEdit="TEXT",
              formItemTypeForAdd="TEXT_BOX")
    public String getName();
    @Override
    public void setName(String name);
    
    @Override
    @Binding(detypedName="level")
    @FormItem(defaultValue="INFO",
              localLabel="subsys_logging_logLevel",
              required=true,
              formItemTypeForEdit="COMBO_BOX",
              formItemTypeForAdd="COMBO_BOX")
    public String getLevel();
    @Override
    public void setLevel(String logLevel);
    
    /* Filters not implemented yet
    public String getFilter();
    public void setFilter(String filter);
    */
    
    @Binding(detypedName="queue-length")
    @FormItem(defaultValue="512",
              localLabel="subsys_logging_queueLength",
              required=true,
              formItemTypeForEdit="NUMBER_BOX",
              formItemTypeForAdd="NUMBER_BOX")
    public Integer getQueueLength();
    public void setQueueLength(Integer queueLength);
    
    @Binding(detypedName="overflow-action")
    @FormItem(defaultValue="BLOCK",
              localLabel="subsys_logging_overflowAction",
              required=true,
              formItemTypeForEdit="COMBO_BOX",
              formItemTypeForAdd="COMBO_BOX",
              acceptedValues={"BLOCK", "DISCARD"})
    public String getOverflowAction();
    public void setOverflowAction(String overflowAction);
               
    @Override
    @Binding(detypedName="subhandlers", 
             listType="java.lang.String")
    @FormItem(defaultValue="",
             localLabel="subsys_logging_subhandlers",
             required=false,
             formItemTypeForEdit="STRING_LIST_EDITOR",
             formItemTypeForAdd="STRING_LIST_EDITOR",
             tabName="CUSTOM")
    public List<String> getHandlers();
    @Override
    public void setHandlers(List<String> handlers);
}
