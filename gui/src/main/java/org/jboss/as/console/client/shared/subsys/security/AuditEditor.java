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

import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SimpleKeyProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.shared.subsys.security.wizard.GenericSecurityDomainWizard;

/**
 * @author David Bosschaert
 */
public class AuditEditor extends AbstractDomainDetailEditor<GenericSecurityDomainData>{
    public AuditEditor(SecurityDomainsPresenter presenter) {
        super(presenter, GenericSecurityDomainData.class);
         setDescription(Console.CONSTANTS.subsys_security_audit_desc());
    }

    @Override
    ProvidesKey<GenericSecurityDomainData> getKeyProvider() {
        return new SimpleKeyProvider<GenericSecurityDomainData>();
    }

    @Override
    String getEntityName() {
        return Console.CONSTANTS.subsys_security_audit();
    }

    @Override
    String getStackElementName() {
        return Console.CONSTANTS.subsys_security_auditProviderModule();
    }

    @Override
    String getStackName() {
        return Console.CONSTANTS.subsys_security_providerModules();
    }

    @Override
    Wizard<GenericSecurityDomainData> getWizard() {
        return new GenericSecurityDomainWizard<GenericSecurityDomainData>(this, GenericSecurityDomainData.class,
            presenter, SecurityDomainsPresenter.AUDIT_IDENTIFIER, "provider-modules");
    }

    @Override
    void saveData() {
        presenter.saveAudit(domainName, attributesProvider.getList(), resourceExists);
    }

	@Override
	void removeData() {
        presenter.removeAudit(domainName, attributesProvider.getList());
		
	}
}
