package org.jboss.as.console.client.shared.help;

import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.dmr.client.ModelDescriptionConstants.CHILDREN;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_DESCRIPTION_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * @author Heiko Braun
 * @date 6/8/11
 */
public class HelpSystem {

    private DispatchAsync dispatcher;
    private PropertyMetaData propertyMetaData;

    @Inject
    public HelpSystem(DispatchAsync dispatcher, PropertyMetaData propertyMetaData) {
        this.dispatcher = dispatcher;
        this.propertyMetaData = propertyMetaData;
    }

    public void getAttributeDescriptions(
            ModelNode resourceAddress,
            final FormAdapter form,
            final AsyncCallback<Widget> callback)
    {

        final SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<ul class='help-attribute-descriptions'>");

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION);
        operation.get(ADDRESS).set(resourceAddress);

        // build field name list

        List<String> formItemNames = form.getFormItemNames();
        List<PropertyBinding> bindings = propertyMetaData.getBindingsForType(form.getConversionType());
        final List<String> fieldNames = new ArrayList<String>();

        for(PropertyBinding binding : bindings)
        {
            if(formItemNames.contains(binding.getJavaName()))
                fieldNames.add(binding.getDetypedName());
        }

        dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());

                if (response.get(OUTCOME).asString().equals("success")
                        && response.hasDefined(RESULT)) {

                    List<ModelNode> modelNodes = response.get(RESULT).asList();
                    for (ModelNode res : modelNodes) {
                        matchAttributes(res, fieldNames, html);
                        matchChildren(res, fieldNames, html);
                    }

                    html.appendHtmlConstant("</ul>");
                    callback.onSuccess(new HTML(html.toSafeHtml()));

                } else {
                    System.out.println(operation);
                    System.out.println(response);
                    onFailure(new Exception(""));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    private void matchAttributes(ModelNode prototype, List<String> fieldNames, SafeHtmlBuilder html) {
        matchSubElement(prototype, fieldNames, html, ATTRIBUTES);
    }

    private void matchChildren(ModelNode prototype, List<String> fieldNames, SafeHtmlBuilder html) {
        matchSubElement(prototype, fieldNames, html, CHILDREN);
    }

    private void matchSubElement(ModelNode prototype, List<String> fieldNames, SafeHtmlBuilder html, String entity) {
        if (prototype.hasDefined(RESULT))
            prototype = prototype.get(RESULT).asObject();

        if (!prototype.hasDefined(entity))
            return;

        try {
            List<Property> attributes = prototype.get(entity).asPropertyList();

            for(Property prop : attributes)
            {
                String childName = prop.getName();
                ModelNode value = prop.getValue();

                if(fieldNames.contains(childName))
                {
                    html.appendHtmlConstant("<li>");
                    html.appendEscaped(childName).appendEscaped(": ");
                    html.appendEscaped(value.get("description").asString());
                    html.appendHtmlConstant("</li>");
                }
            }
        } catch (IllegalArgumentException e) {
             Log.error("Failed to read help description", e);
        }
    }
}
