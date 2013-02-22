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
package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;
import org.jboss.mbui.gui.behaviour.InteractionEvent;
import org.jboss.mbui.gui.behaviour.PresentationEvent;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.widgets.ModelNodeForm;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceAttribute;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.jboss.mbui.model.behaviour.ResourceType.*;
import static org.jboss.mbui.model.behaviour.ResourceType.System;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 * @date 11/01/2012
 */
public class FormStrategy implements ReificationStrategy<ReificationWidget>
{

    private static final QName SAVE_ID = QName.valueOf("org.jboss.as:save");
    private static final QName LOAD_ID = QName.valueOf("org.jboss.as:load");
    private static final QName RESET_ID = QName.valueOf("org.jboss.as:reset");
    private static final QName UPDATE_ID = QName.valueOf("org.jboss.as:form-update");

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {
        FormAdapter adapter = null;
        if (interactionUnit != null)
        {
            Map<String, ModelNode> descriptions = context.get (ContextKey.MODEL_DESCRIPTIONS);
            ModelNode modelDescription = descriptions.get(interactionUnit.getId().getNamespaceURI());
            assert modelDescription!=null : "Model description is required to execute FormStrategy";

            EventBus eventBus = context.get(ContextKey.EVENTBUS);
            assert eventBus!=null : "Coordinator bus is required to execute FormStrategy";

            adapter = new FormAdapter(interactionUnit, eventBus, modelDescription);
        }
        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof org.jboss.mbui.model.structure.as7.Form;
    }

    class FormAdapter implements ReificationWidget
    {
        final ModelNodeForm form;
        final InteractionUnit interactionUnit;
        private SafeHtmlBuilder helpTexts;
        private EventBus coordinator;

        FormAdapter(final InteractionUnit interactionUnit, EventBus coordinator, final ModelNode modelDescription)
        {
            this.interactionUnit = interactionUnit;
            this.coordinator = coordinator;

            this.form = new ModelNodeForm();
            this.form.setNumColumns(2);
            this.form.setEnabled(false);

            assert modelDescription.hasDefined("attributes") : "Invalid model description. Expected child 'attributes'";

            List<Property> attributeDescriptions = modelDescription.get("attributes").asPropertyList();

            ResourceMapping resourceMapping = (ResourceMapping)
                    this.interactionUnit.findMapping(MappingType.RESOURCE);

            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            List<FormItem> items = new ArrayList<FormItem>(attributes.size());

            helpTexts = new SafeHtmlBuilder();
            helpTexts.appendHtmlConstant("<table class='help-attribute-descriptions'>");

            for (ResourceAttribute attribute : attributes)
            {
                for(Property attr : attributeDescriptions)
                {
                    if(!attr.getName().equals(attribute.getName()))
                        continue;


                    char[] stringArray = attr.getName().toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);

                    String label = new String(stringArray).replace("-", " ");
                    ModelNode attrValue = attr.getValue();

                    // help
                    helpTexts.appendHtmlConstant("<tr class='help-field-row'>");
                    helpTexts.appendHtmlConstant("<td class='help-field-name'>");
                    helpTexts.appendEscaped(label).appendEscaped(": ");
                    helpTexts.appendHtmlConstant("</td>");
                    helpTexts.appendHtmlConstant("<td class='help-field-desc'>");
                    try {
                        helpTexts.appendHtmlConstant(attrValue.get("description").asString());
                    } catch (Throwable e) {
                        // ignore parse errors
                        helpTexts.appendHtmlConstant("<i>Failed to parse description</i>");
                    }
                    helpTexts.appendHtmlConstant("</td>");
                    helpTexts.appendHtmlConstant("</tr>");

                    boolean required = !attr.getValue().get("nillable").asBoolean();
                    ModelType type = ModelType.valueOf(attrValue.get("type").asString());
                    //System.out.println(attr.getName()+">"+type);
                    switch(type)
                    {
                        case BOOLEAN:
                            CheckBoxItem checkBoxItem = new CheckBoxItem(attr.getName(), label);
                            items.add(checkBoxItem);
                            break;
                        case DOUBLE:
                            NumberBoxItem num = new NumberBoxItem(attr.getName(), label);
                            num.setRequired(required);
                            items.add(num);
                            break;
                        case LONG:
                            NumberBoxItem num2 = new NumberBoxItem(attr.getName(), label);
                            num2.setRequired(required);
                            items.add(num2);
                            break;
                        case INT:
                            NumberBoxItem num3 = new NumberBoxItem(attr.getName(), label);
                            num3.setRequired(required);
                            items.add(num3);
                            break;
                        case STRING:
                            if(attrValue.get("allowed").isDefined())
                            {
                                List<ModelNode> allowed = attrValue.get("allowed").asList();
                                Set<String> allowedValues = new HashSet<String>(allowed.size());
                                for(ModelNode value : allowed)
                                    allowedValues.add(value.asString());

                                ComboBoxItem combo = new ComboBoxItem(attr.getName(), label);
                                combo.setValueMap(allowedValues);
                            }
                            else
                            {
                                TextBoxItem tb = new TextBoxItem(attr.getName(), label);
                                tb.setRequired(required);
                                items.add(tb);
                            }
                            break;
                        default:
                            throw new RuntimeException("Unsupported ModelType "+type);
                    }
                }
            }

