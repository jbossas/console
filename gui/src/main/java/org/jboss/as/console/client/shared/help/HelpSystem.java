package org.jboss.as.console.client.shared.help;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
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

    @Inject
    public HelpSystem(DispatchAsync dispatcher, ApplicationMetaData propertyMetaData) {
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
    }

    public void getAttributeDescriptions(
            ModelNode resourceAddress,
            final FormAdapter form,
            final AsyncCallback<HTML> callback)
    {


        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ADDRESS).set(resourceAddress);
        operation.get(RECURSIVE).set(true);

        // build field name list

        List<String> formItemNames = form.getFormItemNames();
        BeanMetaData beanMetaData = propertyMetaData.getBeanMetaData(form.getConversionType());
        List<PropertyBinding> bindings = beanMetaData.getProperties();
        final List<String> fieldNames = new ArrayList<String>();

        for(PropertyBinding binding : bindings)
        {
            if(!binding.isKey() && formItemNames.contains(binding.getJavaName())) {
                String[] splitDetypedNames = binding.getDetypedName().split("/");
                // last one in the path is the attribute name
                fieldNames.add(splitDetypedNames[splitDetypedNames.length - 1]);
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

                    final SafeHtmlBuilder html = new SafeHtmlBuilder();
                    html.appendHtmlConstant("<table class='help-attribute-descriptions'>");

                    ModelNode payload = response.get(RESULT);

                    ModelNode descriptionModel = null;
                    if(ModelType.LIST.equals(payload.getType()))
                        descriptionModel = payload.asList().get(0);
                    else
                        descriptionModel = payload;

                    matchSubElements(descriptionModel, fieldNames, html);

                    html.appendHtmlConstant("</table>");
                    callback.onSuccess(new HTML(html.toSafeHtml()));

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

    public void getMetricDescriptions(
            AddressCallback address,
            Column[] columns,
            final AsyncCallback<HTML> callback)
    {

        final List<String> attributeNames = new LinkedList<String>();
        for(Column c : columns)
            attributeNames.add(c.getDeytpedName());

        final ModelNode operation = address.getAddress();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);

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
                    final SafeHtmlBuilder html = new SafeHtmlBuilder();
                    html.appendHtmlConstant("<table class='help-attribute-descriptions'>");

                    ModelNode payload = response.get(RESULT);

                    ModelNode descriptionModel = null;
                    if(ModelType.LIST.equals(payload.getType()))
                        descriptionModel = payload.asList().get(0);
                    else
                        descriptionModel = payload;


                    matchSubElements(descriptionModel, attributeNames, html);

                    html.appendHtmlConstant("</table>");

                    callback.onSuccess(new HTML(html.toSafeHtml()));
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }


    private static void matchSubElements(ModelNode descriptionModel, List<String> fieldNames, SafeHtmlBuilder html) {

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

                    if(fieldNames.contains(childName))
                    {
                        // make sure it's not processed twice
                        fieldNames.remove(childName);

                        html.appendHtmlConstant("<tr class='help-field-row'>");
                        html.appendHtmlConstant("<td class='help-field-name'>");
                        html.appendEscaped(childName).appendEscaped(": ");
                        html.appendHtmlConstant("</td>");
                        html.appendHtmlConstant("<td class='help-field-desc'>");
                        html.appendEscaped(value.get("description").asString());
                        html.appendHtmlConstant("</td>");
                        html.appendHtmlConstant("</tr>");

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

                        matchSubElements(childDesc.get(MODEL_DESCRIPTION, modDescProp.getName()), fieldNames, html);

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
