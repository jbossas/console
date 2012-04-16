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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.logging.LoggingLevelProducer.LogLevelConsumer;
import org.jboss.as.console.client.shared.subsys.logging.model.PeriodicRotatingFileHandler;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Subview for PeriodicRotatingFileHandler.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class PeriodicRotatingFileHandlerSubview extends AbstractHandlerSubview<PeriodicRotatingFileHandler> implements FrameworkView, LogLevelConsumer, HandlerProducer {
	
    TextBoxItem suffixAdd = new SuffixValidatingTextItem();
    TextBoxItem suffixEdit = new SuffixValidatingTextItem();
    
    public PeriodicRotatingFileHandlerSubview(ApplicationMetaData applicationMetaData,
                              DispatchAsync dispatcher,
                              HandlerListManager handlerListManager) {
        super(PeriodicRotatingFileHandler.class, applicationMetaData, dispatcher, handlerListManager);
    }

    @Override
    protected String provideDescription() {
         return Console.CONSTANTS.subsys_logging_periodicRotatingFileHandlers_desc();
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_logging_periodicRotatingFileHandlers();
    }

    @Override
    protected FormAdapter<PeriodicRotatingFileHandler> makeAddEntityForm() {
        Form<PeriodicRotatingFileHandler> form = new Form(type);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                       levelItemForAdd,
                       formMetaData.findAttribute("filePath").getFormItemForAdd(),
                       formMetaData.findAttribute("fileRelativeTo").getFormItemForAdd(),
//                       formMetaData.findAttribute("suffix").getFormItemForAdd());
                       new FormItem[]{(FormItem<String>)suffixAdd});
        return form;
    }
    
    @Override
    protected FormAdapter<PeriodicRotatingFileHandler> makeEditEntityDetailsForm() {
        Form<PeriodicRotatingFileHandler> form = new Form(type);
        form.setNumColumns(2);
        FormMetaData attributes = getFormMetaData();

        // add base items to form
        FormItem[][] items = new FormItem[attributes.getBaseAttributes().size()][];
        int i=0;
        for (PropertyBinding attrib : attributes.getBaseAttributes()) {
        	if(attrib.getDetypedName().equals("suffix")){
        		items[i++] = new FormItem[]{(FormItem<String>)suffixEdit};
   				continue;
        	}
            items[i++] = attrib.getFormItemForEdit(this);
        }
        form.setFields(items);

        // add grouped items to form
        for (String subgroup : attributes.getGroupNames()) {
            FormItem[][] groupItems = new FormItem[attributes.getGroupedAttribtes(subgroup).size()][];
            int j=0;
            for (PropertyBinding attrib : attributes.getGroupedAttribtes(subgroup)) {
                groupItems[j++] = attrib.getFormItemForEdit(this);
            }
            form.setFieldsInGroup(subgroup, groupItems);
        }

        return form;
    }
}