            helpTexts.appendHtmlConstant("</table>");

            form.setFields(items.toArray(new FormItem[]{}));

        }

        @Override
        public InteractionUnit getInteractionUnit() {
            return interactionUnit;
        }

        @Override
        public void add(final ReificationWidget widget)
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

                            InteractionEvent saveEvent = new InteractionEvent(SAVE_ID);
                            saveEvent.setPayload(form.getChangedValues());

                            coordinator.fireEventFromSource(
                                    saveEvent,
                                    interactionUnit.getId()
                            );
                        }

                        @Override
                        public void onDelete(ModelNode entity) {
                            // unsupported
                        }
                    });

            StaticHelpPanel help = new StaticHelpPanel(helpTexts.toSafeHtml());

            layout.add(tools.asWidget());
            layout.add(help.asWidget());
            layout.add(form.asWidget());

            // handle resets within this scope
            coordinator.addHandler(SystemEvent.TYPE, new SystemEvent.Handler() {
                @Override
                public boolean accepts(SystemEvent event) {
                    return event.getId().equals(RESET_ID);
                }

                @Override
                public void onSystemEvent(SystemEvent event) {
                    form.clearValues();


                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            // request loading of data
                            InteractionEvent loadEvent = new InteractionEvent(LOAD_ID);

                            // update interaction units
                            coordinator.fireEventFromSource(
                                    loadEvent,
                                    interactionUnit.getId()
                            );
                        }
                    });
                }
            });


            // handle the results of function calls
            coordinator.addHandler(PresentationEvent.TYPE, new PresentationEvent.PresentationHandler()
            {
                @Override
                public boolean accepts(PresentationEvent event) {
                    boolean isFormUpdate = event.getId().equalsIgnoreSuffix(UPDATE_ID);
                    boolean matchingId = event.getTarget().equalsIgnoreSuffix(getInteractionUnit().getId());

                    // only single resources accepted (might be collection, see LoadResourceProcedure)
                    boolean payloadMatches = event.getPayload() instanceof ModelNode;

                    return isFormUpdate && matchingId && payloadMatches;
                }

                @Override
                public void onPresentationEvent(PresentationEvent event) {

                    assert (event.getPayload() instanceof ModelNode) : "Unexpected type "+event.getPayload().getClass();
                    form.edit((ModelNode)event.getPayload());
                }
            });


            // Register inputs and outputs

            Resource<ResourceType> saveEvent = new Resource<ResourceType>(SAVE_ID, Event);
            Resource<ResourceType> loadEvent = new Resource<ResourceType>(LOAD_ID, Event);
            Resource<ResourceType> reset = new Resource<ResourceType>(RESET_ID, System);
            Resource<ResourceType> update = new Resource<ResourceType>(getInteractionUnit().getId(), Presentation);

            getInteractionUnit().setOutputs(saveEvent, loadEvent);
            getInteractionUnit().setInputs(reset, update);

            return layout;
        }
    }
}
