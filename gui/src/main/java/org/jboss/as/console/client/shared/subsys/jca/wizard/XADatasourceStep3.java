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

package org.jboss.as.console.client.shared.subsys.jca.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.properties.PropertyEditor;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/6/11
 */
public class XADatasourceStep3 implements PropertyManagement {

    private NewXADatasourceWizard wizard;
    private PropertyEditor propEditor;
    private List<PropertyRecord> properties;
    private BeanFactory factory = GWT.create(BeanFactory.class);
    private HTML errorMessages;

    public XADatasourceStep3(NewXADatasourceWizard wizard) {
        this.wizard = wizard;
        this.properties = new ArrayList<PropertyRecord>();
    }

    void edit(XADataSource dataSource) {

    }

    @Override
    public void onCreateProperty(String reference, PropertyRecord prop) {

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
        PropertyRecord proto = factory.property().as();
        proto.setKey("name");
        proto.setValue("value");

        properties.add(proto);
        propEditor.setProperties("", properties);

        errorMessages.setVisible(false);
    }

    @Override
    public void closePropertyDialoge() {

    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        layout.add(new HTML("<h3>"+ Console.CONSTANTS.subsys_jca_xadataSource_step3()+"</h3>"));

        propEditor = new PropertyEditor(this, true);

        errorMessages = new HTML(Console.CONSTANTS.subsys_jca_err_prop_required());
        errorMessages.setStyleName("error-panel");
        errorMessages.setVisible(false);

        layout.add(errorMessages);

        Widget widget = propEditor.asWidget();
        layout.add(widget);

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                boolean hasProperties = propEditor.getPropertyTable().getRowCount() > 0;

                if(!hasProperties)
                    errorMessages.setVisible(true);
                else
                    wizard.onConfigureProperties(properties);
            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                wizard.getPresenter().closeDialogue();
            }
        };

        DialogueOptions options = new DialogueOptions(
                Console.CONSTANTS.common_label_next(),submitHandler,
                Console.CONSTANTS.common_label_cancel(),cancelHandler
        );

        return new WindowContentBuilder(layout,options).build();
    }
}
