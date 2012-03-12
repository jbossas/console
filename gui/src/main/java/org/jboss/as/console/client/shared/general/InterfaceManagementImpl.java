package org.jboss.as.console.client.shared.general;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanUtils;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.general.validation.CompositeDecision;
import org.jboss.as.console.client.shared.general.validation.DecisionTree;
import org.jboss.as.console.client.shared.general.validation.ValidationResult;
import org.jboss.as.console.client.shared.general.wizard.NewInterfaceWizard;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
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
 * @date 11/17/11
 */
public class InterfaceManagementImpl implements InterfaceManagement {

    private DispatchAsync dispatcher;
    private DefaultWindow window;
    private EntityAdapter<Interface> entityAdapter;
    private BeanMetaData beanMetaData;
    private InterfaceManagement.Callback callback;

    public InterfaceManagementImpl(
            DispatchAsync dispatcher,
            EntityAdapter<Interface> entityAdapter,
            BeanMetaData beanMetaData) {
        this.dispatcher = dispatcher;
        this.entityAdapter = entityAdapter;
        this.beanMetaData = beanMetaData;
    }

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void closeDialoge() {
        window.hide();
    }

    @Override
    public void launchNewInterfaceDialogue() {
        window = new DefaultWindow(Console.MESSAGES.createTitle("Network Interface"));
        window.setWidth(480);
        window.setHeight(360);

        window.trapWidget(
                new NewInterfaceWizard(this).asWidget()
        );

        window.setGlassEnabled(true);
        window.center();
    }

    @Override
    public void createNewInterface(final Interface entity) {

        window.hide();

        // artificial values need to be merged manually
        String wildcard = entity.getAddressWildcard();

        entity.setAnyAddress(wildcard.equals(Interface.ANY_ADDRESS));
        entity.setAnyIP4Address(wildcard.equals(Interface.ANY_IP4));
        entity.setAnyIP6Address(wildcard.equals(Interface.ANY_IP6));

        // TODO: https://issues.jboss.org/browse/AS7-2670

        // Workaround: Create the operation manually
        //ModelNode operation = entityAdapter.fromEntity(entity);

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(callback.getBaseAddress());
        operation.get(ADDRESS).add("interface", entity.getName());
        operation.get(OP).set(ADD);
        //operation.get(NAME).set(entity.getName());

        if(isSet(entity.getInetAddress()))
            operation.get("inet-address").set(entity.getInetAddress());
        else if(entity.isAnyAddress())
            operation.get("any-address").set(true);
        else if(entity.isAnyIP4Address())
            operation.get("any-ip4-address").set(true);
        else if(entity.isAnyIP6Address())
            operation.get("any-ip6-address").set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                if(ModelNodeUtil.indicatesSuccess(response))
                {
                    Console.info(Console.MESSAGES.added("Network Interface"));
                }
                else
                {
                    Console.error(Console.MESSAGES.addingFailed("Network Interface"),
                            response.getFailureDescription());
                }

                loadInterfaces();
            }
        });
    }



    @Override
    public void onRemoveInterface(final Interface entity) {

        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(callback.getBaseAddress());
        operation.get(ADDRESS).add("interface", entity.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();

                System.out.println(response);
                if(ModelNodeUtil.indicatesSuccess(response))
                {
                    Console.info(Console.MESSAGES.deleted("Network Interface"));
                }
                else
                {
                    Console.error(Console.MESSAGES.deletionFailed("Network Interface"),
                            response.getFailureDescription());
                }

                loadInterfaces();
            }
        });
    }

    public void loadInterfaces() {
        callback.loadInterfaces();
    }

    @Override
    public ValidationResult validateInterfaceConstraints(final Interface entity, Map<String, Object> changeset)
    {

        //long s0 = System.currentTimeMillis();

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

        // dump log
        StringBuilder sb = new StringBuilder();
        for(String s : decisions)
            sb.append(s).append(" \n");
        System.out.println(sb.toString());

        //System.out.println("** Exec time: "+(System.currentTimeMillis()-s0)+" ms **");
        return validation;
    }

    @Override
    public void onSaveInterface(final Interface entity, Map<String, Object> changeset) {

        doPersistChanges(entity, changeset);
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
        ModelNode address = addressBinding.asResource(callback.getBaseAddress(), entity.getName());
        ModelNode operation = entityAdapter.fromChangeset(workAround, address);

        //System.out.println(operation);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                //System.out.println(response);

                if(ModelNodeUtil.indicatesSuccess(response))
                {
                    Console.info(Console.MESSAGES.modified("Network Interface"));
                }
                else
                {
                    Console.error(Console.MESSAGES.modificationFailed("Network Interface"),
                            response.getFailureDescription());
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
