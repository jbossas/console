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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

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
    private boolean isUpdate;

    public DeploymentStep2(NewDeploymentWizard wizard, DefaultWindow window, DeploymentViewRefresher refresher, boolean isUpdate) {
        this.wizard = wizard;
        this.window = window;
        this.refresher = refresher;
        this.isUpdate = isUpdate;
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
                refresher.getAllDeploymentNames(),
                isUpdate);
        RuntimeNameTextBoxItem runtimeNameField = new RuntimeNameTextBoxItem("runtimeName", Console.CONSTANTS.common_label_runtimeName(), isUpdate);

        form.setFields(hashField, nameField, runtimeNameField);

        layout.add(form.asWidget());

        // -----

        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                window.hide();
            }
        };
        ClickHandler submitHandler = new ClickHandler() {

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
        };

        DialogueOptions options = new DialogueOptions(submitHandler, cancelHandler);

        return new WindowContentBuilder(layout, options).build();
    }

    void edit(DeploymentReference ref) {
        form.edit(ref);
    }

    private static class Step2TextBoxItem extends TextBoxItem {
        protected String errorMessage = "";
        protected boolean isUpdate;

        public Step2TextBoxItem(String name, String title, boolean isUpdate) {
            super(name, title);
            this.isUpdate = isUpdate;
            setEnabled(!isUpdate);
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

        public DeploymentNameTextBoxItem(String name, String title, List<String> currentDeploymentNames, boolean isUpdate) {
            super(name, title, isUpdate);
            this.currentDeploymentNames = currentDeploymentNames;
        }

        @Override
        public boolean validate(String name) {
            if (isUpdate) return true;
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
        public RuntimeNameTextBoxItem(String name, String title, boolean isUpdate) {
            super(name, title, isUpdate);
        }

        @Override
        public boolean validate(String name) {
            if (isUpdate) return true;
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
