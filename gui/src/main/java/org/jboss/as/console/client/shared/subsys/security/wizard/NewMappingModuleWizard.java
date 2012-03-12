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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.subsys.security.AbstractDomainDetailEditor.Wizard;
import org.jboss.as.console.client.shared.subsys.security.MappingEditor;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.model.MappingModule;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;

/**
 * @author David Bosschaert
 */
public class NewMappingModuleWizard extends GenericSecurityDomainWizard<MappingModule>
                                    implements PropertyManagement, Wizard<MappingModule> {
    public NewMappingModuleWizard(MappingEditor editor, SecurityDomainsPresenter presenter) {
        super(editor, MappingModule.class, presenter, SecurityDomainsPresenter.MAPPING_IDENTIFIER,
            "mapping-modules", "type");
    }

    @Override
    FormItem<?>[] getCustomFields() {
        ComboBoxItem type = new ComboBoxItem("type", Console.CONSTANTS.subsys_security_typeField());
        type.setValueMap(new String[]{"principal", "role", "attribute","credential"});
        return new FormItem [] {type};
    }

    @Override
    void copyCustomFields(MappingModule original, MappingModule edited) {
        original.setType(edited.getType());
    }
}
