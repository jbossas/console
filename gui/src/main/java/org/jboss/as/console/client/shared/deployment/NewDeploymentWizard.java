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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author Heiko Braun
 * @author Stan Silvert <ssilvert@redhat.com> (C) 2011 Red Hat Inc.
 * @date 4/7/11
 */
public class NewDeploymentWizard  {
    private VerticalPanel layout;
    private DeckPanel deck;

    private BeanFactory factory = GWT.create(BeanFactory.class);

    private DeploymentStep1 step1;
    private DeploymentStep2 step2;

    private DefaultWindow window;
    private DispatchAsync dispatcher;
    private DeploymentViewRefresher refresher;
    private boolean isUpdate;
    private DeploymentRecord oldDeployment;

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    /**
     *
     * @param window
     * @param dispatcher
     * @param refresher
     * @param isUpdate Are we updating content that is already in the repository?
     * @param oldDeployment The original deployment.  If isUpdate == false, this should be null.
     */
    public NewDeploymentWizard(DefaultWindow window, DispatchAsync dispatcher, DeploymentViewRefresher refresher, boolean isUpdate, DeploymentRecord oldDeployment) {
        this.window = window;
        this.dispatcher = dispatcher;
        this.refresher = refresher;
        this.isUpdate = isUpdate;
        this.oldDeployment = oldDeployment;

        deck = new DeckPanel();

        step1 = new DeploymentStep1(this, window);
        step2 = new DeploymentStep2(this, window, refresher, isUpdate);

        deck.add(step1.asWidget());
        deck.add(step2.asWidget());

        deck.showWidget(0);
    }

    public Widget asWidget() {
        return deck;
    }


    public void onUploadComplete(String fileName, String hash) {

        // html5 spec: anonymous file upload (C:\fakepath\)
        int fakePathIndex = fileName.lastIndexOf("\\");
        if(fakePathIndex!=-1)
        {
            fileName = fileName.substring(fakePathIndex+1, fileName.length());
        }

        DeploymentReference deploymentRef = factory.deploymentReference().as();
        deploymentRef.setHash(hash);

        if (isUpdate) {
            deploymentRef.setName(oldDeployment.getName());
            deploymentRef.setRuntimeName(oldDeployment.getRuntimeName());
        } else {
            deploymentRef.setName(fileName);
            deploymentRef.setRuntimeName(fileName);
        }

        step2.edit(deploymentRef);
        deck.showWidget(1); // proceed to step2
    }

    public void onDeployToGroup(final DeploymentReference deployment) {
        window.hide();
        assignDeploymentName(deployment);
    }

    private String makeAddJSO(DeploymentReference deployment) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"address\":[").append("{\"deployment\":\"").append(deployment.getName()).append("\"}],");
        sb.append("\"operation\":\"add\",");
        sb.append("\"runtime-name\":\"").append(deployment.getRuntimeName()).append("\",");
        sb.append("\"content\":");
        sb.append("[{\"hash\":{");
        sb.append("\"BYTES_VALUE\":\"").append(deployment.getHash()).append("\"");
        sb.append("}}],");
        sb.append("\"name\":\"").append(deployment.getName()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private String makeFullReplaceJSO(DeploymentReference deployment) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"operation\":\"full-replace-deployment\",");
        sb.append("\"content\":");
        sb.append("[{\"hash\":{");
        sb.append("\"BYTES_VALUE\":\"").append(deployment.getHash()).append("\"");
        sb.append("}}],");
        sb.append("\"name\":\"").append(deployment.getName()).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private void assignDeploymentName(final DeploymentReference deployment) {
        String requestJSO;
        if (isUpdate) {
            requestJSO = makeFullReplaceJSO(deployment);
        } else {
            requestJSO = makeAddJSO(deployment);
        }
        //System.out.println("requestJSO=" + requestJSO);

        RequestBuilder rb = new RequestBuilder(
                RequestBuilder.POST,
                Console.getBootstrapContext().getProperty(BootstrapContext.DOMAIN_API)
        );

        rb.setHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON);

        try {


            final PopupPanel loading = Feedback.loading(
                    Console.CONSTANTS.common_label_plaseWait(),
                    Console.CONSTANTS.common_label_requestProcessed(), new Feedback.LoadingCallback() {
                @Override
                public void onCancel() {

                }
            });

            rb.sendRequest(requestJSO, new RequestCallback(){
                @Override
                public void onResponseReceived(Request request, Response response) {
                    //System.out.println("response=");
                    //System.out.println(response.getText());
                    if(200 != response.getStatusCode()) {
                        onDeploymentFailed(deployment, response);
                        return;
                    }

                    loading.hide();
                    window.hide();
                    refresher.refreshView();

                    String operation = Console.CONSTANTS.common_label_addContent();
                    if (isUpdate) operation = Console.CONSTANTS.common_label_updateContent();
                    sendMsg(Console.CONSTANTS.common_label_success() +
                            ": " + operation +
                            ": " + deployment.getName(), Message.Severity.Info);
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    sendMsg(Console.CONSTANTS.common_error_deploymentFailed() + ": " + exception.getMessage(), Message.Severity.Error);
                    Log.error(Console.CONSTANTS.common_error_deploymentFailed() + ": ", exception);
                }
            });
        } catch (RequestException e) {
            sendMsg(Console.CONSTANTS.common_error_deploymentFailed() + ": " + e.getMessage(), Message.Severity.Error);
            Log.error(Console.CONSTANTS.common_error_unknownError(), e);
        }
    }

    private void onDeploymentFailed(DeploymentReference deployment, Response response) {
        sendMsg(Console.CONSTANTS.common_error_deploymentFailed() +
                ": " + deployment.getName() +
                ": " + response.getText(), Message.Severity.Error);
    }

    private void sendMsg(String message, Message.Severity severity) {
        Console.getMessageCenter().notify(new Message(message, severity));
    }
}
