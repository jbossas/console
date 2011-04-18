/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 4/18/11
 */
public class DatasourceStep1 {


    NewDatasourceWizard wizard;

    public DatasourceStep1(NewDatasourceWizard wizard) {
        this.wizard = wizard;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:15px; vertical-align:center;width:95%");

        layout.add(new HTML("<h3>Step 1/3: Datasource Attributes</h3>"));

        final Form<DataSource> form = new Form<DataSource>(DataSource.class);

        TextBoxItem name = new TextBoxItem("name", "Name");
        TextBoxItem jndiName = new TextBoxItem("jndiName", "JNDI Name") {
            @Override
            public boolean validate(String value) {
                boolean notEmpty = super.validate(value);

                return notEmpty && !value.contains(":") && !value.startsWith("/");
            }

            @Override
            public String getErrMessage() {
                return "Not empty, no prefix, no leading slash";
            }
        };
        CheckBoxItem enabled = new CheckBoxItem("enabled", "Enabled?");
        enabled.setValue(Boolean.TRUE);

        form.setFields(name, jndiName, enabled);

        layout.add(form.asWidget());

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if(!validation.hasErrors())
                {
                    wizard.onConfigureBaseAttributes(form.getUpdatedEntity());
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
