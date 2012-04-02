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

package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class NewAddressPatternWizard {

    private MsgDestinationsPresenter presenter;
    private MessagingProvider provider;

    public NewAddressPatternWizard (MsgDestinationsPresenter presenter, MessagingProvider provider) {
        this.presenter = presenter;
        this.provider = provider;
    }


    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<AddressingPattern> form = new Form<AddressingPattern>(AddressingPattern.class);


        TextBoxItem pattern = new TextBoxItem("pattern", "Pattern");

        TextBoxItem dlQ = new TextBoxItem("deadLetterQueue", "Dead Letter Address");
        TextBoxItem expQ= new TextBoxItem("expiryQueue", "Expiry Address");
        NumberBoxItem redelivery = new NumberBoxItem("redeliveryDelay", "Redelivery Delay");
        NumberBoxItem maxAttempts = new NumberBoxItem("maxDelivery", "Max Delivery Attempts");

        form.setFields(pattern, dlQ, expQ, redelivery, maxAttempts);

        // TODO: defaults
        dlQ.setValue("jms.queue.DLQ");
        expQ.setValue("jms.queue.ExpiryQueue");
        redelivery.setValue(0);
        maxAttempts.setValue(10);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        //submit
                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            presenter.onCreateAddressPattern(form.getUpdatedEntity());
                        }
                    }
                },
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // cancel
                        presenter.closeDialogue();
                    }
                }
        );

        return new WindowContentBuilder(layout, options).build();
    }
}
