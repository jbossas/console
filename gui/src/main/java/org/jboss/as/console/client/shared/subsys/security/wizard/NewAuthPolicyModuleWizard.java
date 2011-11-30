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

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.properties.PropertyManagement;
import org.jboss.as.console.client.shared.subsys.security.AbstractDomainDetailEditor.Wizard;
import org.jboss.as.console.client.shared.subsys.security.AuthEditor;
import org.jboss.as.console.client.shared.subsys.security.SecurityDomainsPresenter;
import org.jboss.as.console.client.shared.subsys.security.model.AbstractAuthData;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.ListBoxItem;

/**
 * @author David Bosschaert
 */
public class NewAuthPolicyModuleWizard <T extends AbstractAuthData> extends GenericSecurityDomainWizard<T>
                                                                    implements PropertyManagement, Wizard<T> {
    private final List<String> flagChoices;

    public NewAuthPolicyModuleWizard(AuthEditor<T> editor, Class<T> cls, List<String> flagChoices,
        SecurityDomainsPresenter presenter, String type, String moduleAttrName) {
        super(editor, cls, presenter, type, moduleAttrName, "flag");
        this.flagChoices = flagChoices;
    }

    @Override
    FormItem<?>[] getCustomFields() {
        ListBoxItem flag = new ListBoxItem("flag", Console.CONSTANTS.subsys_security_flagField());
        flag.setChoices(flagChoices, flagChoices.get(0));
        return new FormItem [] {flag};
    }

    @Override
    void copyCustomFields(T original, T edited) {
        original.setFlag(edited.getFlag());
    }
}
