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

package org.jboss.as.console.client.domain.groups.deployment;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.DefaultButton;
import org.jboss.dmr.client.Base64;

/**
 * @author Heiko Braun
 * @date 4/8/11
 */
public class DeploymentStep1 {

    private NewDeploymentWizard wizard;

    public DeploymentStep1(NewDeploymentWizard wizard) {
        this.wizard = wizard;
    }

    public Widget asWidget()
    {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "width:95%; margin:15px;");

        // Create a FormPanel and point it at a service.
        final FormPanel form = new FormPanel();
        String url = Console.MODULES.getBootstrapContext().getProperty(BootstrapContext.DEPLOYMENT_API);
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

        // Add a 'submit' button.

        Label cancel = new Label("Cancel");
        cancel.setStyleName("html-link");
        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                wizard.getPresenter().closeDialoge();
            }
        });

        Button submit = new DefaultButton("Next &rsaquo;&rsaquo;", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.submit();
            }
        });

        HorizontalPanel options = new HorizontalPanel();
        options.getElement().setAttribute("style", "margin-top:10px;width:100%");

        HTML spacer = new HTML("&nbsp;");
        options.add(spacer);

        options.add(submit);
        options.add(spacer);
        options.add(cancel);
        cancel.getElement().getParentElement().setAttribute("style","vertical-align:middle");
        submit.getElement().getParentElement().setAttribute("align", "right");
        submit.getElement().getParentElement().setAttribute("width", "100%");

        panel.add(options);

        // Add an event handler to the form.
        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                String html = event.getResults();

                // Step 1: upload content, retrieve hash value
                try {

                    String json = html;

                    if(!GWT.isScript()) // TODO: Formpanel weirdness
                        json = html.substring(html.indexOf(">")+1, html.lastIndexOf("<"));

                    JSONObject response  = JSONParser.parseLenient(json).isObject();
                    JSONObject result = response.get("result").isObject();
                    String hash= result.get("BYTES_VALUE").isString().stringValue();
                    // step2: assign name and group
                    wizard.onUploadComplete(upload.getFilename(), hash);

                } catch (Exception e) {
                    Log.error("Failed to decode response: "+html, e);
                }

            }
        });

        layout.add(new HTML("<h3>Step 1/2: Deployment Selection</h3>"));
        layout.add(new HTML("Please chose a file that you want to deploy."));
        layout.add(form);
        return layout;
    }
}
