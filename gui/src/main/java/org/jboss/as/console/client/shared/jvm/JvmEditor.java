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

package org.jboss.as.console.client.shared.jvm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import java.util.Map;
import java.util.regex.Pattern;


/**
 * @author Heiko Braun
 * @date 4/20/11
 */
public class JvmEditor {

    private JvmManagement presenter;
    private Form<Jvm> form;
    BeanFactory factory = GWT.create(BeanFactory.class);
    private boolean hasJvm;

    private String reference;
    private Widget formWidget;
    private FormHelpPanel.AddressCallback addressCallback;
    private final Pattern heapSizeValidationPattern = Pattern.compile("[\\d]{2,4}[mM]");

    public JvmEditor(JvmManagement presenter) {
        this.presenter = presenter;
    }

    public void setAddressCallback(FormHelpPanel.AddressCallback addressCallback) {
        this.addressCallback = addressCallback;
    }

    public Widget asWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        form = new Form<Jvm>(Jvm.class);
        form.setNumColumns(2);

        FormToolStrip<Jvm> toolStrip = new FormToolStrip<Jvm>(
                form,
                new FormToolStrip.FormCallback<Jvm>()
                {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        onSaveJvm();
                    }

                    @Override
                    public void onDelete(Jvm entity) {
                        //presenter.onDeleteJvm(reference, form.getEditedEntity());
                    }
                });

        toolStrip.providesDeleteOp(false);

        panel.add(toolStrip.asWidget());

        TextBoxItem nameItem = new TextBoxItem("name", Console.CONSTANTS.common_label_name());
        TextBoxItem heapItem = new TextBoxItem("heapSize", "Heap Size")
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean matchPattern = heapSizeValidationPattern.matcher(value).matches();
                return hasValue && matchPattern;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_heapSize();
            }
        };
        TextBoxItem maxHeapItem = new TextBoxItem("maxHeapSize", "Max Heap Size")
        {
            @Override
            public boolean validate(String value) {
                boolean hasValue = super.validate(value);
                boolean matchPattern = heapSizeValidationPattern.matcher(value).matches();
                return hasValue && matchPattern;
            }

            @Override
            public String getErrMessage() {
                return Console.MESSAGES.common_validation_heapSize();
            }
        };
        //CheckBoxItem debugItem = new CheckBoxItem("debugEnabled", "Debug Enabled?");
        //TextBoxItem debugOptionsItem = new TextBoxItem("debugOptions", "Debug Options");

        form.setFields(nameItem, heapItem, maxHeapItem);
        form.setEnabled(false);

        // ---

        if(addressCallback!=null)
        {
            final FormHelpPanel helpPanel = new FormHelpPanel(addressCallback, form);
            panel.add(helpPanel.asWidget());
        }

        // ---

        formWidget = form.asWidget();
        panel.add(formWidget);

        return panel;
    }

    private void onSaveJvm() {

        FormValidation validation = form.validate();
        if(!validation.hasErrors())
        {
            form.setEnabled(false);
            Jvm jvm = form.getUpdatedEntity();
            if(hasJvm)
                presenter.onUpdateJvm(reference, jvm.getName(), form.getChangedValues());
            else
                presenter.onCreateJvm(reference, jvm);
        }
    }

    public void setSelectedRecord(String reference, Jvm jvm) {
        this.reference = reference;

        hasJvm = jvm!=null;

        form.setEnabled(false);

        if(hasJvm)
            form.edit(jvm);
        else
            form.edit(factory.jvm().as());

    }

    public void clearValues() {
        form.clearValues();
    }
}
