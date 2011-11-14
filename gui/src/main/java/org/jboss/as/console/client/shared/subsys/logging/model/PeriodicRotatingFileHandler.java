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

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * File Handler Entity
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
@Address("/subsystem=logging/periodic-rotating-file-handler={0}")
public interface PeriodicRotatingFileHandler extends NamedEntity, HasLevel {
    
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
    
    @Binding(detypedName="encoding")
    @FormItem(defaultValue="UTF-8",
              localLabel="subsys_logging_encoding",
              required=false,
              formItemTypeForEdit="TEXT_BOX",
              formItemTypeForAdd="TEXT_BOX")
    public String getEncoding();
    public void setEncoding(String encoding);
    
    /* Filters not implemented yet
    public String getFilter();
    public void setFilter(String filter);
    */
    
    @Binding(detypedName="formatter")
    @FormItem(defaultValue="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",
              localLabel="subsys_logging_formatter",
              required=true,
              formItemTypeForEdit="FREE_FORM_TEXT_BOX",
              formItemTypeForAdd="FREE_FORM_TEXT_BOX")
    public String getFormatter();
    public void setFormatter(String formatter);
    
    @Binding(detypedName="file/relative-to")
    @FormItem(defaultValue="jboss.server.log.dir",
              localLabel="subsys_logging_fileRelativeTo",
              required=true,
              formItemTypeForEdit="TEXT_BOX",
              formItemTypeForAdd="TEXT_BOX")
    public String getFileRelativeTo();
    public void setFileRelativeTo(String file);
    
    @Binding(detypedName="file/path")
    @FormItem(defaultValue="",
              localLabel="subsys_logging_filePath",
              required=true,
              formItemTypeForEdit="TEXT_BOX",
              formItemTypeForAdd="TEXT_BOX")
    public String getFilePath();
    public void setFilePath(String file);
    
    @Binding(detypedName="suffix")
    @FormItem(defaultValue="",
              localLabel="subsys_logging_suffix",
              required=true,
              formItemTypeForEdit="TEXT_BOX",
              formItemTypeForAdd="TEXT_BOX")
    public String getSuffix();
    public void setSuffix(String suffix);
    
    @Binding(detypedName="append")
    @FormItem(defaultValue="true",
            localLabel="subsys_logging_append",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public boolean isAppend();
    public void setAppend(boolean append);
    
    @Binding(detypedName="autoflush")
    @FormItem(defaultValue="true",
            localLabel="subsys_logging_autoFlush",
            required=false,
            formItemTypeForEdit="CHECK_BOX",
            formItemTypeForAdd="CHECK_BOX")
    public boolean isAutoFlush();
    public void setAutoFlush(boolean autoFlush);
}
