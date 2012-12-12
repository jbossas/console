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

package org.jboss.as.console.client.domain.hosts.general;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.general.HeapBoxItem;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

/**
 * @author Heiko Braun
 * @date 5/26/11
 */
public class NewHostJvmWizard {
    private HostJVMPresenter presenter;
    private final String hostName;

    public NewHostJvmWizard(HostJVMPresenter presenter, String hostName ) {
        this.presenter = presenter;
        this.hostName = hostName;
    }

    Widget asWidget() {


        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Jvm> form = new Form<Jvm>(Jvm.class);

        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name());
        HeapBoxItem heapItem = new HeapBoxItem("heapSize", "Heap Size");
        HeapBoxItem maxHeapItem = new HeapBoxItem("maxHeapSize", "Max Heap Size");
        HeapBoxItem permgen = new HeapBoxItem("permgen", "Permgen Size", false);
        HeapBoxItem maxPermgen = new HeapBoxItem("maxPermgen", "Max Permgen Size", false);

        form.setFields(nameItem, heapItem, maxHeapItem, permgen, maxPermgen);


         final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = new ModelNode();
                        address.add("host", Console.MODULES.getDomainEntityManager().getSelectedHost());
                        address.add("jvm", "*");
                        return address;
                    }
                }, form
        );
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        heapItem.setValue("64m");
        maxHeapItem.setValue("256m");
        permgen.setValue("128m");
        maxPermgen.setValue("128m");

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {

                        FormValidation validation = form.validate();
                        if(!validation.hasErrors())
                        {
                            presenter.onCreateJvm(hostName, form.getUpdatedEntity());
                        }
                    }
                },
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialogue();
                    }
                }
        );


        return new WindowContentBuilder(layout, options).build();
    }
}
