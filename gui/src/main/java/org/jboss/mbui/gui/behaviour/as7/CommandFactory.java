package org.jboss.mbui.gui.behaviour.as7;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelType;
import org.jboss.dmr.client.Property;
import org.jboss.mbui.gui.behaviour.ModelDrivenCommand;
import org.jboss.mbui.gui.behaviour.StatementEvent;
import org.jboss.mbui.gui.reification.strategy.SelectStrategy;
import org.jboss.mbui.gui.reification.widgets.ModelNodeForm;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.as7.StereoTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 *
 * Encapsulates the creation of {@link ModelDrivenCommand}"s to be used with {@link DMROperationProcedure}'s.
 *
 * @author Heiko Braun
 * @date 3/6/13
 */
public class CommandFactory {

    private final DispatchAsync dispatcher;

    public CommandFactory(DispatchAsync dispatcher) {
        this.dispatcher = dispatcher;
    }

    ModelDrivenCommand createCommand(
            String operationName,
            OperationContext context)
    {
        if (operationName.equals("remove"))
        {
            return createRemoveCmd(context);
        }
        else
        {
            return createGenericCommand(operationName, context);
        }

    }

    private ModelDrivenCommand createRemoveCmd(final OperationContext context) {
        return new ModelDrivenCommand() {
            @Override
            public void execute(Dialog dialog, Object data) {

                final ModelNode operation = context.getAddress().asResource(context.getStatementContext());
                operation.get(OP).set(REMOVE);
                final String label = operation.get(ADDRESS).asString();   // TODO

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Resource"),
                        Console.MESSAGES.deleteConfirm(label),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean confirmed) {

                                if(confirmed)
                                {
                                    dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                                        @Override
                                        public void onSuccess(DMRResponse dmrResponse) {
                                            ModelNode response = dmrResponse.get();
                                            if(response.isFailure())
                                            {
                                                Console.error(Console.MESSAGES.deletionFailed(label), response.getFailureDescription());
                                            }
                                            else
                                            {
                                                Console.info(Console.MESSAGES.deleted(label));

                                                clearReset(context);

                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        };
    }

    /**
     * TODO: This is considered a temporary solution.
     *
     * It's difficult to manage the states of all interaction units after modification to the model.
     * This is a very naiv and pragmatic approach with certain (usability) drawbacks.
     *
     * @param context
     */
    private void clearReset(final OperationContext context) {
        // clear the select statement
        context.getCoordinator().fireEvent(
                new StatementEvent(
                        SelectStrategy.SELECT_ID,
                        "selected.entity",
                        null)
        );

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                context.getCoordinator().onReset();
            }
        });
    }

    private ModelDrivenCommand createGenericCommand(final String operationName, final OperationContext context) {

        assert context.getUnit().doesProduce() : "The unit associated with a command need to be a producer";

        InteractionUnit<StereoTypes> unit = context.getUnit();
        Resource<ResourceType> output = unit.getOutputs().iterator().next();
        final ModelNode operationDescription = context.getOperationDescriptions().get(output.getId());

        assert operationDescription!=null : "Operation meta data required for "+output.getId() + " on "+context.getUnit().getId();

        final List<Property> parameterMetaData = operationDescription.get("request-properties").asPropertyList();


        Delegation delegation = null;

        if(parameterMetaData.isEmpty())
        {
            // simple feedback in case no parameters are required
            delegation = new Delegation(new FeedbackDelegate(operationName, context));
        }
        else
        {
            // form with required parameter (acts as guard on its own)
            delegation = new Delegation(new FormDelegate(context,operationDescription));
        }

        return delegation;
    }

    /**
     * Simple feedback for operations w/o (required) input parameter
     */
    private class FeedbackDelegate implements Command {

        private OperationContext context;
        private String operationName;

        FeedbackDelegate(String operationName, OperationContext context) {
            this.operationName = operationName;
            this.context = context;
        }

        @Override
        public void execute() {
            final ModelNode operation = context.getAddress().asResource(context.getStatementContext());
            operation.get(OP).set(operationName);
            final String label = operation.get(ADDRESS).asString();

            Feedback.confirm(
                    "Execute Operation" ,
                    "Invoke operation " +operationName+ " on " + label + "?",
                    new Feedback.ConfirmationHandler() {
                        @Override
                        public void onConfirmation(boolean confirmed) {
                            if(confirmed)
                            {
                                dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                                    @Override
                                    public void onSuccess(DMRResponse dmrResponse) {
                                        ModelNode response = dmrResponse.get();

                                        String msg = "Operation " +operationName+ " on " + label;

                                        if(response.isFailure())
                                        {
                                            Console.error(Console.MESSAGES.failed(msg), response.getFailureDescription());
                                        }
                                        else
                                        {
                                            Console.info(Console.MESSAGES.successful(msg));

                                            clearReset(context);

                                        }
                                    }
                                });
                            }
                        }
                    }
            );
        }
    }


    /**
     * A delegate that prompts for (required) input parameter to an operation before it's invocation
     */
    private class FormDelegate implements Command {

        private OperationContext context;
        private ModelNode operationmetaData;
        private List<Property> parameterMetaData;
        private Widget widget;
        private ModelNodeForm form;
        private DefaultWindow window;

        private FormDelegate(OperationContext context, ModelNode operationMetaData) {
            this.context = context;
            this.operationmetaData = operationMetaData;
            this.parameterMetaData = operationMetaData.get("request-properties").asPropertyList();

            init();
        }

        private void init() {

            VerticalPanel panel = new VerticalPanel();
            panel.setStyleName("window-content");

            panel.add(new ContentDescription(operationmetaData.get("description").asString()));


            // Helptexts


            SafeHtmlBuilder helpTexts = new SafeHtmlBuilder();
            helpTexts.appendHtmlConstant("<table class='help-attribute-descriptions'>");

            // The form

            form = new ModelNodeForm();
            List<FormItem> items = new ArrayList<FormItem>();

            // each add operation requires an artificial parameter 'entity.key'
            if("add".equals(operationmetaData.get("operation-name").asString()))
                items.add(new TextBoxItem("entity.key", "Name", true));

            for(Property param : parameterMetaData)
            {

                char[] stringArray = param.getName().toCharArray();
                stringArray[0] = Character.toUpperCase(stringArray[0]);

                String label = new String(stringArray).replace("-", " ");
                ModelNode attrValue = param.getValue();

                boolean required = param.getValue().get(REQUIRED).asBoolean();

                // skip non-required parameters
                if(!required) continue;

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

                ModelType type = ModelType.valueOf(attrValue.get(TYPE).asString());

                switch(type)
                {
                    case BOOLEAN:
                        CheckBoxItem checkBoxItem = new CheckBoxItem(param.getName(), label);
                        items.add(checkBoxItem);
                        if(param.getValue().hasDefined(DEFAULT))
                            checkBoxItem.setValue(param.getValue().get(DEFAULT).asBoolean());
                        break;
                    case DOUBLE:
                        NumberBoxItem num = new NumberBoxItem(param.getName(), label);
                        num.setRequired(required);
                        items.add(num);

                        if(param.getValue().hasDefined(DEFAULT))
                            num.setValue(param.getValue().get(DEFAULT).asDouble());

                        break;
                    case LONG:
                        NumberBoxItem num2 = new NumberBoxItem(param.getName(), label);
                        num2.setRequired(required);
                        items.add(num2);

                        if(param.getValue().hasDefined(DEFAULT))
                            num2.setValue(param.getValue().get(DEFAULT).asLong());

                        break;
                    case INT:
                        NumberBoxItem num3 = new NumberBoxItem(param.getName(), label);
                        num3.setRequired(required);
                        items.add(num3);

                        if(param.getValue().hasDefined(DEFAULT))
                            num3.setValue(param.getValue().get(DEFAULT).asInt());

                        break;
                    case STRING:
                        if(attrValue.hasDefined("allowed"))
                        {
                            List<ModelNode> allowed = attrValue.get("allowed").asList();
                            Set<String> allowedValues = new HashSet<String>(allowed.size());
                            for(ModelNode value : allowed)
                                allowedValues.add(value.asString());

                            ComboBoxItem combo = new ComboBoxItem(param.getName(), label);
                            combo.setValueMap(allowedValues);

                            if(param.getValue().hasDefined(DEFAULT))
                                combo.setValue(param.getValue().get(DEFAULT).asString());

                        }
                        else
                        {
                            TextBoxItem tb = new TextBoxItem(param.getName(), label);
                            tb.setRequired(required);
                            items.add(tb);

                            if(param.getValue().hasDefined(DEFAULT))
                                tb.setValue(param.getValue().get(DEFAULT).asString());

                        }

                        break;
                    default:
                        Log.warn("Ignore ModelType " + type);
                }

            }

            helpTexts.appendHtmlConstant("</table>");
            StaticHelpPanel help = new StaticHelpPanel(helpTexts.toSafeHtml());

            form.setFields(items.toArray(new FormItem[]{}));
            panel.add(help.asWidget());
            panel.add(form.asWidget());

            WindowContentBuilder builder = new WindowContentBuilder(
                    panel,
                    new DialogueOptions(
                            "Finish",
                            new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent clickEvent) {
                                    // save
                                    FormValidation validation = form.validate();
                                    if(!validation.hasErrors())
                                    {
                                        // proceed
                                        invokeOperaton(form.getUpdatedEntity());
                                    }
                                }
                            },
                            "Cancel",
                            new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent clickEvent) {
                                    // cancel
                                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                        @Override
                                        public void execute() {
                                            window.hide();
                                        }
                                    });
                                }
                            }
                    )
            );

            this.widget = builder.build();
        }


        @Override
        public void execute() {

            // TODO: clearing the form is necessary in order to re-use the dom elements
            // but it also clears the default values. These need to associated with a FormItem
            // by other means then calling FormItem.setValue()

            form.clearValues();

            String operationName = operationmetaData.get("operation-name").asString();
            final ModelNode operation = context.getAddress().asResource(context.getStatementContext());
            operation.get(OP).set(operationName);

            window = new DefaultWindow("Execute Operation");
            window.setWidth(480);
            window.setHeight(450);

            window.trapWidget(widget);

            window.setGlassEnabled(true);
            window.center();
        }

        private void invokeOperaton(final ModelNode inputParameter) {

            // TODO: Why are wildcards not resolved against the statement context?

            final String operationName = operationmetaData.get("operation-name").asString();

            String[] wildcards = inputParameter.hasDefined("entity.key") ?
                    new String[] {inputParameter.get("entity.key").asString()} : new String[]{};

            final ModelNode operation = context.getAddress().asResource(context.getStatementContext(), wildcards);
            operation.get(OP).set(operationName);

            for(String param : inputParameter.keys())
            {
                if(!"entity.key".equals(param))
                    operation.get(param).set(inputParameter.get(param));
            }

            final String label = operation.get(ADDRESS).asString();

            //System.out.println(operation);

            // the actual invocation
            dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
                @Override
                public void onSuccess(DMRResponse dmrResponse) {
                    ModelNode response = dmrResponse.get();

                    String msg = "Operation " +operationName+ " on " + label;

                    if(response.isFailure())
                    {
                        Console.error(Console.MESSAGES.failed(msg), response.getFailureDescription());
                    }
                    else
                    {
                        Console.info(Console.MESSAGES.successful(msg));

                        clearReset(context);

                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                window.hide();
                            }
                        });

                    }
                }
            });

        }


    }

    class Delegation implements ModelDrivenCommand {

        private Command delegate;

        Delegation(Command delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Dialog dialog, Object data) {
            delegate.execute();
        }
    }
}
