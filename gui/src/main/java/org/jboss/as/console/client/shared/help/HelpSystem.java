package org.jboss.as.console.client.shared.help;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.shared.Preferences;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 6/8/11
 */
public class HelpSystem {

    private DispatchAsync dispatcher;
    private ApplicationMetaData propertyMetaData;

    class Lookup
    {
        String detypedName;
        String javaName;

        Lookup(String detypedName, String javaName) {
            this.detypedName = detypedName;
            this.javaName = javaName;
        }

        public String getDetypedName() {
            return detypedName;
        }

        public String getJavaName() {
            return javaName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Lookup)) return false;

            Lookup lookup = (Lookup) o;

            if (!detypedName.equals(lookup.detypedName)) return false;
            if (!javaName.equals(lookup.javaName)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = detypedName.hashCode();
            result = 31 * result + javaName.hashCode();
            return result;
        }
    }
    @Inject
    public HelpSystem(DispatchAsync dispatcher, ApplicationMetaData propertyMetaData) {
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
    }

    public void getAttributeDescriptions(
            ModelNode resourceAddress,
            final FormAdapter form,
            final AsyncCallback<List<FieldDesc>> callback)
    {


        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ADDRESS).set(resourceAddress);
        operation.get(RECURSIVE).set(true);
        operation.get(LOCALE).set(getLocale());

        // build field name list

        List<String> formItemNames = form.getFormItemNames();
        BeanMetaData beanMetaData = propertyMetaData.getBeanMetaData(form.getConversionType());
        List<PropertyBinding> bindings = beanMetaData.getProperties();
        final List<Lookup> fieldNames = new ArrayList<Lookup>();

        for(PropertyBinding binding : bindings)
        {
            if(!binding.isKey() && formItemNames.contains(binding.getJavaName())) {
                String[] splitDetypedNames = binding.getDetypedName().split("/");
                // last one in the path is the attribute name
                Lookup lookup = new Lookup(splitDetypedNames[splitDetypedNames.length - 1], binding.getJavaName());
                if(!fieldNames.contains(lookup))
                    fieldNames.add(lookup);
            }
        }

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    Log.debug(response.toString());
                    onFailure(new Exception(response.getFailureDescription()));
                }
                else
                {
                    List<FieldDesc> fields = new ArrayList<FieldDesc>();
                    ModelNode payload = response.get(RESULT);

                    ModelNode descriptionModel = null;
                    if(ModelType.LIST.equals(payload.getType()))
                        descriptionModel = payload.asList().get(0);
                    else
                        descriptionModel = payload;

                    matchSubElements(descriptionModel, fieldNames, fields);


                    callback.onSuccess(fields);

                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    public interface AddressCallback
    {
        ModelNode getAddress();
    }

    private String getLocale() {
        String locale = Preferences.get(Preferences.Key.LOCALE) != null ?
                Preferences.get(Preferences.Key.LOCALE) : "en";
        return locale;

    }
    public void getMetricDescriptions(
            AddressCallback address,
            Column[] columns,
            final AsyncCallback<List<FieldDesc>> callback)
    {

        final List<Lookup> attributeNames = new LinkedList<Lookup>();
        for(Column c : columns)
            attributeNames.add(new Lookup(c.getDeytpedName(), c.getLabel()));

        final ModelNode operation = address.getAddress();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(LOCALE).set(getLocale());

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();



                if(response.isFailure())
                {
                    Log.debug(response.toString());
                    onFailure(new Exception(response.getFailureDescription()));
                }
                else
                {
                    List<FieldDesc> fields = new ArrayList<FieldDesc>();

                    ModelNode payload = response.get(RESULT);

                    ModelNode descriptionModel = null;
                    if(ModelType.LIST.equals(payload.getType()))
                        descriptionModel = payload.asList().get(0);
                    else
                        descriptionModel = payload;


                    matchSubElements(descriptionModel, attributeNames, fields);

                    callback.onSuccess(fields);
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }


    private static void matchSubElements(ModelNode descriptionModel, List<Lookup> fieldNames, List<FieldDesc> fields) {

        if (descriptionModel.hasDefined(RESULT))
            descriptionModel = descriptionModel.get(RESULT).asObject();

        try {


            // match attributes
            if(descriptionModel.hasDefined(ATTRIBUTES))
            {
                List<Property> elements = descriptionModel.get(ATTRIBUTES).asPropertyList();

                for(Property element : elements)
                {
                    String childName = element.getName();
                    ModelNode value = element.getValue();

                    for(Lookup lookup : fieldNames)
                    {
                        if(lookup.getDetypedName().equals(childName))
                        {
                            FieldDesc desc = new FieldDesc(lookup.getJavaName(), value.get("description").asString());
                            if(!fields.contains(desc))
                                fields.add(desc);
                        }
                    }
                }
            }

            if(fieldNames.isEmpty())
                return;

            // visit child elements
            if (descriptionModel.hasDefined("children")) {
                List<Property> children = descriptionModel.get("children").asPropertyList();

                for(Property child : children )
                {
                    ModelNode childDesc = child.getValue();
                    for (Property modDescProp : childDesc.get(MODEL_DESCRIPTION).asPropertyList()) {

                        matchSubElements(childDesc.get(MODEL_DESCRIPTION, modDescProp.getName()), fieldNames, fields);

                        // exit early
                        if(fieldNames.isEmpty())
                            return;

                    }
                }
            }


        } catch (IllegalArgumentException e) {
            Log.error("Failed to read help descriptionModel", e);
        }
    }
}
