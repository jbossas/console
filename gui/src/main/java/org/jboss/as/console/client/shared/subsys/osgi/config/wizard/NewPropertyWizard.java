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
package org.jboss.as.console.client.shared.subsys.osgi.config.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextAreaItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;

/**
 * @author Heiko Braun
 * @author David Bosschaert
 */
public class NewPropertyWizard {
    private final PropertyManagement presenter;
    private final String reference;
    private BeanFactory factory = GWT.create(BeanFactory.class);

    public NewPropertyWizard(PropertyManagement presenter, String reference) {
        this.presenter = presenter;
        this.reference = reference;
    }

    public Widget asWidget() {
        DockLayoutPanel wrapper = new DockLayoutPanel(Style.Unit.PX);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        final Form<PropertyRecord> form = new Form<PropertyRecord>(PropertyRecord.class);

        final TextBoxItem nameItem = new TextBoxItem("key", Console.CONSTANTS.common_label_name());
        final TextAreaItem valueItem = new TextAreaItem("value", Console.CONSTANTS.common_label_value());

        form.setFields(nameItem, valueItem);

        DialogueOptions options = new DialogueOptions(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // save

                    // Not using form.getUpdatedEntity() here as this goes through a JSON
                    // serialization phase which causes issues with multi-line values
                    PropertyRecord property = factory.property().as();
                    property.setKey(nameItem.getValue());
                    property.setValue(valueItem.getValue());

                    presenter.onCreateProperty(reference, property);
                    presenter.closePropertyDialoge();
                }
            },
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // cancel
                    presenter.closePropertyDialoge();
                }
            }
        );

        panel.add(form.asWidget());

        wrapper.addSouth(options, 35);
        wrapper.add(panel);
        return wrapper;
    }
}
