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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.security.model.AuthenticationLoginModule;
import org.jboss.as.console.client.shared.subsys.security.wizard.NewAuthPolicyModuleWizard;
import org.jboss.ballroom.client.widgets.window.Feedback;

/**
 * @author David Bosschaert
 */
public class AuthenticationEditor extends AuthEditor<AuthenticationLoginModule>{
    AuthenticationEditor(SecurityDomainsPresenter presenter) {
        super(presenter, AuthenticationLoginModule.class);
    }

    @Override
    String getEntityName() {
        return Console.CONSTANTS.subsys_security_authentication();
    }

    @Override
    String getStackElementName() {
        return Console.CONSTANTS.subsys_security_authenticationLoginModule();
    }

    @Override
    String getStackName() {
        return Console.CONSTANTS.subsys_security_loginModules();
    }

    @Override
    void saveData() {
        presenter.saveAuthentication(domainName, attributesProvider.getList(), resourceExists);
    }

    @Override
    Wizard<AuthenticationLoginModule> getWizard() {
        if (flagValues == null) {
            // This sucks a bit, but these values are set asynchronously so there is a very small chance that they aren't
            // there yet. It would be better to automatically wait but is it worth the complexity?
            Feedback.alert(getEntityName(),
                new SafeHtmlBuilder().appendEscaped(Console.MESSAGES.temporarilyUnavailable()).toSafeHtml());
            return null;
        }
        // should really wait until flagValues are set.
        return new NewAuthPolicyModuleWizard<AuthenticationLoginModule>(this, entityClass, flagValues,
            presenter, SecurityDomainsPresenter.AUTHENTICATION_IDENTIFIER, "login-modules");
    }
}
