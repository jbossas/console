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
package org.jboss.as.console.client.shared.subsys.osgi.wizard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.osgi.OSGiPresenter;
import org.jboss.as.console.client.shared.subsys.osgi.model.OSGiPreloadedModule;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;

/**
 * @author David Bosschaert
 */
public class NewModuleWizard {
    private final OSGiPresenter presenter;
    private final OSGiPreloadedModule module;

    public NewModuleWizard(OSGiPresenter presenter, OSGiPreloadedModule module) {
        this.presenter = presenter;
        this.module = module;
    }

    public Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");
        final Form<OSGiPreloadedModule> form = new Form<OSGiPreloadedModule>(OSGiPreloadedModule.class);

        TextBoxItem identifier = new TextBoxItem("identifier", Console.CONSTANTS.subsys_osgi_preloadedModuleId());
        TextBoxItem startLevel = new TextBoxItem("startLevel", Console.CONSTANTS.subsys_osgi_preloadedModuleStartLevel());
        startLevel.setRequired(false);

        form.setFields(identifier, startLevel);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add(ModelDescriptionConstants.SUBSYSTEM, OSGiPresenter.OSGI_SUBSYSTEM);
                address.add("module", "*");
                return address;
            }
        }, form);
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());
        final String originalIdentifier;
        if (module != null) {
            originalIdentifier = module.getIdentifier();
            form.edit(module);
        } else {
            originalIdentifier = null;
        }

        DialogueOptions options = new DialogueOptions(
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    FormValidation validation = form.validate();
                    if (!validation.hasErrors()) {
                        if (originalIdentifier != null)
                            presenter.onDeletePreloadedModule(originalIdentifier);

                        presenter.onAddPreloadedModule(form.getUpdatedEntity());
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
}
