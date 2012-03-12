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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.dispatch.impl.SimpleDMRResponseHandler;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.subsys.security.model.AbstractAuthData;
import org.jboss.as.console.client.shared.subsys.security.model.AuthenticationLoginModule;
import org.jboss.as.console.client.shared.subsys.security.model.AuthorizationPolicyProvider;
import org.jboss.as.console.client.shared.subsys.security.model.GenericSecurityDomainData;
import org.jboss.as.console.client.shared.subsys.security.model.MappingModule;
import org.jboss.as.console.client.shared.subsys.security.model.SecurityDomain;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Bosschaert
 * @author Heiko Braun
 */
public class SecurityDomainsPresenter
        extends Presenter<SecurityDomainsPresenter.MyView, SecurityDomainsPresenter.MyProxy>
    {

    private static final String CLASSIC = "classic";
    private static final String SECURITY_DOMAIN = "security-domain";

    public static final String AUDIT_IDENTIFIER = "audit";
    public static final String AUTHENTICATION_IDENTIFIER = "authentication";
    public static final String AUTHORIZATION_IDENTIFIER = "authorization";
    public static final String MAPPING_IDENTIFIER = "mapping";

    public static final String SECURITY_SUBSYSTEM = "security";

    private final DispatchAsync dispatcher;
    private final BeanFactory factory;
    private final RevealStrategy revealStrategy;
    private final EntityAdapter<SecurityDomain> entityAdapter;
    private PlaceManager placeManager;

    private String selectedDomain;

    public PlaceManager getPlaceManager() {
        return this.placeManager;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.SecurityDomainsPresenter)
    public interface MyProxy extends Proxy<SecurityDomainsPresenter>, Place {
    }

    public interface MyView extends View, FrameworkView {
        void setPresenter(SecurityDomainsPresenter presenter);
        void setAuthenticationLoginModules(String domainName, List<AuthenticationLoginModule> modules, boolean resourceExists);
        void setAuthorizationPolicyProviders(String domainName, List<AuthorizationPolicyProvider> providers, boolean resourceExists);
        void setMappingModules(String domainName, List<MappingModule> modules, boolean resourceExists);
        void setAuditModules(String domainName, List<GenericSecurityDomainData> modules, boolean resourceExists);

        void loadSecurityDomain(String domainName);

        @Deprecated
        void setAuthFlagValues(String type, List<String> values);

        void setSelectedDomain(String selectedDomain);
    }

    @Inject
    public SecurityDomainsPresenter(EventBus eventBus, MyView view, MyProxy proxy,
                                    DispatchAsync dispatcher, BeanFactory factory, RevealStrategy revealStrategy,
                                    ApplicationMetaData appMetaData, PlaceManager placeManager) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.factory = factory;
        this.revealStrategy = revealStrategy;
        this.entityAdapter = new EntityAdapter<SecurityDomain>(SecurityDomain.class, appMetaData);

        this.placeManager = placeManager;
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onReset() {
        super.onReset();
        getView().initialLoad();
        getView().setSelectedDomain(selectedDomain);
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);

        Console.schedule(new Command() {
            @Override
            public void execute() {
                loadAuthFlagValues();
            }
        });
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        selectedDomain = request.getParameter("name", null);
    }

    private void loadAuthFlagValues() {
        loadAuthFlagValues(AUTHORIZATION_IDENTIFIER, "policy-modules");
        loadAuthFlagValues(AUTHENTICATION_IDENTIFIER, "login-modules");
    }

    @Deprecated
    private void loadAuthFlagValues(final String type, final String attrName) {
        ModelNode operation = createOperation(ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, "*");
        operation.get(ModelDescriptionConstants.ADDRESS).add(type, CLASSIC);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                List<ModelNode> res = response.get(ModelDescriptionConstants.RESULT).asList();
                if (res.size() == 0)
                    return;

                ModelNode attrDesc = res.get(0).get(ModelDescriptionConstants.RESULT,
                        ModelDescriptionConstants.ATTRIBUTES, attrName,
                        ModelDescriptionConstants.VALUE_TYPE, "flag");

                List<String> values = new ArrayList<String>();
                for (ModelNode option : attrDesc.get(ModelDescriptionConstants.ALLOWED).asList()) {
                    values.add(option.asString());
                }

                getView().setAuthFlagValues(type, values);
            }
        });
    }

    public void updateDomainSelection(final SecurityDomain domain) {
        // load sub-elements which are not automatically loaded by the framework
        ModelNode operation = createOperation(ModelDescriptionConstants.READ_RESOURCE_OPERATION);
        operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, domain.getName());
        operation.get(ModelDescriptionConstants.RECURSIVE).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                ModelNode model = response.get(ModelDescriptionConstants.RESULT);

                loadGeneric(model, domain, AUTHORIZATION_IDENTIFIER, "policy-modules", AuthorizationPolicyProvider.class,
                        new CustomLoadHandler<AuthorizationPolicyProvider>() {
                            @Override
                            public void readFromModel(ModelNode n, AuthorizationPolicyProvider object) {
                                object.setFlag(n.get("flag").asString());
                            }

                            @Override
                            public void setInView(List<AuthorizationPolicyProvider> modules, boolean resourceExists) {
                                getView().setAuthorizationPolicyProviders(domain.getName(), modules, resourceExists);
                            }
                        });

                loadGeneric(model, domain, AUTHENTICATION_IDENTIFIER, "login-modules", AuthenticationLoginModule.class,
                        new CustomLoadHandler<AuthenticationLoginModule>() {
                            @Override
                            public void readFromModel(ModelNode n, AuthenticationLoginModule object) {
                                object.setFlag(n.get("flag").asString());
                            }

                            @Override
                            public void setInView(List<AuthenticationLoginModule> modules, boolean resourceExists) {
                                getView().setAuthenticationLoginModules(domain.getName(), modules, resourceExists);
                            }
                        });

                loadGeneric(model, domain, MAPPING_IDENTIFIER, "mapping-modules", MappingModule.class,
                        new CustomLoadHandler<MappingModule>() {
                            @Override
                            public void readFromModel(ModelNode n, MappingModule object) {
                                object.setType(n.get("type").asString());
                            }

                            @Override
                            public void setInView(List<MappingModule> modules, boolean resourceExists) {
                                getView().setMappingModules(domain.getName(), modules, resourceExists);
                            }
                        });

                loadGeneric(model, domain, AUDIT_IDENTIFIER, "provider-modules", GenericSecurityDomainData.class,
                        new CustomLoadHandler<GenericSecurityDomainData>() {
                            @Override
                            public void readFromModel(ModelNode n, GenericSecurityDomainData object) {
                            }

                            @Override
                            public void setInView(List<GenericSecurityDomainData> modules, boolean resourceExists) {
                                getView().setAuditModules(domain.getName(), modules, resourceExists);
                            }
                        });
            }
        });
    }

    private <T extends GenericSecurityDomainData> void loadGeneric(ModelNode model, SecurityDomain domain, String type, String attrName, Class<T> cls,
                                                                   CustomLoadHandler<T> customHandler) {
        List<T> modules = new ArrayList<T>();
        boolean resourceExists = false;
        if (model.hasDefined(type)) {
            ModelNode subModel = model.get(type, CLASSIC);
            resourceExists = subModel.hasDefined(attrName);

            if (resourceExists) {
                for (ModelNode node : subModel.get(attrName).asList()) {
                    T pm = factory.create(cls).as();

                    pm.setCode(node.get("code").asString());
                    customHandler.readFromModel(node, pm);

                    if (node.hasDefined("module-options")) {
                        List<Property> pl = node.require("module-options").asPropertyList();
                        pm.setProperties(entityAdapter.fromDMRPropertyList(pl));
                    }

                    modules.add(pm);
                }
            }
        }

        customHandler.setInView(modules, resourceExists);
    }

    private static class CustomAuthSaveFieldhandler<P extends AbstractAuthData> extends CustomSaveHandler<P>{
        @Override
        public void setInModel(ModelNode n, P object) {
            n.get("flag").set(object.getFlag());
        }
    }

    public void saveAuthorization(String domainName, List<AuthorizationPolicyProvider> list, boolean resourceExists) {
        saveGeneric(domainName, list, AUTHORIZATION_IDENTIFIER, "policy-modules", resourceExists,
                new CustomAuthSaveFieldhandler<AuthorizationPolicyProvider>());
    }

    public void saveAuthentication(String domainName, List<AuthenticationLoginModule> list, boolean resourceExists) {

        saveGeneric(domainName, list, AUTHENTICATION_IDENTIFIER, "login-modules", resourceExists,
                new CustomAuthSaveFieldhandler<AuthenticationLoginModule>());
    }

    public void saveMapping(String domainName, List<MappingModule> list, boolean resourceExists) {
        saveGeneric(domainName, list, MAPPING_IDENTIFIER, "mapping-modules", resourceExists,
                new CustomSaveHandler<MappingModule>() {
                    @Override
                    public void setInModel(ModelNode n, MappingModule object) {
                        n.get("type").set(object.getType());
                    }
                });
    }

    public void saveAudit(String domainName, List<GenericSecurityDomainData> list, boolean resourceExists) {
        saveGeneric(domainName, list, AUDIT_IDENTIFIER, "provider-modules", resourceExists,
                new CustomSaveHandler<GenericSecurityDomainData>());
    }
    
    public void removeAuthorization(String domainName, List<AuthorizationPolicyProvider> list) {
        removeGeneric(domainName, list, AUTHORIZATION_IDENTIFIER, "policy-modules",
                new CustomAuthSaveFieldhandler<AuthorizationPolicyProvider>());
    }

    public void removeAuthentication(String domainName, List<AuthenticationLoginModule> list) {

        removeGeneric(domainName, list, AUTHENTICATION_IDENTIFIER, "login-modules",
                new CustomAuthSaveFieldhandler<AuthenticationLoginModule>());
    }

    public void removeMapping(String domainName, List<MappingModule> list) {
        removeGeneric(domainName, list, MAPPING_IDENTIFIER, "mapping-modules",
                new CustomSaveHandler<MappingModule>() {
                    @Override
                    public void setInModel(ModelNode n, MappingModule object) {
                        n.get("type").set(object.getType());
                    }
                });
    }
    
    
    public void removeAudit(String domainName, List<GenericSecurityDomainData> list) {
        removeGeneric(domainName, list, AUDIT_IDENTIFIER, "provider-modules",
                new CustomSaveHandler<GenericSecurityDomainData>());
    }
    
    
    public <T extends GenericSecurityDomainData> void removeGeneric(final String domainName, List<T> list, String type, String attrName,
            CustomSaveHandler<T> customHandler) {

    	ModelNode operation = null;

        ModelNode valueList = new ModelNode();
        valueList.setEmptyList();

        for (T pm : list) {
            ModelNode n = new ModelNode();
            n.get("code").set(pm.getCode());

            if (customHandler != null) {
                customHandler.setInModel(n, pm);
            }

            List<PropertyRecord> props = pm.getProperties();
            if (props != null)
                n.get("module-options").set(entityAdapter.fromEntityPropertyList(props));

            valueList.add(n);
        }

            operation = createOperation(ModelDescriptionConstants.REMOVE);
            operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, domainName);
            operation.get(ModelDescriptionConstants.ADDRESS).add(type, CLASSIC);
            operation.get(ModelDescriptionConstants.NAME).set(attrName);

            if(list.size()>0)
                operation.get("value").set(valueList);
            else
                operation.get("value").setEmptyList();

        
        dispatcher.execute(new DMRAction(operation),
                new SimpleDMRResponseHandler(ModelDescriptionConstants.REMOVE,
                        attrName, domainName, new Command() {
                    @Override
                    public void execute() {
                        getView().loadSecurityDomain(domainName);
                    }
                }));
    }
    

    // TODO: https://issues.jboss.org/browse/AS7-2936
    public <T extends GenericSecurityDomainData> void saveGeneric(final String domainName, List<T> list, String type, String attrName, boolean resourceExists,
                                                                  CustomSaveHandler<T> customHandler) {

        ModelNode operation = null;

        ModelNode valueList = new ModelNode();
        valueList.setEmptyList();

        for (T pm : list) {
            ModelNode n = new ModelNode();
            n.get("code").set(pm.getCode());

            if (customHandler != null) {
                customHandler.setInModel(n, pm);
            }

            List<PropertyRecord> props = pm.getProperties();
            if (props != null)
                n.get("module-options").set(entityAdapter.fromEntityPropertyList(props));

            valueList.add(n);
        }

        if (resourceExists) {
            operation = createOperation(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION);
            operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, domainName);
            operation.get(ModelDescriptionConstants.ADDRESS).add(type, CLASSIC);
            operation.get(ModelDescriptionConstants.NAME).set(attrName);

            if(list.size()>0)
                operation.get("value").set(valueList);
            else
                operation.get("value").setEmptyList();

        } else {
            operation = createOperation(ModelDescriptionConstants.ADD);
            operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, domainName);
            operation.get(ModelDescriptionConstants.ADDRESS).add(type, CLASSIC);

             if(list.size()>0)
                operation.get(attrName).set(valueList);
            else
                operation.get(attrName).setEmptyList();

        }
        
        dispatcher.execute(new DMRAction(operation),
                new SimpleDMRResponseHandler(ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION,
                        attrName, domainName, new Command() {
                    @Override
                    public void execute() {
                        getView().loadSecurityDomain(domainName);
                    }
                }));
    }


    public void getDescription(String type, final DescriptionCallBack callback) {
        ModelNode operation = createOperation(ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ModelDescriptionConstants.ADDRESS).add(SECURITY_DOMAIN, "*");
        operation.get(ModelDescriptionConstants.ADDRESS).add(type, CLASSIC);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                List<ModelNode> resList = response.get(ModelDescriptionConstants.RESULT).asList();
                if (resList.size() == 0)
                    return;

                callback.setDescription(resList.get(0).get(ModelDescriptionConstants.RESULT));
            }
        });
    }

    private ModelNode createOperation(String operator) {
        ModelNode operation = new ModelNode();
        operation.get(ModelDescriptionConstants.OP).set(operator);
        operation.get(ModelDescriptionConstants.ADDRESS).set(Baseadress.get());
        operation.get(ModelDescriptionConstants.ADDRESS).add(ModelDescriptionConstants.SUBSYSTEM, SECURITY_SUBSYSTEM);
        return operation;
    }

    private interface CustomLoadHandler<P> {
        void readFromModel(ModelNode n, P object);
        void setInView(List<P> modules, boolean resourceExists);
    }

    private static class CustomSaveHandler<P> {
        void setInModel(ModelNode n, P object) {}
    }

    public interface DescriptionCallBack {
        public void setDescription(ModelNode desc);
    }
}
