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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.apache.commons.digester.rss.TextInput;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 4/8/11
 */
public class DeploymentStep1 {
    private NewDeploymentWizard wizard;
    private DefaultWindow window;
    private PopupPanel loading;
    private Form<DeploymentRecord> unmanagedForm;

    public DeploymentStep1(NewDeploymentWizard wizard, DefaultWindow window) {
        this.wizard = wizard;
        this.window = window;
    }

    public Widget asWidget()
    {

        final TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");

        // -------

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");


        // Create a FormPanel and point it at a service.
        final FormPanel form = new FormPanel();
        String url = Console.getBootstrapContext().getProperty(BootstrapContext.DEPLOYMENT_API);
        form.setAction(url);

        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        // Create a panel to hold all of the form widgets.
        VerticalPanel panel = new VerticalPanel();
        panel.getElement().setAttribute("style", "width:100%");
        form.setWidget(panel);

        // Create a FileUpload widget.
        final FileUpload upload = new FileUpload();
        upload.setName("uploadFormElement");
        panel.add(upload);


        final HTML errorMessages = new HTML("Please chose a file!");
        errorMessages.setStyleName("error-panel");
        errorMessages.setVisible(false);
        panel.add(errorMessages);

        // Add a 'submit' button.


        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                window.hide();
            }
        };

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                errorMessages.setVisible(false);

                // verify form
                String filename = upload.getFilename();

                if(tabs.getTabBar().getSelectedTab()==1)
                {
                    // unmanaged content
                    if(unmanagedForm.validate().hasErrors())
                    {
                        return;
                    }
                    else
                    {
                        wizard.onCreateUnmanaged(unmanagedForm.getUpdatedEntity());
                    }
                }
                else if(filename!=null && !filename.equals(""))
                {
                    loading = Feedback.loading(
                            Console.CONSTANTS.common_label_plaseWait(),
                            Console.CONSTANTS.common_label_requestProcessed(),
                            new Feedback.LoadingCallback() {
                                @Override
                                public void onCancel() {

                                }
                            });
                    form.submit();
                }
                else
                {
                    errorMessages.setVisible(true);
                }
            }
        };

        DialogueOptions options = new DialogueOptions(
                Console.CONSTANTS.common_label_next(), submitHandler,
                Console.CONSTANTS.common_label_cancel(), cancelHandler);

        // Add an event handler to the form.
        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {

                getLoading().hide();


                String html = event.getResults();

                // Step 1: upload content, retrieve hash value
                try {

                    String json = html;

                    try {
                        if(!GWT.isScript()) // TODO: Formpanel weirdness
                            json = html.substring(html.indexOf(">")+1, html.lastIndexOf("<"));
                    } catch (StringIndexOutOfBoundsException e) {
                        // if I get this exception it means I shouldn't strip out the html
                        // this issue still needs more research
                        Log.debug("Failed to strip out HTML.  This should be preferred?");
                    }

                    JSONObject response  = JSONParser.parseLenient(json).isObject();
                    JSONObject result = response.get("result").isObject();
                    String hash= result.get("BYTES_VALUE").isString().stringValue();
                    // step2: assign name and group
                    wizard.onUploadComplete(upload.getFilename(), hash);

                } catch (Exception e) {
                    Log.error(Console.CONSTANTS.common_error_failedToDecode() + ": " + html, e);
                }


                // Option 2: Unmanaged content

            }
        });

        String stepText = "<h3>" + Console.CONSTANTS.common_label_step() + "1/2: " +
                Console.CONSTANTS.common_label_deploymentSelection() + "</h3>";
        layout.add(new HTML(stepText));
        HTML description = new HTML();
        description.setHTML(Console.CONSTANTS.common_label_chooseFile());
        description.getElement().setAttribute("style", "padding-bottom:15px;");
        layout.add(description);
        layout.add(form);


        // Unmanaged form
        VerticalPanel unmanagedPanel = new VerticalPanel();
        unmanagedPanel.setStyleName("window-content");

        String unmanagedText = "<h3>" + Console.CONSTANTS.common_label_step() + "1/1: Specify Deployment</h3>";
        unmanagedPanel.add(new HTML(unmanagedText));

        unmanagedForm = new Form<DeploymentRecord>(DeploymentRecord.class);
        TextAreaItem path = new TextAreaItem("path", "Path");
        TextAreaItem relativeTo= new TextAreaItem("relativeTo", "Relative To", false);

        TextBoxItem name = new TextBoxItem("name", "Name");
        TextBoxItem runtimeName = new TextBoxItem("runtimeName", "Runtime Name");
        CheckBoxItem archive = new CheckBoxItem("archive", "Is Archive?");
        archive.setValue(true);
        unmanagedForm.setFields(path, relativeTo, archive, name, runtimeName);
        unmanagedPanel.add(unmanagedForm.asWidget());


        // Composite view

        tabs.add(layout, "Managed");
        tabs.add(unmanagedPanel, "Unmanaged");
        tabs.selectTab(0);

        return new WindowContentBuilder(tabs, options).build();
    }

    private PopupPanel getLoading() {
        return loading;
    }
}
