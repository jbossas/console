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

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

import java.util.EnumSet;
import java.util.Map;

/**
 * Displays form and buttons that allow editing of attributes of the Entity.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 * @author Heiko Braun
 */
public class EntityDetails<T> implements EditListener, SingleEntityView<T> {

    private String entitiesName;
    private FormAdapter<T> form;
    private EntityToDmrBridge bridge;
    private EnumSet<FrameworkButton> hideButtons;
    private AddressBinding helpAddress;

    /**
     * Create a new EntityDetails.
     *
     * @param presenter the presenter that will be called for various actions.
     * @param entitiesName The heading for the details form
     * @param form The form containing all AttributesMetadata that can be displayed or edited.
     * @param helpAddress The address used to find help for the attributes on the form.
     */
    public EntityDetails(FrameworkPresenter presenter,String entitiesName, FormAdapter form, AddressBinding helpAddress) {
        this(presenter, entitiesName, form, helpAddress, EnumSet.noneOf(FrameworkButton.class));
    }

    public EntityDetails(FrameworkPresenter presenter,
                         String entitiesName,
                         FormAdapter form,
                         AddressBinding helpAddress,
                         EnumSet<FrameworkButton> hideButtons) {
        this.entitiesName = entitiesName;
        this.form = form;
        this.bridge = presenter.getEntityBridge();
        this.helpAddress = helpAddress;
        this.hideButtons = hideButtons;
    }

    public EntityToDmrBridge getBridge() {
        return bridge;
    }

    /**
     * Get the EntityDetails as a Widget.
     * @return The Widget.
     */
    public Widget asWidget() {

        // dirty but works
        boolean tabbedLayout = (form instanceof TabbedFormLayoutPanel);

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");


        // ----------------------------
        if(tabbedLayout)
        {
            // assign click handler
            TabbedFormLayoutPanel formTabs = (TabbedFormLayoutPanel)form;
            formTabs.setBridge(bridge);
            formTabs.setHelpAddress(helpAddress);

        }
        else
        {
            if (!hideButtons.contains(FrameworkButton.EDIT_SAVE)) {
                // toolstrip
                FormToolStrip<T> toolStrip = new FormToolStrip<T>(
                        form,
                        new FormToolStrip.FormCallback<T>() {
                            @Override
                            public void onSave(Map<String, Object> changeset) {
                                bridge.onSaveDetails(form.getEditedEntity(), changeset);
                            }

                            @Override
                            public void onDelete(T entity) {
                                bridge.onRemove(entity);
                            }
                        }
                );

                toolStrip.providesDeleteOp(false);  // belongs to the top

                layout.add(toolStrip.asWidget());
            }

            // help panel
            if (helpAddress != null) {
                layout.add(HelpWidgetFactory.makeHelpWidget(helpAddress, form));
            }
        }

        layout.add(form.asWidget());

        // ----------------------------

        form.addEditListener(this);
        setEditingEnabled(false);  // initially don't allow edit

        // ----------------------------

        return layout;
    }

    /**
     * Set the state of the form and buttons if editing details is enabled.
     * @param isEnabled Set the flag for editing details.
     */
    public void setEditingEnabled(boolean isEnabled) {
        form.setEnabled(isEnabled);
    }

    /**
     * Called when the form is presented with a new bean for editing.  You can override this method
     * to receive the callbacks.
     * @param bean The bean being edited.
     */
    @Override
    public void editingBean(Object bean) {
    }

    @Override
    public void updatedEntity(T entity) {
        form.edit(entity);
    }

    @Override
    public String getTitle() {
        return entitiesName + " Details";
    }

    /*public void bind(DefaultCellTable<T> table) {
        form.bind(table);
    } */

    public void clearValues() {
        form.clearValues();
    }
}
