package org.jboss.mbui.gui.reification.strategy;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.InteractionEvent;
import org.jboss.mbui.gui.behaviour.PresentationEvent;
import org.jboss.mbui.gui.behaviour.StatementEvent;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.gui.behaviour.as7.GlobalQNames;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.as7.StereoTypes;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/19/13
 */
public class PullDownStrategy implements ReificationStrategy<ReificationWidget, StereoTypes> {

    private EventBus eventBus;

    @Override
    public boolean prepare(InteractionUnit<StereoTypes> interactionUnit, Context context) {
        eventBus = context.get(ContextKey.EVENTBUS);
        return eventBus!=null;
    }

    @Override
    public ReificationWidget reify(InteractionUnit<StereoTypes> interactionUnit, Context context) {
        return new PullDownAdapter(interactionUnit);
    }

    @Override
    public boolean appliesTo(InteractionUnit<StereoTypes> interactionUnit) {
        return StereoTypes.PullDown == interactionUnit.getStereotype();
    }

    class PullDownAdapter implements ReificationWidget {

        private InteractionUnit<StereoTypes> unit;
        private ListBox comboBox;
        private String previousSelection = null;

        PullDownAdapter(InteractionUnit<StereoTypes> unit) {
            this.unit = unit;
            this.comboBox = new ListBox(false) ;

            comboBox.addChangeHandler(new ChangeHandler(){

                @Override
                public void onChange(ChangeEvent changeEvent) {

                    // create statement
                    String selection = comboBox.getValue(comboBox.getSelectedIndex());

                    if (selection != null && !selection.equals("")) {
                        // create a select statement
                        eventBus.fireEventFromSource(
                                new StatementEvent(
                                        GlobalQNames.SELECT_ID,
                                        "selected.entity",
                                        selection),   // synthetic key (convention), see LoadResourceProcedure
                                getInteractionUnit().getId());

                        previousSelection = selection;

                    } else {
                        // clear the select statement
                        eventBus.fireEventFromSource(
                                new StatementEvent(
                                        GlobalQNames.SELECT_ID,
                                        "selected.entity",
                                        null),
                                getInteractionUnit().getId());

                         previousSelection =null;
                    }
                }
            });


            // handle resets within this scope
            eventBus.addHandler(SystemEvent.TYPE, new SystemEvent.Handler() {
                @Override
                public boolean accepts(SystemEvent event) {

                    return event.getId().equals(GlobalQNames.RESET_ID);
                }

                @Override
                public void onSystemEvent(SystemEvent event) {
                    comboBox.clear();

                    // request loading of data
                    InteractionEvent reset = new InteractionEvent(GlobalQNames.LOAD_ID);

                    // update interaction units
                    eventBus.fireEventFromSource(
                            reset,
                            getInteractionUnit().getId()
                    );
                }
            });

            // handle the results of function calls
            eventBus.addHandler(PresentationEvent.TYPE, new PresentationEvent.PresentationHandler()
            {
                @Override
                public boolean accepts(PresentationEvent event) {
                    boolean matchingType = event.getPayload() instanceof List;
                    boolean matchingTarget = event.getTarget().equals(getInteractionUnit().getId());
                    return matchingTarget && matchingType;
                }

                @Override
                public void onPresentationEvent(PresentationEvent event) {
                    List<ModelNode> entities = (List<ModelNode>)event.getPayload();
                    comboBox.clear();

                    // initial state
                    comboBox.addItem("");
                    comboBox.setItemText(0, "<<Please select>>");

                    for(ModelNode item : entities)
                    {
                        String key = item.get("entity.key").asString();  // synthetic key
                        comboBox.addItem(key);

                        if(key.equals(previousSelection))
                            comboBox.setItemSelected(comboBox.getItemCount() - 1, true);

                    }

                    // fallback to default selection
                    if(comboBox.getSelectedIndex()<=0 && comboBox.getItemCount()>0)
                        comboBox.setItemSelected(0, true);

                }
            });

            // Register inputs & outputs

            getInteractionUnit().setInputs(
                    new Resource<ResourceType>(GlobalQNames.RESET_ID, ResourceType.System),
                    new Resource<ResourceType>(getInteractionUnit().getId(), ResourceType.Presentation)
            );

            getInteractionUnit().setOutputs(
                    new Resource<ResourceType>(GlobalQNames.LOAD_ID, ResourceType.Interaction),
                    new Resource<ResourceType>(GlobalQNames.SELECT_ID, ResourceType.Statement)
            );

        }

        @Override
        public InteractionUnit<StereoTypes> getInteractionUnit() {
            return unit;
        }

        @Override
        public void add(ReificationWidget widget) {
            throw new UnsupportedOperationException("Should not be called on atomic unit");
        }

        @Override
        public Widget asWidget() {

            HorizontalPanel panel = new HorizontalPanel();
            panel.add(new ContentHeaderLabel(getInteractionUnit().getLabel()));
            Widget listBoxWidget = comboBox.asWidget();
            panel.add(listBoxWidget);

            listBoxWidget.getElement().setAttribute("style", "margin-left:10px");
            return panel;
        }
    }
}
