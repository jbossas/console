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
package org.jboss.as.console.client.mbui.cui.reification;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.mbui.aui.aim.InteractionUnit;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceAttribute;
import org.jboss.as.console.client.mbui.aui.mapping.as7.ResourceMapping;
import org.jboss.as.console.client.mbui.cui.Context;
import org.jboss.as.console.client.mbui.cui.ReificationStrategy;
import org.jboss.as.console.client.mbui.cui.widgets.ModelNodeForm;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class FormStrategy implements ReificationStrategy<ReificationWidget>
{
    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        FormAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new FormAdapter(interactionUnit);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof org.jboss.as.console.client.mbui.aui.aim.as7.Form;
    }

    class FormAdapter implements ReificationWidget
    {
        final ModelNodeForm form;
        final InteractionUnit interactionUnit;

        FormAdapter(final InteractionUnit interactionUnit)
        {
            this.interactionUnit = interactionUnit;
            this.form = new ModelNodeForm();
            this.form.setNumColumns(2);
            this.form.setEnabled(false);

            ModelNode modelNode = ModelNode.fromBase64(base64);
            ModelNode description = modelNode.get("result").get("step-1").get("result");
            List<Property> attributeDescriptions = description.get("attributes").asPropertyList();

            // TODO There can be many mappings. How do we know the ID for the resource mapping?
            ResourceMapping resourceMapping = (ResourceMapping)
                    this.interactionUnit.getEntityContext()
                            .getMapping(interactionUnit.getId());

            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            List<FormItem> items = new ArrayList<FormItem>(attributes.size());

            for (ResourceAttribute attribute : attributes)
            {
                for(Property attr : attributeDescriptions)
                {
                    if(!attr.getName().equals(attribute.getName()))
                        continue;

                    ModelType type = ModelType.valueOf(attr.getValue().get("type").asString());

                    switch(type)
                    {
                        case BOOLEAN:
                            CheckBoxItem checkBoxItem = new CheckBoxItem(attr.getName(), attr.getName().toUpperCase());
                            items.add(checkBoxItem);
                            break;
                        case DOUBLE:
                            NumberBoxItem num = new NumberBoxItem(attr.getName(), attr.getName().toUpperCase());
                            items.add(num);
                            break;
                        case LONG:
                            NumberBoxItem num2 = new NumberBoxItem(attr.getName(), attr.getName().toUpperCase());
                            items.add(num2);
                            break;
                        case INT:
                            NumberBoxItem num3 = new NumberBoxItem(attr.getName(), attr.getName().toUpperCase());
                            items.add(num3);
                            break;
                        case STRING:
                            TextBoxItem tb = new TextBoxItem(attr.getName(), attr.getName().toUpperCase());
                            items.add(tb);
                            break;
                        default:
                            throw new RuntimeException("Unsupported ModelType "+type);
                    }
                }
            }

            form.setFields(items.toArray(new FormItem[]{}));
        }

        @Override
        public void add(final ReificationWidget widget, final InteractionUnit interactionUnit,
                        final InteractionUnit parent)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public Widget asWidget()
        {

            VerticalPanel layout = new VerticalPanel();
            layout.setStyleName("fill-layout-width");
            layout.getElement().setAttribute("style", "margin-top:15px;");

            FormToolStrip<ModelNode> tools = new FormToolStrip<ModelNode>(
                    form,
                    new FormToolStrip.FormCallback<ModelNode>() {
                        @Override
                        public void onSave(Map<String, Object> changeset) {
                            // TODO: what's happening here ?
                        }

                        @Override
                        public void onDelete(ModelNode entity) {
                            // unsupported
                        }
                    });

            FormHelpPanel help = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
                @Override
                public ModelNode getAddress() {
                    return new ModelNode();// TODO: how to get to the address ?
                }
            }, form);

            layout.add(tools.asWidget());
            layout.add(help.asWidget());
            layout.add(form.asWidget());

            return layout;
        }
    }


    static final String base64 = "bwAAAAMAB291dGNvbWVzAAdzdWNjZXNzAAZyZXN1bHRvAAAAAgAGc3RlcC0xbwAAAAIAB291dGNv\n" +
            "bWVzAAdzdWNjZXNzAAZyZXN1bHRvAAAABAALZGVzY3JpcHRpb25zAC9UaGUgY29uZmlndXJhdGlv\n" +
            "biBvZiB0aGUgdHJhbnNhY3Rpb24gc3Vic3lzdGVtLgAKYXR0cmlidXRlc28AAAAQABlwcm9jZXNz\n" +
            "LWlkLXNvY2tldC1iaW5kaW5nbwAAAAoABHR5cGV0cwALZGVzY3JpcHRpb25zAL9UaGUgbmFtZSBv\n" +
            "ZiB0aGUgc29ja2V0IGJpbmRpbmcgY29uZmlndXJhdGlvbiB0byB1c2UgaWYgdGhlIHRyYW5zYWN0\n" +
            "aW9uIG1hbmFnZXIgc2hvdWxkIHVzZSBhIHNvY2tldC1iYXNlZCBwcm9jZXNzIGlkLiBXaWxsIGJl\n" +
            "ICd1bmRlZmluZWQnIGlmICdwcm9jZXNzLWlkLXV1aWQnIGlzICd0cnVlJzsgb3RoZXJ3aXNlIG11\n" +
            "c3QgYmUgc2V0LgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhuaWxsYWJsZVoAAAxhbHRlcm5hdGl2\n" +
            "ZXNsAAAAAXMAD3Byb2Nlc3MtaWQtdXVpZAAKbWluLWxlbmd0aEoAAAAAAAAAAQAKbWF4LWxlbmd0\n" +
            "aEoAAAAAf////wALYWNjZXNzLXR5cGVzAApyZWFkLXdyaXRlAAdzdG9yYWdlcwANY29uZmlndXJh\n" +
            "dGlvbgAQcmVzdGFydC1yZXF1aXJlZHMADGFsbC1zZXJ2aWNlcwAPZGVmYXVsdC10aW1lb3V0bwAA\n" +
            "AAkABHR5cGV0SQALZGVzY3JpcHRpb25zABRUaGUgZGVmYXVsdCB0aW1lb3V0LgATZXhwcmVzc2lv\n" +
            "bnMtYWxsb3dlZFoBAAhuaWxsYWJsZVoBAAdkZWZhdWx0SQAAASwABHVuaXRzAAdTRUNPTkRTAAth\n" +
            "Y2Nlc3MtdHlwZXMACnJlYWQtd3JpdGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0\n" +
            "LXJlcXVpcmVkcwAMYWxsLXNlcnZpY2VzABtwcm9jZXNzLWlkLXNvY2tldC1tYXgtcG9ydHNvAAAA\n" +
            "CwAEdHlwZXRJAAtkZXNjcmlwdGlvbnMBkVRoZSBtYXhpbXVtIG51bWJlciBvZiBwb3J0cyB0byBz\n" +
            "ZWFyY2ggZm9yIGFuIG9wZW4gcG9ydCBpZiB0aGUgdHJhbnNhY3Rpb24gbWFuYWdlciBzaG91bGQg\n" +
            "dXNlIGEgc29ja2V0LWJhc2VkIHByb2Nlc3MgaWQuIElmIHRoZSBwb3J0IHNwZWNpZmllZCBieSB0\n" +
            "aGUgc29ja2V0IGJpbmRpbmcgcmVmZXJlbmNlZCBpbiAncHJvY2Vzcy1pZC1zb2NrZXQtYmluZGlu\n" +
            "ZycgaXMgb2NjdXBpZWQsIHRoZSBuZXh0IGhpZ2hlciBwb3J0IHdpbGwgYmUgdHJpZWQgdW50aWwg\n" +
            "YW4gb3BlbiBwb3J0IGlzIGZvdW5kIG9yIHRoZSBudW1iZXIgb2YgcG9ydHMgc3BlY2lmaWVkIGJ5\n" +
            "IHRoaXMgYXR0cmlidXRlIGhhdmUgYmVlbiB0cmllZC4gV2lsbCBiZSAndW5kZWZpbmVkJyBpZiAn\n" +
            "cHJvY2Vzcy1pZC11dWlkJyBpcyAndHJ1ZScuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACG5pbGxh\n" +
            "YmxlWgEAB2RlZmF1bHRJAAAACgAIcmVxdWlyZXNsAAAAAXMAGXByb2Nlc3MtaWQtc29ja2V0LWJp\n" +
            "bmRpbmcAA21pbkoAAAAAAAAAAQADbWF4SgAAAAB/////AAthY2Nlc3MtdHlwZXMACnJlYWQtd3Jp\n" +
            "dGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVkcwAMYWxsLXNlcnZp\n" +
            "Y2VzAAtyZWxhdGl2ZS10b28AAAAKAAR0eXBldHMAC2Rlc2NyaXB0aW9ucwFaUmVmZXJlbmNlcyBh\n" +
            "IGdsb2JhbCBwYXRoIGNvbmZpZ3VyYXRpb24gaW4gdGhlIGRvbWFpbiBtb2RlbCwgZGVmYXVsdGlu\n" +
            "ZyB0byB0aGUgSkJvc3MgQXBwbGljYXRpb24gU2VydmVyIGRhdGEgZGlyZWN0b3J5IChqYm9zcy5z\n" +
            "ZXJ2ZXIuZGF0YS5kaXIpLiBUaGUgdmFsdWUgb2YgdGhlICJwYXRoIiBhdHRyaWJ1dGUgd2lsbCB0\n" +
            "cmVhdGVkIGFzIHJlbGF0aXZlIHRvIHRoaXMgcGF0aC4gVXNlIGFuIGVtcHR5IHN0cmluZyB0byBk\n" +
            "aXNhYmxlIHRoZSBkZWZhdWx0IGJlaGF2aW9yIGFuZCBmb3JjZSB0aGUgdmFsdWUgb2YgdGhlICJw\n" +
            "YXRoIiBhdHRyaWJ1dGUgdG8gYmUgdHJlYXRlZCBhcyBhbiBhYnNvbHV0ZSBwYXRoLgATZXhwcmVz\n" +
            "c2lvbnMtYWxsb3dlZFoBAAhuaWxsYWJsZVoBAAdkZWZhdWx0cwAVamJvc3Muc2VydmVyLmRhdGEu\n" +
            "ZGlyAAptaW4tbGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AAthY2Nlc3MtdHlw\n" +
            "ZXMACnJlYWQtd3JpdGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVk\n" +
            "cwAMYWxsLXNlcnZpY2VzAA9wcm9jZXNzLWlkLXV1aWRvAAAACAAEdHlwZXRaAAtkZXNjcmlwdGlv\n" +
            "bnMATUluZGljYXRlcyB3aGV0aGVyIHRoZSB0cmFuc2FjdGlvbiBtYW5hZ2VyIHNob3VsZCB1c2Ug\n" +
            "YSBVVUlEIGJhc2VkIHByb2Nlc3MgaWQuABNleHByZXNzaW9ucy1hbGxvd2VkWgAACG5pbGxhYmxl\n" +
            "WgAADGFsdGVybmF0aXZlc2wAAAABcwAZcHJvY2Vzcy1pZC1zb2NrZXQtYmluZGluZwALYWNjZXNz\n" +
            "LXR5cGVzAApyZWFkLXdyaXRlAAdzdG9yYWdlcwANY29uZmlndXJhdGlvbgAQcmVzdGFydC1yZXF1\n" +
            "aXJlZHMADGFsbC1zZXJ2aWNlcwARdXNlLWhvcm5ldHEtc3RvcmVvAAAACAAEdHlwZXRaAAtkZXNj\n" +
            "cmlwdGlvbnMBDVVzZSB0aGUgSG9ybmV0USBqb3VybmFsIHN0b3JlIGZvciB3cml0aW5nIHRyYW5z\n" +
            "YWN0aW9uIGxvZ3MuIFNldCB0byB0cnVlIHRvIGVuYWJsZSBhbmQgdG8gZmFsc2UgdG8gdXNlIHRo\n" +
            "ZSBkZWZhdWx0IGxvZyBzdG9yZSB0eXBlLiBUaGUgZGVmYXVsdCBsb2cgc3RvcmUgaXMgbm9ybWFs\n" +
            "bHkgb25lIGZpbGUgc3lzdGVtIGZpbGUgcGVyIHRyYW5zYWN0aW9uIGxvZy4gVGhlIHNlcnZlciBz\n" +
            "aG91bGQgYmUgcmVzdGFydGVkIGZvciB0aGlzIHNldHRpbmcgdG8gdGFrZSBlZmZlY3QuABNleHBy\n" +
            "ZXNzaW9ucy1hbGxvd2VkWgEACG5pbGxhYmxlWgEAB2RlZmF1bHRaAAALYWNjZXNzLXR5cGVzAApy\n" +
            "ZWFkLXdyaXRlAAdzdG9yYWdlcwANY29uZmlndXJhdGlvbgAQcmVzdGFydC1yZXF1aXJlZHMAA2p2\n" +
            "bQAOc29ja2V0LWJpbmRpbmdvAAAACQAEdHlwZXRzAAtkZXNjcmlwdGlvbnMAUVVzZWQgdG8gcmVm\n" +
            "ZXJlbmNlIHRoZSBjb3JyZWN0IHNvY2tldCBiaW5kaW5nIHRvIHVzZSBmb3IgdGhlIHJlY292ZXJ5\n" +
            "IGVudmlyb25tZW50LgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhuaWxsYWJsZVoAAAptaW4tbGVu\n" +
            "Z3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AAthY2Nlc3MtdHlwZXMACnJlYWQtd3Jp\n" +
            "dGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVkcwAMYWxsLXNlcnZp\n" +
            "Y2VzAANqdHNvAAAACAAEdHlwZXRaAAtkZXNjcmlwdGlvbnMAMUlmIHRydWUgdGhpcyBlbmFibGVz\n" +
            "IHRoZSBKYXZhIFRyYW5zYWN0aW9uIFNlcnZpY2UAE2V4cHJlc3Npb25zLWFsbG93ZWRaAQAIbmls\n" +
            "bGFibGVaAQAHZGVmYXVsdFoAAAthY2Nlc3MtdHlwZXMACnJlYWQtd3JpdGUAB3N0b3JhZ2VzAA1j\n" +
            "b25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVkcwADanZtABFyZWNvdmVyeS1saXN0ZW5lcm8A\n" +
            "AAAIAAR0eXBldFoAC2Rlc2NyaXB0aW9ucwBQVXNlZCB0byBzcGVjaWZ5IGlmIHRoZSByZWNvdmVy\n" +
            "eSBzeXN0ZW0gc2hvdWxkIGxpc3RlbiBvbiBhIG5ldHdvcmsgc29ja2V0IG9yIG5vdC4AE2V4cHJl\n" +
            "c3Npb25zLWFsbG93ZWRaAQAIbmlsbGFibGVaAQAHZGVmYXVsdFoAAAthY2Nlc3MtdHlwZXMACnJl\n" +
            "YWQtd3JpdGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVkcwAMYWxs\n" +
            "LXNlcnZpY2VzABVzdGF0dXMtc29ja2V0LWJpbmRpbmdvAAAACQAEdHlwZXRzAAtkZXNjcmlwdGlv\n" +
            "bnMAV1VzZWQgdG8gcmVmZXJlbmNlIHRoZSBjb3JyZWN0IHNvY2tldCBiaW5kaW5nIHRvIHVzZSBm\n" +
            "b3IgdGhlIHRyYW5zYWN0aW9uIHN0YXR1cyBtYW5hZ2VyLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoB\n" +
            "AAhuaWxsYWJsZVoAAAptaW4tbGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AAth\n" +
            "Y2Nlc3MtdHlwZXMACnJlYWQtd3JpdGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0\n" +
            "LXJlcXVpcmVkcwAMYWxsLXNlcnZpY2VzAA9ub2RlLWlkZW50aWZpZXJvAAAACgAEdHlwZXRzAAtk\n" +
            "ZXNjcmlwdGlvbnMAOFVzZWQgdG8gc2V0IHRoZSBub2RlIGlkZW50aWZpZXIgb24gdGhlIGNvcmUg\n" +
            "ZW52aXJvbm1lbnQuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACG5pbGxhYmxlWgEAB2RlZmF1bHRz\n" +
            "AAExAAptaW4tbGVuZ3RoSgAAAAAAAAAAAAptYXgtbGVuZ3RoSgAAAAAAAAAXAAthY2Nlc3MtdHlw\n" +
            "ZXMACnJlYWQtd3JpdGUAB3N0b3JhZ2VzAA1jb25maWd1cmF0aW9uABByZXN0YXJ0LXJlcXVpcmVk\n" +
            "cwAMYWxsLXNlcnZpY2VzABFvYmplY3Qtc3RvcmUtcGF0aG8AAAAKAAR0eXBldHMAC2Rlc2NyaXB0\n" +
            "aW9ucwDVRGVub3RlcyBhIHJlbGF0aXZlIG9yIGFic29sdXRlIGZpbGVzeXN0ZW0gcGF0aCBkZW5v\n" +
            "dGluZyB3aGVyZSB0aGUgdHJhbnNhY3Rpb24gbWFuYWdlciBvYmplY3Qgc3RvcmUgc2hvdWxkIHN0\n" +
            "b3JlIGRhdGEuIEJ5IGRlZmF1bHQgdGhlIHZhbHVlIGlzIHRyZWF0ZWQgYXMgcmVsYXRpdmUgdG8g\n" +
            "dGhlIHBhdGggZGVub3RlZCBieSB0aGUgInJlbGF0aXZlLXRvIiBhdHRyaWJ1dGUuABNleHByZXNz\n" +
            "aW9ucy1hbGxvd2VkWgEACG5pbGxhYmxlWgEAB2RlZmF1bHRzAA90eC1vYmplY3Qtc3RvcmUACm1p\n" +
            "bi1sZW5ndGhKAAAAAAAAAAEACm1heC1sZW5ndGhKAAAAAH////8AC2FjY2Vzcy10eXBlcwAKcmVh\n" +
            "ZC13cml0ZQAHc3RvcmFnZXMADWNvbmZpZ3VyYXRpb24AEHJlc3RhcnQtcmVxdWlyZWRzAAxhbGwt\n" +
            "c2VydmljZXMABHBhdGhvAAAACgAEdHlwZXRzAAtkZXNjcmlwdGlvbnMAzURlbm90ZXMgYSByZWxh\n" +
            "dGl2ZSBvciBhYnNvbHV0ZSBmaWxlc3lzdGVtIHBhdGggZGVub3Rpbmcgd2hlcmUgdGhlIHRyYW5z\n" +
            "YWN0aW9uIG1hbmFnZXIgY29yZSBzaG91bGQgc3RvcmUgZGF0YS4gQnkgZGVmYXVsdCB0aGUgdmFs\n" +
            "dWUgaXMgdHJlYXRlZCBhcyByZWxhdGl2ZSB0byB0aGUgcGF0aCBkZW5vdGVkIGJ5IHRoZSAicmVs\n" +
            "YXRpdmUtdG8iIGF0dHJpYnV0ZS4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAQAIbmlsbGFibGVaAQAH\n" +
            "ZGVmYXVsdHMAA3ZhcgAKbWluLWxlbmd0aEoAAAAAAAAAAQAKbWF4LWxlbmd0aEoAAAAAf////wAL\n" +
            "YWNjZXNzLXR5cGVzAApyZWFkLXdyaXRlAAdzdG9yYWdlcwANY29uZmlndXJhdGlvbgAQcmVzdGFy\n" +
            "dC1yZXF1aXJlZHMADGFsbC1zZXJ2aWNlcwARZW5hYmxlLXRzbS1zdGF0dXNvAAAACAAEdHlwZXRa\n" +
            "AAtkZXNjcmlwdGlvbnMAdVdoZXRoZXIgdGhlIHRyYW5zYWN0aW9uIHN0YXR1cyBtYW5hZ2VyIChU\n" +
            "U00pIHNlcnZpY2UsIG5lZWRlZCBmb3Igb3V0IG9mIHByb2Nlc3MgcmVjb3ZlcnksIHNob3VsZCBi\n" +
            "ZSBwcm92aWRlZCBvciBub3QuLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhuaWxsYWJsZVoBAAdk\n" +
            "ZWZhdWx0WgAAC2FjY2Vzcy10eXBlcwAKcmVhZC13cml0ZQAHc3RvcmFnZXMADWNvbmZpZ3VyYXRp\n" +
            "b24AEHJlc3RhcnQtcmVxdWlyZWRzAAxhbGwtc2VydmljZXMAEWVuYWJsZS1zdGF0aXN0aWNzbwAA\n" +
            "AAgABHR5cGV0WgALZGVzY3JpcHRpb25zACVXaGV0aGVyIHN0YXRpc3RpY3Mgc2hvdWxkIGJlIGVu\n" +
            "YWJsZWQuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACG5pbGxhYmxlWgEAB2RlZmF1bHRaAAALYWNj\n" +
            "ZXNzLXR5cGVzAApyZWFkLXdyaXRlAAdzdG9yYWdlcwANY29uZmlndXJhdGlvbgAQcmVzdGFydC1y\n" +
            "ZXF1aXJlZHMADGFsbC1zZXJ2aWNlcwAYb2JqZWN0LXN0b3JlLXJlbGF0aXZlLXRvbwAAAAoABHR5\n" +
            "cGV0cwALZGVzY3JpcHRpb25zAVpSZWZlcmVuY2VzIGEgZ2xvYmFsIHBhdGggY29uZmlndXJhdGlv\n" +
            "biBpbiB0aGUgZG9tYWluIG1vZGVsLCBkZWZhdWx0aW5nIHRvIHRoZSBKQm9zcyBBcHBsaWNhdGlv\n" +
            "biBTZXJ2ZXIgZGF0YSBkaXJlY3RvcnkgKGpib3NzLnNlcnZlci5kYXRhLmRpcikuIFRoZSB2YWx1\n" +
            "ZSBvZiB0aGUgInBhdGgiIGF0dHJpYnV0ZSB3aWxsIHRyZWF0ZWQgYXMgcmVsYXRpdmUgdG8gdGhp\n" +
            "cyBwYXRoLiBVc2UgYW4gZW1wdHkgc3RyaW5nIHRvIGRpc2FibGUgdGhlIGRlZmF1bHQgYmVoYXZp\n" +
            "b3IgYW5kIGZvcmNlIHRoZSB2YWx1ZSBvZiB0aGUgInBhdGgiIGF0dHJpYnV0ZSB0byBiZSB0cmVh\n" +
            "dGVkIGFzIGFuIGFic29sdXRlIHBhdGguABNleHByZXNzaW9ucy1hbGxvd2VkWgEACG5pbGxhYmxl\n" +
            "WgEAB2RlZmF1bHRzABVqYm9zcy5zZXJ2ZXIuZGF0YS5kaXIACm1pbi1sZW5ndGhKAAAAAAAAAAEA\n" +
            "Cm1heC1sZW5ndGhKAAAAAH////8AC2FjY2Vzcy10eXBlcwAKcmVhZC13cml0ZQAHc3RvcmFnZXMA\n" +
            "DWNvbmZpZ3VyYXRpb24AEHJlc3RhcnQtcmVxdWlyZWRzAAxhbGwtc2VydmljZXMACm9wZXJhdGlv\n" +
            "bnNvAAAADQATcmVhZC1jaGlsZHJlbi1uYW1lc28AAAAEAA5vcGVyYXRpb24tbmFtZXMAE3JlYWQt\n" +
            "Y2hpbGRyZW4tbmFtZXMAC2Rlc2NyaXB0aW9ucwBOR2V0cyB0aGUgbmFtZXMgb2YgYWxsIGNoaWxk\n" +
            "cmVuIHVuZGVyIHRoZSBzZWxlY3RlZCByZXNvdXJjZSB3aXRoIHRoZSBnaXZlbiB0eXBlABJyZXF1\n" +
            "ZXN0LXByb3BlcnRpZXNvAAAAAQAKY2hpbGQtdHlwZW8AAAAHAAR0eXBldHMAC2Rlc2NyaXB0aW9u\n" +
            "cwA6VGhlIG5hbWUgb2YgdGhlIG5vZGUgdW5kZXIgd2hpY2ggdG8gZ2V0IHRoZSBjaGlsZHJlbiBu\n" +
            "YW1lcwATZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoBAAhuaWxsYWJsZVoAAAptaW4t\n" +
            "bGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////ABByZXBseS1wcm9wZXJ0aWVzbwAA\n" +
            "AAMABHR5cGV0bAAKdmFsdWUtdHlwZXRzAAtkZXNjcmlwdGlvbnMAElRoZSBjaGlsZHJlbiBuYW1l\n" +
            "cwAacmVhZC1vcGVyYXRpb24tZGVzY3JpcHRpb25vAAAABAAOb3BlcmF0aW9uLW5hbWVzABpyZWFk\n" +
            "LW9wZXJhdGlvbi1kZXNjcmlwdGlvbgALZGVzY3JpcHRpb25zACNHZXRzIGRlc2NyaXB0aW9uIG9m\n" +
            "IGdpdmVuIG9wZXJhdGlvbgAScmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAIABG5hbWVvAAAABwAEdHlw\n" +
            "ZXRzAAtkZXNjcmlwdGlvbnMAEU5hbWUgb2Ygb3BlcmF0aW9uABNleHByZXNzaW9ucy1hbGxvd2Vk\n" +
            "WgAACHJlcXVpcmVkWgEACG5pbGxhYmxlWgAACm1pbi1sZW5ndGhKAAAAAAAAAAEACm1heC1sZW5n\n" +
            "dGhKAAAAAH////8ABmxvY2FsZW8AAAAHAAR0eXBldHMAC2Rlc2NyaXB0aW9ucwAlTG9jYWxlIGlu\n" +
            "IHdoaWNoIHRvIHJldHVybiBkZXNjcmlwdGlvbgATZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1\n" +
            "aXJlZFoAAAhuaWxsYWJsZVoBAAptaW4tbGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/\n" +
            "////ABByZXBseS1wcm9wZXJ0aWVzbwAAAAEABHR5cGV0bwAGcmVtb3ZlbwAAAAQADm9wZXJhdGlv\n" +
            "bi1uYW1lcwAGcmVtb3ZlAAtkZXNjcmlwdGlvbnMAIVJlbW92ZXMgdGhlIHRyYW5zYWN0aW9uIHN1\n" +
            "YnN5c3RlbQAScmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAAAEHJlcGx5LXByb3BlcnRpZXNvAAAAAAAZ\n" +
            "cmVhZC1yZXNvdXJjZS1kZXNjcmlwdGlvbm8AAAAEAA5vcGVyYXRpb24tbmFtZXMAGXJlYWQtcmVz\n" +
            "b3VyY2UtZGVzY3JpcHRpb24AC2Rlc2NyaXB0aW9ucwBeR2V0cyB0aGUgZGVzY3JpcHRpb24gb2Yg\n" +
            "YSByZXNvdXJjZSdzIGF0dHJpYnV0ZXMsIHR5cGVzIG9mIGNoaWxkcmVuIGFuZCwgb3B0aW9uYWxs\n" +
            "eSwgb3BlcmF0aW9ucwAScmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAcACm9wZXJhdGlvbnNvAAAABgAE\n" +
            "dHlwZXRaAAtkZXNjcmlwdGlvbnMATldoZXRoZXIgdG8gaW5jbHVkZSBkZXNjcmlwdGlvbnMgb2Yg\n" +
            "dGhlIHJlc291cmNlJ3Mgb3BlcmF0aW9ucy4gRGVmYXVsdCBpcyBmYWxzZQATZXhwcmVzc2lvbnMt\n" +
            "YWxsb3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgAACWluaGVyaXRlZG8A\n" +
            "AAAGAAR0eXBldFoAC2Rlc2NyaXB0aW9ucwBxSWYgJ29wZXJhdGlvbnMnIGlzIHRydWUsIHdoZXRo\n" +
            "ZXIgdG8gaW5jbHVkZSBkZXNjcmlwdGlvbnMgb2YgdGhlIHJlc291cmNlJ3MgaW5oZXJpdGVkIG9w\n" +
            "ZXJhdGlvbnMuIERlZmF1bHQgaXMgdHJ1ZS4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWly\n" +
            "ZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoBAAlyZWN1cnNpdmVvAAAABgAEdHlwZXRaAAtkZXNj\n" +
            "cmlwdGlvbnMAUVdoZXRoZXIgdG8gaW5jbHVkZSByZWN1cnNpdmVseSBkZXNjcmlwdGlvbnMgb2Yg\n" +
            "Y2hpbGQgcmVzb3VyY2VzLiBEZWZhdWx0IGlzIGZhbHNlLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoA\n" +
            "AAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgAAD3JlY3Vyc2l2ZS1kZXB0aG8AAAAG\n" +
            "AAR0eXBldEkAC2Rlc2NyaXB0aW9ucwBIVGhlIGRlcHRoIHRvIHdoaWNoIGluZm9ybWF0aW9uIGFi\n" +
            "b3V0IGNoaWxkIHJlc291cmNlcyBzaG91bGQgYmUgaW5jbHVkZWQuABNleHByZXNzaW9ucy1hbGxv\n" +
            "d2VkWgAACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2RlZmF1bHRJAAAAAAAHcHJveGllc28AAAAG\n" +
            "AAR0eXBldFoAC2Rlc2NyaXB0aW9ucwDCV2hldGhlciB0byBpbmNsdWRlIHJlbW90ZSByZXNvdXJj\n" +
            "ZXMgaW4gYSByZWN1cnNpdmUgcXVlcnkgKGkuZS4gaG9zdCBsZXZlbCByZXNvdXJjZXMgaW4gYSBx\n" +
            "dWVyeSBvZiB0aGUgZG9tYWluIHJvb3Q7IHJ1bm5pbmcgc2VydmVyIHJlc291cmNlcyBpbiBhIHF1\n" +
            "ZXJ5IG9mIGEgaG9zdCkuIElmIGFic2VudCwgZmFsc2UgaXMgdGhlIGRlZmF1bHQAE2V4cHJlc3Np\n" +
            "b25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoAAA9pbmNsdWRl\n" +
            "LWFsaWFzZXNvAAAABgAEdHlwZXRaAAtkZXNjcmlwdGlvbnMAPElmICd0cnVlJyBhbmQgcmVjdXJz\n" +
            "aXZlLCBpbmNsdWRlIGNoaWxkcmVuIHdoaWNoIGFyZSBhbGlhc2VzLgATZXhwcmVzc2lvbnMtYWxs\n" +
            "b3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgAABmxvY2FsZW8AAAAHAAR0\n" +
            "eXBldHMAC2Rlc2NyaXB0aW9ucwBXVGhlIGxvY2FsZSB0byBnZXQgdGhlIHJlc291cmNlIGRlc2Ny\n" +
            "aXB0aW9uIGluLiBJZiBudWxsLCB0aGUgZGVmYXVsdCBsb2NhbGUgd2lsbCBiZSB1c2VkABNleHBy\n" +
            "ZXNzaW9ucy1hbGxvd2VkWgAACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEACm1pbi1sZW5ndGhKAAAA\n" +
            "AAAAAAEACm1heC1sZW5ndGhKAAAAAH////8AEHJlcGx5LXByb3BlcnRpZXNvAAAAAgAEdHlwZXRv\n" +
            "AAtkZXNjcmlwdGlvbnMAH1RoZSBkZXNjcmlwdGlvbiBvZiB0aGUgcmVzb3VyY2UADXJlYWQtcmVz\n" +
            "b3VyY2VvAAAABAAOb3BlcmF0aW9uLW5hbWVzAA1yZWFkLXJlc291cmNlAAtkZXNjcmlwdGlvbnMA\n" +
            "c1JlYWRzIGEgbW9kZWwgcmVzb3VyY2UncyBhdHRyaWJ1dGUgdmFsdWVzIGFsb25nIHdpdGggZWl0\n" +
            "aGVyIGJhc2ljIG9yIGNvbXBsZXRlIGluZm9ybWF0aW9uIGFib3V0IGFueSBjaGlsZCByZXNvdXJj\n" +
            "ZXMAEnJlcXVlc3QtcHJvcGVydGllc28AAAAHAAlyZWN1cnNpdmVvAAAABgAEdHlwZXRaAAtkZXNj\n" +
            "cmlwdGlvbnMAa1doZXRoZXIgdG8gaW5jbHVkZSBjb21wbGV0ZSBpbmZvcm1hdGlvbiBhYm91dCBj\n" +
            "aGlsZCByZXNvdXJjZXMsIHJlY3Vyc2l2ZWx5LiBJZiBhYnNlbnQsIGZhbHNlIGlzIHRoZSBkZWZh\n" +
            "dWx0ABNleHByZXNzaW9ucy1hbGxvd2VkWgAACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2RlZmF1\n" +
            "bHRaAAAPcmVjdXJzaXZlLWRlcHRobwAAAAYABHR5cGV0SQALZGVzY3JpcHRpb25zAEhUaGUgZGVw\n" +
            "dGggdG8gd2hpY2ggaW5mb3JtYXRpb24gYWJvdXQgY2hpbGQgcmVzb3VyY2VzIHNob3VsZCBiZSBp\n" +
            "bmNsdWRlZC4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAAAIbmlsbGFibGVaAQAH\n" +
            "ZGVmYXVsdEkAAAAAAAdwcm94aWVzbwAAAAYABHR5cGV0WgALZGVzY3JpcHRpb25zAMNXaGV0aGVy\n" +
            "IHRvIGluY2x1ZGUgcmVtb3RlIHJlc291cmNlcyBpbiBhIHJlY3Vyc2l2ZSBxdWVyeSAoaS5lLiBo\n" +
            "b3N0IGxldmVsIHJlc291cmNlcyBpbiBhIHF1ZXJ5IG9mIHRoZSBkb21haW4gcm9vdDsgcnVubmlu\n" +
            "ZyBzZXJ2ZXIgcmVzb3VyY2VzIGluIGEgcXVlcnkgb2YgYSBob3N0KS4gSWYgYWJzZW50LCBmYWxz\n" +
            "ZSBpcyB0aGUgZGVmYXVsdC4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAAAIbmls\n" +
            "bGFibGVaAQAHZGVmYXVsdFoAAA9pbmNsdWRlLXJ1bnRpbWVvAAAABgAEdHlwZXRaAAtkZXNjcmlw\n" +
            "dGlvbnMAoFdoZXRoZXIgdG8gaW5jbHVkZSBydW50aW1lIGF0dHJpYnV0ZXMgKGkuZS4gdGhvc2Ug\n" +
            "d2hvc2UgdmFsdWUgZG9lcyBub3QgY29tZSBmcm9tIHRoZSBwZXJzaXN0ZW50IGNvbmZpZ3VyYXRp\n" +
            "b24pIGluIHRoZSByZXNwb25zZS4gSWYgYWJzZW50LCBmYWxzZSBpcyB0aGUgZGVmYXVsdC4AE2V4\n" +
            "cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoAABBp\n" +
            "bmNsdWRlLWRlZmF1bHRzbwAAAAYABHR5cGV0WgALZGVzY3JpcHRpb25zAIFCb29sZWFuIHRvIGVu\n" +
            "YWJsZS9kaXNhYmxlIGRlZmF1bHQgcmVhZGluZy4gSW4gY2FzZSBpdCBpcyBzZXQgdG8gZmFsc2Ug\n" +
            "b25seSBhdHRyaWJ1dGUgc2V0IGJ5IHVzZXIgYXJlIHJldHVybmVkIGlnbm9yaW5nIHVuZGVmaW5l\n" +
            "ZC4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVs\n" +
            "dFoBAA9hdHRyaWJ1dGVzLW9ubHlvAAAABgAEdHlwZXRaAAtkZXNjcmlwdGlvbnMAildoZXRoZXIg\n" +
            "b3Igbm90IHRvIG9ubHkgcmVhZCB0aGUgYXR0cmlidXRlcyBvbiB0aGUgc3BlY2lmaWVkIHJlc291\n" +
            "cmNlLiBDYW5ub3QgYmUgdXNlZCBpbiBjb25qdW5jdGlvbiB3aXRoICdyZWN1cnNpdmUnIG9yICdy\n" +
            "ZWN1cnNpdmUtZGVwdGgnLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxs\n" +
            "YWJsZVoBAAdkZWZhdWx0WgAAD2luY2x1ZGUtYWxpYXNlc28AAAAGAAR0eXBldFoAC2Rlc2NyaXB0\n" +
            "aW9ucwA8SWYgJ3RydWUnIGFuZCByZWN1cnNpdmUsIGluY2x1ZGUgY2hpbGRyZW4gd2hpY2ggYXJl\n" +
            "IGFsaWFzZXMuABNleHByZXNzaW9ucy1hbGxvd2VkWgAACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEA\n" +
            "B2RlZmF1bHRaAAAQcmVwbHktcHJvcGVydGllc28AAAACAAR0eXBldG8AC2Rlc2NyaXB0aW9ucwBQ\n" +
            "VGhlIHJlc291cmNlJ3MgYXR0cmlidXRlIHZhbHVlcyBhbG9uZyB3aXRoIGluZm9ybWF0aW9uIGFi\n" +
            "b3V0IGFueSBjaGlsZCByZXNvdXJjZXMAA2FkZG8AAAAEAA5vcGVyYXRpb24tbmFtZXMAA2FkZAAL\n" +
            "ZGVzY3JpcHRpb25zAB5BZGRzIHRoZSB0cmFuc2FjdGlvbiBzdWJzeXN0ZW0AEnJlcXVlc3QtcHJv\n" +
            "cGVydGllc28AAAAQABlwcm9jZXNzLWlkLXNvY2tldC1iaW5kaW5nbwAAAAgABHR5cGV0cwALZGVz\n" +
            "Y3JpcHRpb25zAL9UaGUgbmFtZSBvZiB0aGUgc29ja2V0IGJpbmRpbmcgY29uZmlndXJhdGlvbiB0\n" +
            "byB1c2UgaWYgdGhlIHRyYW5zYWN0aW9uIG1hbmFnZXIgc2hvdWxkIHVzZSBhIHNvY2tldC1iYXNl\n" +
            "ZCBwcm9jZXNzIGlkLiBXaWxsIGJlICd1bmRlZmluZWQnIGlmICdwcm9jZXNzLWlkLXV1aWQnIGlz\n" +
            "ICd0cnVlJzsgb3RoZXJ3aXNlIG11c3QgYmUgc2V0LgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhy\n" +
            "ZXF1aXJlZFoBAAhuaWxsYWJsZVoAAAxhbHRlcm5hdGl2ZXNsAAAAAXMAD3Byb2Nlc3MtaWQtdXVp\n" +
            "ZAAKbWluLWxlbmd0aEoAAAAAAAAAAQAKbWF4LWxlbmd0aEoAAAAAf////wAPZGVmYXVsdC10aW1l\n" +
            "b3V0bwAAAAcABHR5cGV0SQALZGVzY3JpcHRpb25zABRUaGUgZGVmYXVsdCB0aW1lb3V0LgATZXhw\n" +
            "cmVzc2lvbnMtYWxsb3dlZFoBAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0SQAAASwA\n" +
            "BHVuaXRzAAdTRUNPTkRTABtwcm9jZXNzLWlkLXNvY2tldC1tYXgtcG9ydHNvAAAACQAEdHlwZXRJ\n" +
            "AAtkZXNjcmlwdGlvbnMBkVRoZSBtYXhpbXVtIG51bWJlciBvZiBwb3J0cyB0byBzZWFyY2ggZm9y\n" +
            "IGFuIG9wZW4gcG9ydCBpZiB0aGUgdHJhbnNhY3Rpb24gbWFuYWdlciBzaG91bGQgdXNlIGEgc29j\n" +
            "a2V0LWJhc2VkIHByb2Nlc3MgaWQuIElmIHRoZSBwb3J0IHNwZWNpZmllZCBieSB0aGUgc29ja2V0\n" +
            "IGJpbmRpbmcgcmVmZXJlbmNlZCBpbiAncHJvY2Vzcy1pZC1zb2NrZXQtYmluZGluZycgaXMgb2Nj\n" +
            "dXBpZWQsIHRoZSBuZXh0IGhpZ2hlciBwb3J0IHdpbGwgYmUgdHJpZWQgdW50aWwgYW4gb3BlbiBw\n" +
            "b3J0IGlzIGZvdW5kIG9yIHRoZSBudW1iZXIgb2YgcG9ydHMgc3BlY2lmaWVkIGJ5IHRoaXMgYXR0\n" +
            "cmlidXRlIGhhdmUgYmVlbiB0cmllZC4gV2lsbCBiZSAndW5kZWZpbmVkJyBpZiAncHJvY2Vzcy1p\n" +
            "ZC11dWlkJyBpcyAndHJ1ZScuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACHJlcXVpcmVkWgAACG5p\n" +
            "bGxhYmxlWgEAB2RlZmF1bHRJAAAACgAIcmVxdWlyZXNsAAAAAXMAGXByb2Nlc3MtaWQtc29ja2V0\n" +
            "LWJpbmRpbmcAA21pbkoAAAAAAAAAAQADbWF4SgAAAAB/////AAtyZWxhdGl2ZS10b28AAAAIAAR0\n" +
            "eXBldHMAC2Rlc2NyaXB0aW9ucwFaUmVmZXJlbmNlcyBhIGdsb2JhbCBwYXRoIGNvbmZpZ3VyYXRp\n" +
            "b24gaW4gdGhlIGRvbWFpbiBtb2RlbCwgZGVmYXVsdGluZyB0byB0aGUgSkJvc3MgQXBwbGljYXRp\n" +
            "b24gU2VydmVyIGRhdGEgZGlyZWN0b3J5IChqYm9zcy5zZXJ2ZXIuZGF0YS5kaXIpLiBUaGUgdmFs\n" +
            "dWUgb2YgdGhlICJwYXRoIiBhdHRyaWJ1dGUgd2lsbCB0cmVhdGVkIGFzIHJlbGF0aXZlIHRvIHRo\n" +
            "aXMgcGF0aC4gVXNlIGFuIGVtcHR5IHN0cmluZyB0byBkaXNhYmxlIHRoZSBkZWZhdWx0IGJlaGF2\n" +
            "aW9yIGFuZCBmb3JjZSB0aGUgdmFsdWUgb2YgdGhlICJwYXRoIiBhdHRyaWJ1dGUgdG8gYmUgdHJl\n" +
            "YXRlZCBhcyBhbiBhYnNvbHV0ZSBwYXRoLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhyZXF1aXJl\n" +
            "ZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0cwAVamJvc3Muc2VydmVyLmRhdGEuZGlyAAptaW4tbGVu\n" +
            "Z3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AA9wcm9jZXNzLWlkLXV1aWRvAAAABgAE\n" +
            "dHlwZXRaAAtkZXNjcmlwdGlvbnMATUluZGljYXRlcyB3aGV0aGVyIHRoZSB0cmFuc2FjdGlvbiBt\n" +
            "YW5hZ2VyIHNob3VsZCB1c2UgYSBVVUlEIGJhc2VkIHByb2Nlc3MgaWQuABNleHByZXNzaW9ucy1h\n" +
            "bGxvd2VkWgAACHJlcXVpcmVkWgEACG5pbGxhYmxlWgAADGFsdGVybmF0aXZlc2wAAAABcwAZcHJv\n" +
            "Y2Vzcy1pZC1zb2NrZXQtYmluZGluZwARdXNlLWhvcm5ldHEtc3RvcmVvAAAABgAEdHlwZXRaAAtk\n" +
            "ZXNjcmlwdGlvbnMBDVVzZSB0aGUgSG9ybmV0USBqb3VybmFsIHN0b3JlIGZvciB3cml0aW5nIHRy\n" +
            "YW5zYWN0aW9uIGxvZ3MuIFNldCB0byB0cnVlIHRvIGVuYWJsZSBhbmQgdG8gZmFsc2UgdG8gdXNl\n" +
            "IHRoZSBkZWZhdWx0IGxvZyBzdG9yZSB0eXBlLiBUaGUgZGVmYXVsdCBsb2cgc3RvcmUgaXMgbm9y\n" +
            "bWFsbHkgb25lIGZpbGUgc3lzdGVtIGZpbGUgcGVyIHRyYW5zYWN0aW9uIGxvZy4gVGhlIHNlcnZl\n" +
            "ciBzaG91bGQgYmUgcmVzdGFydGVkIGZvciB0aGlzIHNldHRpbmcgdG8gdGFrZSBlZmZlY3QuABNl\n" +
            "eHByZXNzaW9ucy1hbGxvd2VkWgEACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2RlZmF1bHRaAAAO\n" +
            "c29ja2V0LWJpbmRpbmdvAAAABwAEdHlwZXRzAAtkZXNjcmlwdGlvbnMAUVVzZWQgdG8gcmVmZXJl\n" +
            "bmNlIHRoZSBjb3JyZWN0IHNvY2tldCBiaW5kaW5nIHRvIHVzZSBmb3IgdGhlIHJlY292ZXJ5IGVu\n" +
            "dmlyb25tZW50LgATZXhwcmVzc2lvbnMtYWxsb3dlZFoBAAhyZXF1aXJlZFoBAAhuaWxsYWJsZVoA\n" +
            "AAptaW4tbGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AANqdHNvAAAABgAEdHlw\n" +
            "ZXRaAAtkZXNjcmlwdGlvbnMAMUlmIHRydWUgdGhpcyBlbmFibGVzIHRoZSBKYXZhIFRyYW5zYWN0\n" +
            "aW9uIFNlcnZpY2UAE2V4cHJlc3Npb25zLWFsbG93ZWRaAQAIcmVxdWlyZWRaAAAIbmlsbGFibGVa\n" +
            "AQAHZGVmYXVsdFoAABFyZWNvdmVyeS1saXN0ZW5lcm8AAAAGAAR0eXBldFoAC2Rlc2NyaXB0aW9u\n" +
            "cwBQVXNlZCB0byBzcGVjaWZ5IGlmIHRoZSByZWNvdmVyeSBzeXN0ZW0gc2hvdWxkIGxpc3RlbiBv\n" +
            "biBhIG5ldHdvcmsgc29ja2V0IG9yIG5vdC4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAQAIcmVxdWly\n" +
            "ZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoAABVzdGF0dXMtc29ja2V0LWJpbmRpbmdvAAAABwAE\n" +
            "dHlwZXRzAAtkZXNjcmlwdGlvbnMAV1VzZWQgdG8gcmVmZXJlbmNlIHRoZSBjb3JyZWN0IHNvY2tl\n" +
            "dCBiaW5kaW5nIHRvIHVzZSBmb3IgdGhlIHRyYW5zYWN0aW9uIHN0YXR1cyBtYW5hZ2VyLgATZXhw\n" +
            "cmVzc2lvbnMtYWxsb3dlZFoBAAhyZXF1aXJlZFoBAAhuaWxsYWJsZVoAAAptaW4tbGVuZ3RoSgAA\n" +
            "AAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////AA9ub2RlLWlkZW50aWZpZXJvAAAACAAEdHlwZXRz\n" +
            "AAtkZXNjcmlwdGlvbnMAOFVzZWQgdG8gc2V0IHRoZSBub2RlIGlkZW50aWZpZXIgb24gdGhlIGNv\n" +
            "cmUgZW52aXJvbm1lbnQuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACHJlcXVpcmVkWgAACG5pbGxh\n" +
            "YmxlWgEAB2RlZmF1bHRzAAExAAptaW4tbGVuZ3RoSgAAAAAAAAAAAAptYXgtbGVuZ3RoSgAAAAAA\n" +
            "AAAXABFvYmplY3Qtc3RvcmUtcGF0aG8AAAAIAAR0eXBldHMAC2Rlc2NyaXB0aW9ucwDVRGVub3Rl\n" +
            "cyBhIHJlbGF0aXZlIG9yIGFic29sdXRlIGZpbGVzeXN0ZW0gcGF0aCBkZW5vdGluZyB3aGVyZSB0\n" +
            "aGUgdHJhbnNhY3Rpb24gbWFuYWdlciBvYmplY3Qgc3RvcmUgc2hvdWxkIHN0b3JlIGRhdGEuIEJ5\n" +
            "IGRlZmF1bHQgdGhlIHZhbHVlIGlzIHRyZWF0ZWQgYXMgcmVsYXRpdmUgdG8gdGhlIHBhdGggZGVu\n" +
            "b3RlZCBieSB0aGUgInJlbGF0aXZlLXRvIiBhdHRyaWJ1dGUuABNleHByZXNzaW9ucy1hbGxvd2Vk\n" +
            "WgEACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2RlZmF1bHRzAA90eC1vYmplY3Qtc3RvcmUACm1p\n" +
            "bi1sZW5ndGhKAAAAAAAAAAEACm1heC1sZW5ndGhKAAAAAH////8ABHBhdGhvAAAACAAEdHlwZXRz\n" +
            "AAtkZXNjcmlwdGlvbnMAzURlbm90ZXMgYSByZWxhdGl2ZSBvciBhYnNvbHV0ZSBmaWxlc3lzdGVt\n" +
            "IHBhdGggZGVub3Rpbmcgd2hlcmUgdGhlIHRyYW5zYWN0aW9uIG1hbmFnZXIgY29yZSBzaG91bGQg\n" +
            "c3RvcmUgZGF0YS4gQnkgZGVmYXVsdCB0aGUgdmFsdWUgaXMgdHJlYXRlZCBhcyByZWxhdGl2ZSB0\n" +
            "byB0aGUgcGF0aCBkZW5vdGVkIGJ5IHRoZSAicmVsYXRpdmUtdG8iIGF0dHJpYnV0ZS4AE2V4cHJl\n" +
            "c3Npb25zLWFsbG93ZWRaAQAIcmVxdWlyZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdHMAA3ZhcgAK\n" +
            "bWluLWxlbmd0aEoAAAAAAAAAAQAKbWF4LWxlbmd0aEoAAAAAf////wARZW5hYmxlLXRzbS1zdGF0\n" +
            "dXNvAAAABgAEdHlwZXRaAAtkZXNjcmlwdGlvbnMAdVdoZXRoZXIgdGhlIHRyYW5zYWN0aW9uIHN0\n" +
            "YXR1cyBtYW5hZ2VyIChUU00pIHNlcnZpY2UsIG5lZWRlZCBmb3Igb3V0IG9mIHByb2Nlc3MgcmVj\n" +
            "b3ZlcnksIHNob3VsZCBiZSBwcm92aWRlZCBvciBub3QuLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoB\n" +
            "AAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgAAEWVuYWJsZS1zdGF0aXN0aWNzbwAA\n" +
            "AAYABHR5cGV0WgALZGVzY3JpcHRpb25zACVXaGV0aGVyIHN0YXRpc3RpY3Mgc2hvdWxkIGJlIGVu\n" +
            "YWJsZWQuABNleHByZXNzaW9ucy1hbGxvd2VkWgEACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2Rl\n" +
            "ZmF1bHRaAAAYb2JqZWN0LXN0b3JlLXJlbGF0aXZlLXRvbwAAAAgABHR5cGV0cwALZGVzY3JpcHRp\n" +
            "b25zAVpSZWZlcmVuY2VzIGEgZ2xvYmFsIHBhdGggY29uZmlndXJhdGlvbiBpbiB0aGUgZG9tYWlu\n" +
            "IG1vZGVsLCBkZWZhdWx0aW5nIHRvIHRoZSBKQm9zcyBBcHBsaWNhdGlvbiBTZXJ2ZXIgZGF0YSBk\n" +
            "aXJlY3RvcnkgKGpib3NzLnNlcnZlci5kYXRhLmRpcikuIFRoZSB2YWx1ZSBvZiB0aGUgInBhdGgi\n" +
            "IGF0dHJpYnV0ZSB3aWxsIHRyZWF0ZWQgYXMgcmVsYXRpdmUgdG8gdGhpcyBwYXRoLiBVc2UgYW4g\n" +
            "ZW1wdHkgc3RyaW5nIHRvIGRpc2FibGUgdGhlIGRlZmF1bHQgYmVoYXZpb3IgYW5kIGZvcmNlIHRo\n" +
            "ZSB2YWx1ZSBvZiB0aGUgInBhdGgiIGF0dHJpYnV0ZSB0byBiZSB0cmVhdGVkIGFzIGFuIGFic29s\n" +
            "dXRlIHBhdGguABNleHByZXNzaW9ucy1hbGxvd2VkWgEACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEA\n" +
            "B2RlZmF1bHRzABVqYm9zcy5zZXJ2ZXIuZGF0YS5kaXIACm1pbi1sZW5ndGhKAAAAAAAAAAEACm1h\n" +
            "eC1sZW5ndGhKAAAAAH////8AEHJlcGx5LXByb3BlcnRpZXNvAAAAAAAOcmVhZC1hdHRyaWJ1dGVv\n" +
            "AAAABAAOb3BlcmF0aW9uLW5hbWVzAA5yZWFkLWF0dHJpYnV0ZQALZGVzY3JpcHRpb25zADhHZXRz\n" +
            "IHRoZSB2YWx1ZSBvZiBhbiBhdHRyaWJ1dGUgZm9yIHRoZSBzZWxlY3RlZCByZXNvdXJjZQAScmVx\n" +
            "dWVzdC1wcm9wZXJ0aWVzbwAAAAIABG5hbWVvAAAABwAEdHlwZXRzAAtkZXNjcmlwdGlvbnMASlRo\n" +
            "ZSBuYW1lIG9mIHRoZSBhdHRyaWJ1dGUgdG8gZ2V0IHRoZSB2YWx1ZSBmb3IgdW5kZXIgdGhlIHNl\n" +
            "bGVjdGVkIHJlc291cmNlABNleHByZXNzaW9ucy1hbGxvd2VkWgAACHJlcXVpcmVkWgEACG5pbGxh\n" +
            "YmxlWgAACm1pbi1sZW5ndGhKAAAAAAAAAAEACm1heC1sZW5ndGhKAAAAAH////8AEGluY2x1ZGUt\n" +
            "ZGVmYXVsdHNvAAAABgAEdHlwZXRaAAtkZXNjcmlwdGlvbnMAgUJvb2xlYW4gdG8gZW5hYmxlL2Rp\n" +
            "c2FibGUgZGVmYXVsdCByZWFkaW5nLiBJbiBjYXNlIGl0IGlzIHNldCB0byBmYWxzZSBvbmx5IGF0\n" +
            "dHJpYnV0ZSBzZXQgYnkgdXNlciBhcmUgcmV0dXJuZWQgaWdub3JpbmcgdW5kZWZpbmVkLgATZXhw\n" +
            "cmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgEAEHJl\n" +
            "cGx5LXByb3BlcnRpZXNvAAAAAgAEdHlwZXRvAAtkZXNjcmlwdGlvbnMASFRoZSB2YWx1ZSBvZiB0\n" +
            "aGUgYXR0cmlidXRlLiBUaGUgdHlwZSB3aWxsIGJlIHRoYXQgb2YgdGhlIGF0dHJpYnV0ZSBmb3Vu\n" +
            "ZAAGd2hvYW1pbwAAAAQADm9wZXJhdGlvbi1uYW1lcwAGd2hvYW1pAAtkZXNjcmlwdGlvbnMAOVJl\n" +
            "dHVybnMgdGhlIGlkZW50aXR5IG9mIHRoZSBjdXJyZW50bHkgYXV0aGVudGljYXRlZCB1c2VyLgAS\n" +
            "cmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAEAB3ZlcmJvc2VvAAAABgAEdHlwZXRaAAtkZXNjcmlwdGlv\n" +
            "bnMAM0lmIHNldCB0byB0cnVlIHdob2FtaSBhbHNvIHJldHVybnMgdGhlIHVzZXJzIHJvbGVzLgAT\n" +
            "ZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0WgAA\n" +
            "EHJlcGx5LXByb3BlcnRpZXNvAAAAAAATcmVhZC1jaGlsZHJlbi10eXBlc28AAAAEAA5vcGVyYXRp\n" +
            "b24tbmFtZXMAE3JlYWQtY2hpbGRyZW4tdHlwZXMAC2Rlc2NyaXB0aW9ucwBDR2V0cyB0aGUgdHlw\n" +
            "ZSBuYW1lcyBvZiBhbGwgdGhlIGNoaWxkcmVuIHVuZGVyIHRoZSBzZWxlY3RlZCByZXNvdXJjZQAS\n" +
            "cmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAAAEHJlcGx5LXByb3BlcnRpZXNvAAAAAwAEdHlwZXRsAAp2\n" +
            "YWx1ZS10eXBldHMAC2Rlc2NyaXB0aW9ucwASVGhlIGNoaWxkcmVuIHR5cGVzABRyZWFkLW9wZXJh\n" +
            "dGlvbi1uYW1lc28AAAAEAA5vcGVyYXRpb24tbmFtZXMAFHJlYWQtb3BlcmF0aW9uLW5hbWVzAAtk\n" +
            "ZXNjcmlwdGlvbnMAO0dldHMgdGhlIG5hbWVzIG9mIGFsbCB0aGUgb3BlcmF0aW9ucyBmb3IgdGhl\n" +
            "IGdpdmVuIHJlc291cmNlABJyZXF1ZXN0LXByb3BlcnRpZXNvAAAAAAAQcmVwbHktcHJvcGVydGll\n" +
            "c28AAAADAAR0eXBldGwACnZhbHVlLXR5cGV0cwALZGVzY3JpcHRpb25zABNUaGUgb3BlcmF0aW9u\n" +
            "IG5hbWVzABJ1bmRlZmluZS1hdHRyaWJ1dGVvAAAABAAOb3BlcmF0aW9uLW5hbWVzABJ1bmRlZmlu\n" +
            "ZS1hdHRyaWJ1dGUAC2Rlc2NyaXB0aW9ucwBGU2V0cyB0aGUgdmFsdWUgb2YgYW4gYXR0cmlidXRl\n" +
            "IG9mIHRoZSBzZWxlY3RlZCByZXNvdXJjZSB0byAndW5kZWZpbmVkJwAScmVxdWVzdC1wcm9wZXJ0\n" +
            "aWVzbwAAAAEABG5hbWVvAAAABwAEdHlwZXRzAAtkZXNjcmlwdGlvbnMAPFRoZSBuYW1lIG9mIHRo\n" +
            "ZSBhdHRyaWJ1dGUgd2hpY2ggc2hvdWxkIGJlIHNldCB0byAndW5kZWZpbmVkJwATZXhwcmVzc2lv\n" +
            "bnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoBAAhuaWxsYWJsZVoAAAptaW4tbGVuZ3RoSgAAAAAAAAAB\n" +
            "AAptYXgtbGVuZ3RoSgAAAAB/////ABByZXBseS1wcm9wZXJ0aWVzbwAAAAAAF3JlYWQtY2hpbGRy\n" +
            "ZW4tcmVzb3VyY2VzbwAAAAQADm9wZXJhdGlvbi1uYW1lcwAXcmVhZC1jaGlsZHJlbi1yZXNvdXJj\n" +
            "ZXMAC2Rlc2NyaXB0aW9ucwBNUmVhZHMgaW5mb3JtYXRpb24gYWJvdXQgYWxsIG9mIGEgcmVzb3Vy\n" +
            "Y2UncyBjaGlsZHJlbiB0aGF0IGFyZSBvZiBhIGdpdmVuIHR5cGUAEnJlcXVlc3QtcHJvcGVydGll\n" +
            "c28AAAAGAApjaGlsZC10eXBlbwAAAAcABHR5cGV0cwALZGVzY3JpcHRpb25zAD9UaGUgbmFtZSBv\n" +
            "ZiB0aGUgcmVzb3VyY2UgdW5kZXIgd2hpY2ggdG8gZ2V0IHRoZSBjaGlsZCByZXNvdXJjZXMAE2V4\n" +
            "cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWlyZWRaAQAIbmlsbGFibGVaAAAKbWluLWxlbmd0aEoA\n" +
            "AAAAAAAAAQAKbWF4LWxlbmd0aEoAAAAAf////wAJcmVjdXJzaXZlbwAAAAYABHR5cGV0WgALZGVz\n" +
            "Y3JpcHRpb25zAEhXaGV0aGVyIHRvIGdldCB0aGUgY2hpbGRyZW4gcmVjdXJzaXZlbHkuIElmIGFi\n" +
            "c2VudCwgZmFsc2UgaXMgdGhlIGRlZmF1bHQAE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWly\n" +
            "ZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoAAA9yZWN1cnNpdmUtZGVwdGhvAAAABgAEdHlwZXRJ\n" +
            "AAtkZXNjcmlwdGlvbnMASFRoZSBkZXB0aCB0byB3aGljaCBpbmZvcm1hdGlvbiBhYm91dCBjaGls\n" +
            "ZCByZXNvdXJjZXMgc2hvdWxkIGJlIGluY2x1ZGVkLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhy\n" +
            "ZXF1aXJlZFoAAAhuaWxsYWJsZVoBAAdkZWZhdWx0SQAAAAAAB3Byb3hpZXNvAAAABgAEdHlwZXRa\n" +
            "AAtkZXNjcmlwdGlvbnMAwldoZXRoZXIgdG8gaW5jbHVkZSByZW1vdGUgcmVzb3VyY2VzIGluIGEg\n" +
            "cmVjdXJzaXZlIHF1ZXJ5IChpLmUuIGhvc3QgbGV2ZWwgcmVzb3VyY2VzIGluIGEgcXVlcnkgb2Yg\n" +
            "dGhlIGRvbWFpbiByb290OyBydW5uaW5nIHNlcnZlciByZXNvdXJjZXMgaW4gYSBxdWVyeSBvZiBh\n" +
            "IGhvc3QpLiBJZiBhYnNlbnQsIGZhbHNlIGlzIHRoZSBkZWZhdWx0ABNleHByZXNzaW9ucy1hbGxv\n" +
            "d2VkWgAACHJlcXVpcmVkWgAACG5pbGxhYmxlWgEAB2RlZmF1bHRaAAAPaW5jbHVkZS1ydW50aW1l\n" +
            "bwAAAAYABHR5cGV0WgALZGVzY3JpcHRpb25zARpXaGV0aGVyIHRvIGluY2x1ZGUgcnVudGltZSBh\n" +
            "dHRyaWJ1dGVzIChpLmUuIHRob3NlIHdob3NlIHZhbHVlIGRvZXMgbm90IGNvbWUgZnJvbSB0aGUg\n" +
            "cGVyc2lzdGVudCBjb25maWd1cmF0aW9uKSBpbiB0aGUgcmVzcG9uc2UuIElmIGFic2VudCwgZmFs\n" +
            "c2UgaXMgdGhlIGRlZmF1bHQuIElnbm9yZWQgaWYgdGhlICdyZWN1cnNpdmUnIHBhcmFtZXRlciBp\n" +
            "cyBzZXQgdG8gJ3RydWUnOyBpLmUuIHJ1bnRpbWUgYXR0cmlidXRlcyBjYW4gb25seSBiZSByZWFk\n" +
            "IGluIG5vbi1yZWN1cnNpdmUgcXVlcmllcy4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAIcmVxdWly\n" +
            "ZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoAABBpbmNsdWRlLWRlZmF1bHRzbwAAAAYABHR5cGV0\n" +
            "WgALZGVzY3JpcHRpb25zAIFCb29sZWFuIHRvIGVuYWJsZS9kaXNhYmxlIGRlZmF1bHQgcmVhZGlu\n" +
            "Zy4gSW4gY2FzZSBpdCBpcyBzZXQgdG8gZmFsc2Ugb25seSBhdHRyaWJ1dGUgc2V0IGJ5IHVzZXIg\n" +
            "YXJlIHJldHVybmVkIGlnbm9yaW5nIHVuZGVmaW5lZC4AE2V4cHJlc3Npb25zLWFsbG93ZWRaAAAI\n" +
            "cmVxdWlyZWRaAAAIbmlsbGFibGVaAQAHZGVmYXVsdFoBABByZXBseS1wcm9wZXJ0aWVzbwAAAAMA\n" +
            "BHR5cGV0bAAKdmFsdWUtdHlwZXRvAAtkZXNjcmlwdGlvbnMAFlRoZSBjaGlsZHJlbiByZXNvdXJj\n" +
            "ZXMAD3dyaXRlLWF0dHJpYnV0ZW8AAAAEAA5vcGVyYXRpb24tbmFtZXMAD3dyaXRlLWF0dHJpYnV0\n" +
            "ZQALZGVzY3JpcHRpb25zADhTZXRzIHRoZSB2YWx1ZSBvZiBhbiBhdHRyaWJ1dGUgZm9yIHRoZSBz\n" +
            "ZWxlY3RlZCByZXNvdXJjZQAScmVxdWVzdC1wcm9wZXJ0aWVzbwAAAAIABG5hbWVvAAAABwAEdHlw\n" +
            "ZXRzAAtkZXNjcmlwdGlvbnMASlRoZSBuYW1lIG9mIHRoZSBhdHRyaWJ1dGUgdG8gc2V0IHRoZSB2\n" +
            "YWx1ZSBmb3IgdW5kZXIgdGhlIHNlbGVjdGVkIHJlc291cmNlABNleHByZXNzaW9ucy1hbGxvd2Vk\n" +
            "WgAACHJlcXVpcmVkWgEACG5pbGxhYmxlWgAACm1pbi1sZW5ndGhKAAAAAAAAAAEACm1heC1sZW5n\n" +
            "dGhKAAAAAH////8ABXZhbHVlbwAAAAcABHR5cGV0cwALZGVzY3JpcHRpb25zAIZUaGUgdmFsdWUg\n" +
            "b2YgdGhlIGF0dHJpYnV0ZSB0byBzZXQgdGhlIHZhbHVlIGZvciB1bmRlciB0aGUgc2VsZWN0ZWQg\n" +
            "cmVzb3VyY2UuIE1heSBiZSBudWxsIGlmIHRoZSB1bmRlcmx5aW5nIG1vZGVsIHN1cHBvcnRzIG51\n" +
            "bGwgdmFsdWVzLgATZXhwcmVzc2lvbnMtYWxsb3dlZFoAAAhyZXF1aXJlZFoAAAhuaWxsYWJsZVoB\n" +
            "AAptaW4tbGVuZ3RoSgAAAAAAAAABAAptYXgtbGVuZ3RoSgAAAAB/////ABByZXBseS1wcm9wZXJ0\n" +
            "aWVzbwAAAAAACGNoaWxkcmVubwAAAAEACWxvZy1zdG9yZW8AAAACAAtkZXNjcmlwdGlvbnMAPFJl\n" +
            "cHJlc2VudGF0aW9uIG9mIHRoZSB0cmFuc2FjdGlvbiBsb2dnaW5nIHN0b3JhZ2UgbWVjaGFuaXNt\n" +
            "LgARbW9kZWwtZGVzY3JpcHRpb251AAZzdGVwLTJvAAAAAgAHb3V0Y29tZXMAB3N1Y2Nlc3MABnJl\n" +
            "c3VsdG8AAAAQAA9kZWZhdWx0LXRpbWVvdXRJAAABLAARZW5hYmxlLXN0YXRpc3RpY3NaAQARZW5h\n" +
            "YmxlLXRzbS1zdGF0dXNaAAADanRzWgAAD25vZGUtaWRlbnRpZmllcnMAATEAEW9iamVjdC1zdG9y\n" +
            "ZS1wYXRocwAPdHgtb2JqZWN0LXN0b3JlABhvYmplY3Qtc3RvcmUtcmVsYXRpdmUtdG9zABVqYm9z\n" +
            "cy5zZXJ2ZXIuZGF0YS5kaXIABHBhdGhzAAN2YXIAG3Byb2Nlc3MtaWQtc29ja2V0LW1heC1wb3J0\n" +
            "c0kAAAAKAA9wcm9jZXNzLWlkLXV1aWRaAQARcmVjb3ZlcnktbGlzdGVuZXJaAAALcmVsYXRpdmUt\n" +
            "dG9zABVqYm9zcy5zZXJ2ZXIuZGF0YS5kaXIADnNvY2tldC1iaW5kaW5ncwAYdHhuLXJlY292ZXJ5\n" +
            "LWVudmlyb25tZW50ABVzdGF0dXMtc29ja2V0LWJpbmRpbmdzABJ0eG4tc3RhdHVzLW1hbmFnZXIA\n" +
            "EXVzZS1ob3JuZXRxLXN0b3JlWgAACWxvZy1zdG9yZW8AAAABAAlsb2ctc3RvcmV1AA1zZXJ2ZXIt\n" +
            "Z3JvdXBzdQ==";
}
