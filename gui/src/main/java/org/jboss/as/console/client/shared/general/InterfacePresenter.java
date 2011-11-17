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

package org.jboss.as.console.client.shared.general;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.model.LoadInterfacesCmd;
import org.jboss.as.console.client.shared.general.validation.CompositeDecision;
import org.jboss.as.console.client.shared.general.validation.DecisionTree;
import org.jboss.as.console.client.shared.general.validation.ValidationResult;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 5/17/11
 */
public class InterfacePresenter extends Presenter<InterfacePresenter.MyView, InterfacePresenter.MyProxy> {

    private final PlaceManager placeManager;
    private BeanFactory factory;
    private DispatchAsync dispatcher;
    private LoadInterfacesCmd loadInterfacesCmd;
    private RevealStrategy revealStrategy;
    private DefaultWindow window;
    private EntityAdapter<Interface> entityAdapter;
    private BeanMetaData beanMetaData;

    @ProxyCodeSplit
    @NameToken(NameTokens.InterfacePresenter)
    public interface MyProxy extends Proxy<InterfacePresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(InterfacePresenter presenter);
        void setInterfaces(List<Interface> interfaces);
    }

    @Inject
    public InterfacePresenter(
            EventBus eventBus, MyView view, MyProxy proxy,
            PlaceManager placeManager,
            DispatchAsync dispatcher,
            BeanFactory factory, RevealStrategy revealStrategy,
            ApplicationMetaData metaData) {
        super(eventBus, view, proxy);

        this.placeManager = placeManager;
        this.factory = factory;
        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;

        ModelNode address = new ModelNode();
        address.setEmptyList();
        loadInterfacesCmd = new LoadInterfacesCmd(dispatcher, address, metaData);
        entityAdapter = new EntityAdapter<Interface>(Interface.class, metaData);
        beanMetaData = metaData.getBeanMetaData(Interface.class);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }


    @Override
    protected void onReset() {
        super.onReset();
        loadInterfaces();
    }

    private void loadInterfaces() {

        loadInterfacesCmd.execute(new SimpleCallback<List<Interface>>() {
            @Override
            public void onSuccess(List<Interface> result) {
                getView().setInterfaces(result);
            }
        });

    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }

    public void launchNewInterfaceDialogue() {
        window = new DefaultWindow("New Interface Declaration");
        window.setWidth(480);
        window.setHeight(360);

        window.setWidget(
                new NewInterfaceWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    public void createNewInterface(Interface entity) {
        ModelNode operation = entityAdapter.fromEntity(entity);
        operation.get(ADDRESS).add("interface", entity.getName());
        operation.get(OP).set(ADD);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = ModelNode.fromBase64(dmrResponse.getResponseText());
                System.out.println(result);
            }
        });
    }

    public void closeDialoge() {
        window.hide();
    }

    public void onRemoveInterface(Interface entity) {

    }

    public ValidationResult validateInterfaceConstraints(final Interface entity, Map<String, Object> changeset)
    {
        AutoBean<Interface> autoBean = AutoBeanUtils.getAutoBean(entity);
        Map<String, Object> properties = AutoBeanUtils.getAllProperties(autoBean);

        final List<String> decisions = new LinkedList<String>();

        DecisionTree.DecisionLog log = new DecisionTree.DecisionLog() {
            int index = 0;
            @Override
            public void append(String message) {
                index++;
                decisions.add("["+index+"] " + message);
            }
        };

        CompositeDecision decisionTree = new CompositeDecision();
        decisionTree.setLog(log);

        ValidationResult validation = decisionTree.validate(entity, changeset);
        for(String detail : decisionTree.getDetailMessages())
            validation.addMessage(detail);

        // dump log
        StringBuilder sb = new StringBuilder();
        for(String s : decisions)
            sb.append(s).append(" \n");
        System.out.println(sb.toString());

        return validation;
    }

    public void onSaveInterface(final Interface entity, Map<String, Object> changeset) {

        doPersistChanges(entity, changeset);

        /*CompositeDecision decisionTree = new CompositeDecision();
        ValidationResult validation = decisionTree.validate(entity, changeset);
        if(validation.isValid())
        {
            //Console.info(validation.asMessageString());
            doPersistChanges(entity, changeset);
        }
        else {

            SafeHtmlBuilder html = new SafeHtmlBuilder();
            html.appendHtmlConstant("<h3>");
            html.appendEscaped(validation.asMessageString());
            html.appendHtmlConstant("</h3>");

            for(String detail : decisionTree.getDetailMessages())
                html.appendEscaped(detail).appendHtmlConstant("<br/>");

            Feedback.alert("Invalid Interface Constraints", html.toSafeHtml());
        }  */

    }

    private void doPersistChanges(final Interface entity, Map<String,Object> changeset)
    {
        // artificial values need to be merged manually
        String wildcard = entity.getAddressWildcard();

        changeset.put("anyAddress", wildcard.equals(Interface.ANY_ADDRESS) ? true : FormItem.VALUE_SEMANTICS.UNDEFINED);
        changeset.put("anyIP4Address", wildcard.equals(Interface.ANY_IP4) ? true : FormItem.VALUE_SEMANTICS.UNDEFINED);
        changeset.put("anyIP6Address", wildcard.equals(Interface.ANY_IP6) ? true : FormItem.VALUE_SEMANTICS.UNDEFINED);

        // TODO: https://issues.jboss.org/browse/AS7-2670
        Map<String,Object> workAround = new HashMap<String,Object>(changeset);
        Set<String> keys = changeset.keySet();
        for(String key : keys)
        {
            Object value = changeset.get(key);
            if(value instanceof String)
            {
                // empty string into UNDEFINED
                workAround.put(key, ((String)value).isEmpty() ? FormItem.VALUE_SEMANTICS.UNDEFINED : value);
            }
            else if(value instanceof Boolean)
            {
                // boolean false into UNDEFINED
                workAround.put(key, ((Boolean)value) ? value : FormItem.VALUE_SEMANTICS.UNDEFINED );
            }
        }

        AddressBinding addressBinding = beanMetaData.getAddress();
        ModelNode address = addressBinding.asResource(Baseadress.get(), entity.getName());
        ModelNode operation = entityAdapter.fromChangeset(workAround, address);

        //System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = ModelNode.fromBase64(dmrResponse.getResponseText());
                //System.out.println(response);

                if(ModelNodeUtil.indicatesSuccess(response))
                {
                    Console.info("Success: Update interface "+entity.getName());
                }
                else
                {
                    Console.error("Error: Failed to update interface " + entity.getName(),
                            response.get("failure-description").asString());
                }

                loadInterfaces();
            }
        });
    }

    public static boolean isSet(String value)
    {
        return value!=null && !value.isEmpty();
    }
}
