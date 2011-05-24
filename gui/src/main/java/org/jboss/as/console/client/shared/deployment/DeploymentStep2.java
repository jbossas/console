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
package org.jboss.as.console.client.shared.deployment;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.as.console.client.widgets.DefaultWindow;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.FormValidation;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;

import java.util.List;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 4/8/11
 */
public class DeploymentStep2 {

    private NewDeploymentWizard wizard;
    private DefaultWindow window;
    private Form<DeploymentReference> form;
    private DeploymentViewRefresher refresher;

    public DeploymentStep2(NewDeploymentWizard wizard, DefaultWindow window, DeploymentViewRefresher refresher) {
        this.wizard = wizard;
        this.window = window;
        this.refresher = refresher;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "width:95%, margin:15px;");

        layout.add(new HTML("<h3>" + Console.CONSTANTS.common_label_step() + " 2/2: "
                + Console.CONSTANTS.common_label_verifyDeploymentNames() + "</h3>"));

        form = new Form<DeploymentReference>(DeploymentReference.class);

        TextItem hashField = new TextItem("hash", Console.CONSTANTS.common_label_key());
        DeploymentNameTextBoxItem nameField = new DeploymentNameTextBoxItem("name",
                Console.CONSTANTS.common_label_name(),
                refresher.getAllDeploymentNames());
        RuntimeNameTextBoxItem runtimeNameField = new RuntimeNameTextBoxItem("runtimeName", Console.CONSTANTS.common_label_runtimeName());

        form.setFields(hashField, nameField, runtimeNameField);

        layout.add(form.asWidget());

        // -----

        Label cancel = new Label(Console.CONSTANTS.common_label_cancel());
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                window.hide();
            }
        });

        DefaultButton submit = new DefaultButton(Console.CONSTANTS.common_label_finish(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                FormValidation validation = form.validate();
                if (!validation.hasErrors()) {
                    // proceed
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                        @Override
                        public void execute() {
                            wizard.onDeployToGroup(form.getUpdatedEntity());
                        }
                    });

                }
            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "margin-top:10px;width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);

        options.add(submit);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style", "vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");

        layout.add(options);

        return layout;
    }

    void edit(DeploymentReference ref) {
        form.edit(ref);
    }

    private static class Step2TextBoxItem extends TextBoxItem {
        protected String errorMessage = "";
        
        public Step2TextBoxItem(String name, String title) {
            super(name, title);
        }
        
        @Override
        public boolean validate(String name) {
            if (!super.validate(name)) {
                errorMessage = Console.MESSAGES.common_validation_requiredField();
                return false;
            }
            return true;
        }
        
        @Override
        public String getErrMessage() {
            return errorMessage;
        }
    }
    
    private static class DeploymentNameTextBoxItem extends Step2TextBoxItem {
        private List<String> currentDeploymentNames;

        public DeploymentNameTextBoxItem(String name, String title, List<String> currentDeploymentNames) {
            super(name, title);
            this.currentDeploymentNames = currentDeploymentNames;
        }

        @Override
        public boolean validate(String name) {
            if (!super.validate(name)) return false;
            
            if (currentDeploymentNames.contains(name)) {
                String nameField = Console.CONSTANTS.common_label_name();
                errorMessage = Console.MESSAGES.alreadyExists(nameField);
                return false;
            }
            
            return true;
        }
    }

    private static class RuntimeNameTextBoxItem extends Step2TextBoxItem {
        public RuntimeNameTextBoxItem(String name, String title) {
            super(name, title);
        }

        @Override
        public boolean validate(String name) {
            if (!super.validate(name)) return false;
            
            // need actual list of acceptable extensions like *.war, *.ear, *.rar
            // for now just make sure it is an archive name with 3 char extension
            if (!name.matches(".+\\....")) {
                String runtimeNameField = Console.CONSTANTS.common_label_runtimeName();
                errorMessage = Console.MESSAGES.mustBeDeployableArchive(runtimeNameField);
                return false;
            }
            
            return true;
        }
    }
}
