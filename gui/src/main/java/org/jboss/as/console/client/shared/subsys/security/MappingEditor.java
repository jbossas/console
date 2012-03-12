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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SimpleKeyProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.security.model.MappingModule;
import org.jboss.as.console.client.shared.subsys.security.wizard.NewMappingModuleWizard;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class MappingEditor extends AbstractDomainDetailEditor<MappingModule> {

    public MappingEditor(SecurityDomainsPresenter presenter) {
        super(presenter, MappingModule.class);
         setDescription(Console.CONSTANTS.subsys_security_mapping_desc());
    }

    @Override
    ProvidesKey<MappingModule> getKeyProvider() {
        return new SimpleKeyProvider<MappingModule>();
    }

    @Override
    void addCustomColumns(DefaultCellTable<MappingModule> table) {
        Column<MappingModule, String> typeColumn = new Column<MappingModule, String>(new TextCell()) {
            @Override
            public String getValue(MappingModule record) {
                return record.getType();
            }
        };
        table.addColumn(typeColumn, Console.CONSTANTS.subsys_security_flagField());
    }

    @Override
    String getEntityName() {
        return Console.CONSTANTS.subsys_security_mapping();
    }

    @Override
    String getStackElementName() {
        return Console.CONSTANTS.subsys_security_mappingModule();
    }

    @Override
    String getStackName() {
        return Console.CONSTANTS.subsys_security_modules();
    }

    @Override
    void saveData() {
        presenter.saveMapping(domainName, attributesProvider.getList(), resourceExists);
    }

    @Override
    Wizard<MappingModule> getWizard() {
        return new NewMappingModuleWizard(this, presenter);
    }
    
	@Override
	void removeData() {
        presenter.removeMapping(domainName, attributesProvider.getList());
		
	}
    
}
