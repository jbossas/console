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
package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.EnumSet;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;

/**
 * Displays form and buttons that allow editing of attributes of the Entity.
 * 
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class EntityDetails<T> implements EditListener {

    private String entitiesName;
    private FormAdapter<T> form;
    private ToolButton editBtn;
    private ToolButton cancelBtn;
    private ToolButton removeBtn;
    private EntityToDmrBridge executor;
    private EnumSet<FrameworkButton> hideButtons;
    private AddressBinding address;

    /**
     * Create a new EntityDetails.
     * 
     * @param entitiesName The heading for the details form
     * @param form The form containing all AttributesMetadata that can be displayed or edited.
     * @param executor The EntityToDmrBridge that will be called for various actions.
     */
    public EntityDetails(String entitiesName, FormAdapter form, EntityToDmrBridge executor, AddressBinding address) {
        this(entitiesName, form, executor, address, EnumSet.noneOf(FrameworkButton.class));
    }

    public EntityDetails(String entitiesName,
            FormAdapter form,
            EntityToDmrBridge executor,
            AddressBinding address,
            EnumSet<FrameworkButton> hideButtons) {
        this.entitiesName = entitiesName;
        this.form = form;
        this.executor = executor;
        this.address = address;
        this.hideButtons = hideButtons;
    }

    /**
     * Get the EntityDetails as a Widget.
     * @return The Widget.
     */
    public Widget asWidget() {
        VerticalPanel detailPanel = new VerticalPanel();
        detailPanel.setStyleName("fill-layout-width");

        ToolStrip detailToolStrip = new ToolStrip();
        editBtn = new ToolButton(Console.CONSTANTS.common_label_edit());
        ClickHandler editHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (editBtn.getText().equals(Console.CONSTANTS.common_label_edit())) {
                    executor.onEdit();
                } else {
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) {
                        executor.onSaveDetails(form);
                    }
                }
            }
        };
        editBtn.addClickHandler(editHandler);

        cancelBtn = new ToolButton(Console.CONSTANTS.common_label_cancel());
        ClickHandler cancelHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.cancel();
                EntityDetails.this.setEditingEnabled(false);
            }
        };
        cancelBtn.addClickHandler(cancelHandler);

        removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove());
        ClickHandler removeHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm(Console.CONSTANTS.common_label_areYouSure(),
                        Console.MESSAGES.removeFromConfirm(executor.getName(form.getEditedEntity()), entitiesName),
                        new Feedback.ConfirmationHandler() {

                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    executor.onRemove(form);
                                }
                            }
                        });
            }
        };
        removeBtn.addClickHandler(removeHandler);


        if (!hideButtons.contains(FrameworkButton.EDIT_SAVE)) {
            detailToolStrip.addToolButton(editBtn);
        }
        if (!hideButtons.contains(FrameworkButton.CANCEL)) {
            detailToolStrip.addToolButton(cancelBtn);
        }
        if (!hideButtons.contains(FrameworkButton.REMOVE)) {
            detailToolStrip.addToolButton(removeBtn);
        }

        detailPanel.add(detailToolStrip);

        if (address != null) {
            detailPanel.add(makeHelpWidget());
        }

        detailPanel.add(form.asWidget());
        form.addEditListener(this);

        setEditingEnabled(false);  // initially don't allow edit

        ScrollPanel scroll = new ScrollPanel(detailPanel);
        return scroll;
    }

    protected Widget makeHelpWidget() {
        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {

                    @Override
                    public org.jboss.dmr.client.ModelNode getAddress() {
                        String wildCards[] = new String[address.getNumWildCards()];
                        for (int i = 0; i < wildCards.length; i++) {
                            wildCards[i] = "*";
                        }

                        ModelNode addressAsResource = address.asResource(wildCards);
                        ModelNode addressNode = Baseadress.get();
                        for (Property address : addressAsResource.get(ADDRESS).asPropertyList()) {
                            addressNode.add(address.getName(), address.getValue());
                        }

                        return addressNode;
                    }
                }, form);

        return helpPanel.asWidget();
    }

    /**
     * Bind a table to the details section.
     * @param entityTable The table to be bound.
     */
    public void bind(CellTable<T> entityTable) {
        form.bind(entityTable);
    }

    /**
     * Set the state of the form and buttons if editing details is enabled.
     * @param isEnabled Set the flag for editing details.
     */
    public void setEditingEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
        cancelBtn.setVisible(isEnabled);
        removeBtn.setVisible(!isEnabled);

        if (isEnabled) {
            editBtn.setText(Console.CONSTANTS.common_label_save());
        } else {
            editBtn.setText(Console.CONSTANTS.common_label_edit());
        }
    }

    /**
     * Called when the form is presented with a new bean for editing.  You can override this method
     * to receive the callbacks.
     * @param bean The bean being edited.
     */
    @Override
    public void editingBean(Object bean) {
    }
}
