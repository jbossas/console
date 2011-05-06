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

package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 5/6/11
 */
public class XADatasourceStep2 {

    private NewXADatasourceWizard wizard;
    private Form<XADataSource> form;

    public XADatasourceStep2(NewXADatasourceWizard wizard) {
        this.wizard = wizard;
    }

    void edit(XADataSource dataSource) {
        form.edit(dataSource);
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:15px; vertical-align:center;width:95%");

        layout.add(new HTML("<h3>Step 2/4: Datasource Class</h3>"));

        form = new Form<XADataSource>(XADataSource.class);

        TextBoxItem driverClass = new TextBoxItem("dataSourceClass", "DataSource Class");
        TextBoxItem driverName = new TextBoxItem("driverName", "Driver Name") {
            @Override
            public boolean isRequired() {
                return false;
            }
        };
        TextBoxItem version = new TextBoxItem("driverVersion", "Version")
        {
            @Override
            public boolean isRequired() {
                return false;
            }
        };

        form.setFields(driverClass, driverName, version);

        layout.add(form.asWidget());

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if(!validation.hasErrors())
                {
                    wizard.onConfigureDriver(form.getUpdatedEntity());
                }
            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                wizard.getPresenter().closeDialogue();
            }
        };

        DialogueOptions options = new DialogueOptions(
                "Next &rsaquo;&rsaquo;",submitHandler,
                "cancel",cancelHandler
        );

        layout.add(options);

        return layout;
    }
}
