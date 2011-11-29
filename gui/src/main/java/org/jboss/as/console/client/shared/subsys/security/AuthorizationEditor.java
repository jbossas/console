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
package org.jboss.as.console.client.shared.subsys.security;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import org.jboss.as.console.client.shared.subsys.security.model.AuthorizationPolicyProvider;
import org.jboss.as.console.client.shared.subsys.security.wizard.NewAuthPolicyModuleWizard;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author David Bosschaert
 */
public class AuthorizationEditor extends AuthEditor<AuthorizationPolicyProvider> {
    AuthorizationEditor(SecurityDomainsPresenter presenter) {
        super(presenter, AuthorizationPolicyProvider.class);
    }

    @Override
    String getEntityName() {
        return "Authorization";
    }

    @Override
    String getStackElementName() {
        return getEntityName() + " Policy";
    }

    @Override
    String getStackName() {
        return "Policies";
    }

    @Override
    void saveData() {
        presenter.saveAuthorization(domainName, attributesProvider.getList(), resourceExists);
    }

    @Override
    Wizard<AuthorizationPolicyProvider> getWizard() {
        if (flagValues == null) {
            // This sucks a bit, but these values are set asynchronously so there is a very small chance that they aren't
            // there yet. It would be better to automatically wait but is it worth the complexity?
            Feedback.alert(getEntityName(),
                new SafeHtmlBuilder().appendHtmlConstant("Allowed flag values not yet available, please try again later.").toSafeHtml());
            return null;
        }
        // should really wait until flagValues are set.
        return new NewAuthPolicyModuleWizard<AuthorizationPolicyProvider>(this, entityClass, flagValues,
            presenter, SecurityDomainsPresenter.AUTHORIZATION_IDENTIFIER, "policy-modules");
    }
}
