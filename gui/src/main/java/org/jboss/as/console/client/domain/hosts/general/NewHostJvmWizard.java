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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.hosts.CurrentHostSelection;
import org.jboss.as.console.client.shared.jvm.Jvm;
import org.jboss.as.console.client.widgets.DialogueOptions;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;

/**
 * @author Heiko Braun
 * @date 5/26/11
 */
public class NewHostJvmWizard {
    private HostJVMPresenter presenter;
    private CurrentHostSelection currentHost;

    public NewHostJvmWizard(HostJVMPresenter presenter, CurrentHostSelection currentHost) {
        this.presenter = presenter;
        this.currentHost = currentHost;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:15px;");

        final Form<Jvm> form = new Form<Jvm>(Jvm.class);

        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name());
        TextBoxItem heapItem = new TextBoxItem("heapSize", "Heap Size");
        TextBoxItem maxHeapItem = new TextBoxItem("maxHeapSize", "Max Heap Size");
        CheckBoxItem debugItem = new CheckBoxItem("debugEnabled", "Debug Enabled?");
        //TextBoxItem debugOptionsItem = new TextBoxItem("debugOptions", "Debug Options");

        form.setFields(nameItem, heapItem, maxHeapItem, debugItem);

        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.onCreateJvm(currentHost.getName(), form.getUpdatedEntity());
                    }
                },
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.closeDialogue();
                    }
                }
        );

        layout.add(options);

        return layout;
    }
}
