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

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.security.model.AuthenticationLoginModule;
import org.jboss.as.console.client.shared.subsys.security.model.AuthorizationPolicyProvider;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.shared.subsys.security.model.MappingModule;
import org.jboss.as.console.client.shared.subsys.security.model.SecurityDomain;
import org.jboss.as.console.client.shared.viewframework.AbstractEntityView;
import org.jboss.as.console.client.shared.viewframework.Columns;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridge;
import org.jboss.as.console.client.shared.viewframework.EntityToDmrBridgeImpl;
import org.jboss.as.console.client.shared.viewframework.TabbedFormLayoutPanel;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author David Bosschaert
 */
public class SecurityDomainsView extends AbstractEntityView<SecurityDomain> implements SecurityDomainsPresenter.MyView {
    private final EntityToDmrBridgeImpl<SecurityDomain> bridge;

    AuthenticationEditor authenticationEditor;
    AuthorizationEditor authorizationEditor;
    MappingEditor mappingEditor;
    AuditEditor auditEditor;
    private DefaultCellTable<SecurityDomain> table;
    private TabbedFormLayoutPanel tabBottomPanel;
    private SecurityDomainsPresenter presenter;

    @Inject
    public SecurityDomainsView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(SecurityDomain.class, propertyMetaData);
        bridge = new EntityToDmrBridgeImpl<SecurityDomain>(propertyMetaData, SecurityDomain.class, this, dispatcher);
    }

    @Override
    public Widget createWidget() {
        Widget w = super.createWidget();

        authenticationEditor = new AuthenticationEditor(presenter);
        authorizationEditor = new AuthorizationEditor(presenter);
        mappingEditor = new MappingEditor(presenter);
        auditEditor = new AuditEditor(presenter);
        tabBottomPanel.add(authenticationEditor.asWidget(), authenticationEditor.getEntityName());
        tabBottomPanel.add(authorizationEditor.asWidget(), authorizationEditor.getEntityName());
        tabBottomPanel.add(mappingEditor.asWidget(), mappingEditor.getEntityName());
        tabBottomPanel.add(auditEditor.asWidget(), auditEditor.getEntityName());

        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                SingleSelectionModel<SecurityDomain> ssm = (SingleSelectionModel<SecurityDomain>) table.getSelectionModel();
                presenter.updateDomainSelection(ssm.getSelectedObject());
            }
        });

        return w;
    }

    @Override
    public void setAuthFlagValues(String type, List<String> values) {
        if (SecurityDomainsPresenter.AUTHENTICATION_IDENTIFIER.equals(type)) {
            authenticationEditor.setFlagValues(values);
        } else if (SecurityDomainsPresenter.AUTHORIZATION_IDENTIFIER.equals(type)) {
            authorizationEditor.setFlagValues(values);
        }
    }

    @Override
    protected FormAdapter<SecurityDomain> makeEditEntityDetailsForm() {
        tabBottomPanel = new TabbedFormLayoutPanel(beanType, getFormMetaData(), hideButtons, this);
        return tabBottomPanel;
    }

    @Override
    public EntityToDmrBridge<SecurityDomain> getEntityBridge() {
        return bridge;
    }

    @Override
    protected DefaultCellTable<SecurityDomain> makeEntityTable() {
        table = new DefaultCellTable<SecurityDomain>(5);

        table.addColumn(new Columns.NameColumn(), Columns.NameColumn.LABEL);

        return table;
    }

    @Override
    protected FormAdapter<SecurityDomain> makeAddEntityForm() {
        Form<SecurityDomain> form = new Form(SecurityDomain.class);
        form.setNumColumns(1);
        form.setFields(formMetaData.findAttribute("name").getFormItemForAdd(),
                       formMetaData.findAttribute("cacheType").getFormItemForAdd());
        return form;
    }

    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_security_domains();
    }

    @Override
    public void setPresenter(SecurityDomainsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loadSecurityDomain(String domainName) {
        bridge.loadEntities(domainName);
    }

    @Override
    public void setAuthenticationLoginModules(String domainName, List<AuthenticationLoginModule> modules, boolean resourceExists) {
        authenticationEditor.setData(domainName, modules, resourceExists);
    }

    @Override
    public void setAuthorizationPolicyProviders(String domainName, List<AuthorizationPolicyProvider> policies, boolean resourceExists) {
        authorizationEditor.setData(domainName, policies, resourceExists);
    }

    @Override
    public void setMappingModules(String domainName, List<MappingModule> modules, boolean resourceExists) {
        mappingEditor.setData(domainName, modules, resourceExists);
    }

    @Override
    public void setAuditModules(String domainName, List<GenericSecurityDomainData> modules, boolean resourceExists) {
        auditEditor.setData(domainName, modules, resourceExists);
    }
}
