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
package org.jboss.ballroom.client.widgets.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.properties.NewPropertyWizard;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * FormItem that wraps a PropertyEditor table.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class PropertyEditorFormItem extends FormItem<List<PropertyRecord>> implements PropertyManagement {
    private static BeanFactory factory = GWT.create(BeanFactory.class);;

    protected PropertyEditor propertyEditor;
    protected List<PropertyRecord> value;

    protected DefaultWindow addPropertyDialog;
    protected String addDialogTitle;

    /**
     * Create a new PropertyEditorFormItem.
     *
     * @param name The name of the FormItem.
     * @param title The label that will be displayed with the editor.
     * @param addDialogTitle The title shown when the Add button is pressed.
     * @param rows The max number of rows in the PropertyEditor.
     */
    public PropertyEditorFormItem(String name, String title, String addDialogTitle, int rows) {
        super(name, title);
        this.propertyEditor = new PropertyEditor(this, true, rows);
        this.addDialogTitle = addDialogTitle;
    }

    @Override
    public Widget asWidget() {
        return this.propertyEditor.asWidget();
    }

    @Override
    public void clearValue() {
        // do nothing
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.propertyEditor.setEnabled(isEnabled);
    }

    @Override
    public boolean validate(List<PropertyRecord> value) {
        return true;
    }

    @Override
    public List<PropertyRecord> getValue() {
        return this.value;
    }

    @Override
    public void setValue(List<PropertyRecord> properties) {
        // clone the PropertyRecords so that you can cancel the edit
        List<PropertyRecord> props = new ArrayList<PropertyRecord>(properties.size());
        for (PropertyRecord property : properties) {
            PropertyRecord clone = factory.property().as();
            clone.setKey(property.getKey());
            clone.setValue(property.getValue());
            clone.setBootTime(property.isBootTime());
            props.add(clone);
        }

        this.value = props;
        this.propertyEditor.setProperties("", props);
    }

    @Override
    public void closePropertyDialoge() {
        addPropertyDialog.hide();
    }

    @Override
    public void launchNewPropertyDialoge(String reference) {
        addPropertyDialog = new DefaultWindow(addDialogTitle);
        addPropertyDialog.setWidth(320);
        addPropertyDialog.setHeight(240);
        addPropertyDialog.setWidget(new NewPropertyWizard(this, reference, false).asWidget());
        addPropertyDialog.setGlassEnabled(true);
        addPropertyDialog.center();
    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {
        this.value.add(prop);
        this.propertyEditor.setProperties(reference, value);
        setModified(true);
        closePropertyDialoge();
    }

    @Override
    public void onDeleteProperty(String reference, PropertyRecord prop) {
        this.value.remove(prop);
        this.propertyEditor.setProperties(reference, value);
        setModified(true);
    }

    @Override
    public void onChangeProperty(String reference, PropertyRecord prop) {
        setModified(true);
    }

}
