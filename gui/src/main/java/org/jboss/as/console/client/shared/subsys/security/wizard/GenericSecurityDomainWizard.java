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
package org.jboss.as.console.client.shared.subsys.security.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.security.AbstractDomainDetailEditor;
import org.jboss.as.console.client.shared.subsys.security.AbstractDomainDetailEditor.Wizard;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class GenericSecurityDomainWizard <T extends GenericSecurityDomainData> implements PropertyManagement, Wizard<T> {
    private boolean editMode = false;
    private final AbstractDomainDetailEditor<T> editor;
    private final Class<T> entityClass;
    private final BeanFactory factory = GWT.create(BeanFactory.class);
    private Form<T> form;
    private final SecurityDomainsPresenter presenter;
    private final List<PropertyRecord> properties = new ArrayList<PropertyRecord>();
    private PropertyEditor propEditor;
    private final String type;
    private final String moduleAttrName;
    private final String [] customAttributeNames;

    public GenericSecurityDomainWizard(AbstractDomainDetailEditor<T> editor, Class<T> cls, SecurityDomainsPresenter presenter, String type,
        String moduleAttrName, String ... customAttributeNames) {
        this.editor = editor;
        this.entityClass = cls;
        this.presenter = presenter;
        this.type = type;
        this.moduleAttrName = moduleAttrName;
        this.customAttributeNames = customAttributeNames;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");
        form = new Form<T>(entityClass);

        TextBoxItem code = new TextBoxItem("code", Console.CONSTANTS.subsys_security_codeField());
        FormItem<?>[] customFields = getCustomFields();
        form.setFields(new FormItem [] {code}, customFields);

        new AsyncHelpText(layout);

        layout.add(form.asWidget());
        propEditor = new PropertyEditor(this, true);
        layout.add(propEditor.asWidget());

        DialogueOptions options = new DialogueOptions(
            Console.CONSTANTS.common_label_save(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) {
                        if (editMode) {
                            T original = form.getEditedEntity();
                            T edited = form.getUpdatedEntity();
                            original.setCode(edited.getCode());
                            original.setProperties(properties);

                            copyCustomFields(original, edited);

                            editor.save(original);
                        } else {
                            // it's a new policy
                            T data = form.getUpdatedEntity();
                            data.setProperties(properties);
                            editor.addAttribute(data);
                        }

                        editor.closeWizard();
                    }
                }
            }, Console.CONSTANTS.common_label_cancel(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    editor.closeWizard();
                }
            });

        return new WindowContentBuilder(layout, options).build();
    }

    FormItem<?>[] getCustomFields() {
        return new FormItem[] {};
    }

    void copyCustomFields(T original, T edited) {
    }

    public void edit(T object) {
        editMode = true;
        form.edit(object);

        if (object.getProperties() != null) {
            ArrayList<PropertyRecord> copiedProperties = new ArrayList<PropertyRecord>(object.getProperties().size());
            for (PropertyRecord pr : object.getProperties()) {
                PropertyRecord clone = factory.property().as();
                clone.setKey(pr.getKey());
                clone.setValue(pr.getValue());
                copiedProperties.add(clone);
            }

            properties.clear();
            properties.addAll(copiedProperties);
            propEditor.setProperties("", copiedProperties);
        }
    }

    // PropertyManagement methods
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
        // No need to implement
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        PropertyRecord proto = factory.property().as();
        proto.setKey(Console.CONSTANTS.common_label_name().toLowerCase());
        proto.setValue(Console.CONSTANTS.common_label_value().toLowerCase());

        properties.add(proto);
        propEditor.setProperties("", properties);
    }

    @Override
    public void closePropertyDialoge() {
    }

    private class AsyncHelpText implements SecurityDomainsPresenter.DescriptionCallBack {
        private final VerticalPanel layout;

        private AsyncHelpText(VerticalPanel layout) {
            this.layout = layout;
            presenter.getDescription(type, this);
        }

        @Override
        public void setDescription(ModelNode desc) {
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            if (desc.get(ModelDescriptionConstants.DESCRIPTION).isDefined()) {
                builder.appendEscaped(desc.get(ModelDescriptionConstants.DESCRIPTION).asString());
                builder.appendHtmlConstant("<p/>");
            }

            List<String> attrs = new ArrayList<String>(Arrays.asList(customAttributeNames));
            attrs.add(0, "code"); // Common field

            ModelNode values = desc.get(ModelDescriptionConstants.ATTRIBUTES,
                                        moduleAttrName,
                                        ModelDescriptionConstants.VALUE_TYPE);
            builder.appendHtmlConstant("<ul>");

            for (String s : attrs) {
                builder.appendHtmlConstant("<li><b>");
                builder.appendEscaped(s);
                builder.appendHtmlConstant("</b> - ");
                builder.appendEscaped(values.get(s, ModelDescriptionConstants.DESCRIPTION).asString());
            }

            builder.appendHtmlConstant("<li><b>");
            builder.appendEscaped("module-options");
            builder.appendHtmlConstant("</b> - ");
            builder.appendEscaped(values.get("module-options",
                    ModelDescriptionConstants.DESCRIPTION).asString());

            builder.appendHtmlConstant("</ul>");
            SafeHtml safeHtml = builder.toSafeHtml();
            StaticHelpPanel helpPanel = new StaticHelpPanel(safeHtml);
            layout.insert(helpPanel.asWidget(), 0);
        }
    }
}
