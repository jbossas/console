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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.logging.model.LoggingHandler;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.StatusItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

/**
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class HandlerDetails {
    
    private LoggingPresenter presenter;
    private Form<LoggingHandler> form;
    private ToolButton editBtn;
    private ToolButton cancelBtn;
    private LoggingHandler editedHandler;
    
    public HandlerDetails(LoggingPresenter presenter) {
        this.presenter = presenter;
        form = new Form(LoggingHandler.class);
        form.setNumColumns(2);
    }
    
    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");
        
        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(editBtn.getText().equals(Console.CONSTANTS.common_label_edit()))
                    presenter.onEditHandler();
                else {
                    editedHandler = form.getEditedEntity();
                    presenter.onSaveHandlerDetails(form.getEditedEntity().getName(), form.getEditedEntity().getType(), form.getChangedValues());
                }
            }
        };
        editBtn.addClickHandler(editHandler);
        
        cancelBtn = new ToolButton(Console.CONSTANTS.common_label_cancel());
        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.cancel();
                HandlerDetails.this.setEnabled(false);
            }        
        };
        cancelBtn.addClickHandler(cancelHandler);
        cancelBtn.setVisible(false);
        
        detailToolStrip.addToolButton(editBtn);
        detailToolStrip.addToolButton(cancelBtn);
        detailPanel.add(detailToolStrip);
        
        TextItem nameItem = new TextItem("name", Console.CONSTANTS.common_label_name());
        TextItem typeItem = new TextItem("type", Console.CONSTANTS.subsys_logging_type());

        ComboBoxItem logLevelItem = new ComboBoxItem("level", Console.CONSTANTS.subsys_logging_logLevel());
        logLevelItem.setValueMap(LogLevel.STRINGS);
        
        StatusItem flushItem = new StatusItem("autoflush", Console.CONSTANTS.subsys_logging_autoFlush());

        TextItem formatterItem = new TextItem("formatter", Console.CONSTANTS.subsys_logging_formatter());
        TextItem encodingItem = new TextItem("encoding", Console.CONSTANTS.subsys_logging_encoding());
        TextItem queueItem = new TextItem("queueLength", Console.CONSTANTS.subsys_logging_queueLength());

        form.setFields(nameItem, typeItem, logLevelItem, flushItem, formatterItem, encodingItem, queueItem);
        
        setEnabled(false);

         final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "logging");
                        address.add("console-handler", "*");
                        return address;
                    }
                }, form
        );
        detailPanel.add(helpPanel.asWidget());

        detailPanel.add(form.asWidget());
        
        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }
    
    public void bind(CellTable<LoggingHandler> handlerTable) {
        form.bind(handlerTable);
    }

    public void setEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        cancelBtn.setVisible(isEnabled);

        if(isEnabled) {
            editBtn.setText(Console.CONSTANTS.common_label_save());
        } else {
            editBtn.setText(Console.CONSTANTS.common_label_edit());
        }
    }
    
    public LoggingHandler getEditedLogger() {
        return this.editedHandler;
    }
}
