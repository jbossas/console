package org.jboss.as.console.client.shared.help;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

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
            final Form form,
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

                if(response.get(OUTCOME).asString().equals("success")
                        && response.hasDefined(RESULT))
                {
                    //System.out.println(response);

                    matchAttributes(response, fieldNames, html);
                    matchChildren(response, fieldNames, html);

                    html.appendHtmlConstant("</ul>");
                    callback.onSuccess(new HTML(html.toSafeHtml()));

                }
                else
                {
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
        List<Property> attributes = prototype.get(RESULT).asObject().get("attributes").asPropertyList();

        for(Property prop : attributes)
        {
            String attName = prop.getName();
            ModelNode value = prop.getValue();

            if(fieldNames.contains(attName))
            {
                html.appendHtmlConstant("<li>");
                html.appendEscaped(attName).appendEscaped(": ");
                html.appendEscaped(value.get("description").asString());
                html.appendHtmlConstant("</li>");
            }
        }
    }

    private void matchChildren(ModelNode prototype, List<String> fieldNames, SafeHtmlBuilder html) {

        ModelNode modelNode = prototype.get(RESULT).asObject();
        if(modelNode.hasDefined("children"))
        {
            List<Property> attributes = modelNode.get("children").asPropertyList();

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
        }
    }
}
