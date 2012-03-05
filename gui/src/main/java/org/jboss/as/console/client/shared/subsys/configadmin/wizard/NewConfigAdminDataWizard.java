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
package org.jboss.as.console.client.shared.subsys.configadmin.wizard;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.configadmin.ConfigAdminPresenter;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

/**
 * @author David Bosschaert
 */
public class NewConfigAdminDataWizard implements PropertyManagement {
    private final ConfigAdminPresenter presenter;
    private final List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
    private PropertyEditor propEditor;
    private BeanFactory factory = GWT.create(BeanFactory.class);

    public NewConfigAdminDataWizard(ConfigAdminPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");
        final Form<ConfigAdminData> form = new Form<ConfigAdminData>(ConfigAdminData.class);

        TextBoxItem pid = new TextBoxItem("pid", Console.CONSTANTS.subsys_configadmin_PIDShort());
        form.setFields(pid);

        layout.add(form.asWidget());
        propEditor = new PropertyEditor(this, true);
        layout.add(propEditor.asWidget());
        addEmptyProperty();

        DialogueOptions options = new DialogueOptions(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) {
                        if (properties.size() == 0) {
                            Feedback.alert(Console.CONSTANTS.subsys_configadmin_add(),
                                new SafeHtmlBuilder().appendEscaped(Console.MESSAGES.subsys_configadmin_oneValueRequired()).toSafeHtml());
                        } else {
                            ConfigAdminData data = form.getUpdatedEntity();
                            data.setProperties(properties);
                            presenter.onAddConfigurationAdminData(data);
                        }
                    }
                }
            }, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.closeDialogue();
                }
            });

        return new WindowContentBuilder(layout, options).build();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        // No need to implement
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        properties.remove(prop);
        propEditor.setProperties("", properties);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        // do nothing
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        addEmptyProperty();
    }

    private void addEmptyProperty() {
        PropertyRecord proto = factory.property().as();
        proto.setKey(Console.CONSTANTS.common_label_name().toLowerCase());
        proto.setValue(Console.CONSTANTS.common_label_value().toLowerCase());

        properties.add(proto);
        propEditor.setProperties("", properties);
    }

    @Override
    public void closePropertyDialoge() {
    }
}
