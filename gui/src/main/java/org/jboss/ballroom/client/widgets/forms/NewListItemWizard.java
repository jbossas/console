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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;

/**
 * @author Stan Silvert
 * @date 10/28/11
 */
public class NewListItemWizard {

    private ListManagement<String> listManager;
    private boolean limitChoices = false;
    private FormItem nameItem = null;
    private String label = "Value";
    
    public NewListItemWizard(ListManagement<String> listManager, boolean limitChoices) {
        this.listManager = listManager;
        this.limitChoices = limitChoices;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isChoiceLimited() {
        return this.limitChoices;
    }
    
    public Widget asWidget() {

        DockLayoutPanel wrapper = new DockLayoutPanel(Style.Unit.PX);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("window-content");

        // borrow the PropertyRecord for simple binding of a String to the form
        final Form<PropertyRecord> form = new Form<PropertyRecord>(PropertyRecord.class);

        if (limitChoices) {
            this.nameItem = new ComboBoxItem("value", label);
        } else {
            this.nameItem = new TextBoxItem("value", label);
        }

        form.setFields(nameItem);

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                         // save
                        PropertyRecord property = form.getUpdatedEntity();
                        if(!form.validate().hasErrors())
                            listManager.onCreateItem(property.getValue());
                    }
                },
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // cancel
                        listManager.closeNewItemDialoge();
                    }
                }
        );

        panel.add(form.asWidget());

        wrapper.addSouth(options, 35);
        wrapper.add(panel);
        return wrapper;
    }
    
    /**
     * Called before opening the dialog so that the user sees only the valid choices.
     * 
     * @param choices 
     */
    public void setChoices(List<String> choices) {
        if (!limitChoices) throw new IllegalArgumentException("Attempted to set choices when choices are not limited.");

        List<String> sorted = new ArrayList<String>();
        sorted.addAll(choices);
        Collections.sort(sorted);
        ((ComboBoxItem)this.nameItem).setValueMap(sorted);
    }
}
