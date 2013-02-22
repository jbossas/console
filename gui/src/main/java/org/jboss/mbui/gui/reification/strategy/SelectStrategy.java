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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.dmr.client.ModelNode;
import org.jboss.mbui.gui.behaviour.InteractionEvent;
import org.jboss.mbui.gui.behaviour.PresentationEvent;
import org.jboss.mbui.gui.behaviour.StatementEvent;
import org.jboss.mbui.gui.behaviour.SystemEvent;
import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ContextKey;
import org.jboss.mbui.gui.reification.widgets.ModelNodeCellTable;
import org.jboss.mbui.model.behaviour.Resource;
import org.jboss.mbui.model.behaviour.ResourceType;
import org.jboss.mbui.model.mapping.MappingType;
import org.jboss.mbui.model.mapping.as7.ResourceAttribute;
import org.jboss.mbui.model.mapping.as7.ResourceMapping;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.QName;
import org.jboss.mbui.model.structure.Select;

import java.util.List;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 *
 * @date 11/01/2012
 */
public class SelectStrategy implements ReificationStrategy<ReificationWidget>
{

    final static QName SELECT_ID = QName.valueOf("org.jboss.as:select");
    final static QName RESET_ID = QName.valueOf("org.jboss.as:reset");
    final static QName LOAD_ID = QName.valueOf("org.jboss.as:load");

    @Override
    public ReificationWidget reify(final InteractionUnit interactionUnit, final Context context)
    {

        EventBus eventBus = context.get(ContextKey.EVENTBUS);
        assert eventBus!=null : "Coordinator bus is required to execute FormStrategy";

        ModelNodeCellTableAdapter adapter = null;
        if (interactionUnit != null)
        {
            adapter = new ModelNodeCellTableAdapter(interactionUnit, eventBus);
        }

        return adapter;
    }

    @Override
    public boolean appliesTo(final InteractionUnit interactionUnit)
    {
        return interactionUnit instanceof Select;
    }


    class ModelNodeCellTableAdapter implements ReificationWidget
    {
        final VerticalPanel panel;
        final ModelNodeCellTable table;
        final InteractionUnit interactionUnit;

        ModelNodeCellTableAdapter(final InteractionUnit interactionUnit, final EventBus coordinator)
        {
            this.panel = new VerticalPanel();
            this.table = new ModelNodeCellTable(5);
            this.interactionUnit = interactionUnit;

            ResourceMapping resourceMapping = (ResourceMapping)
                    this.interactionUnit.findMapping(MappingType.RESOURCE);

            List<ResourceAttribute> attributes = resourceMapping.getAttributes();
            for (ResourceAttribute attribute : attributes)
            {
                final String attributeKey = attribute.getLabel() != null ? attribute.getLabel() : attribute.getName();
                table.addColumn(new Column<ModelNode, String>(new TextCell())
                {
                    @Override
                    public String getValue(ModelNode model)
                    {
                        return model.get(attributeKey).asString();
                    }
                }, attributeKey);
            }

            final ListDataProvider<ModelNode> dataProvider = new ListDataProvider<ModelNode>();
            dataProvider.addDataDisplay(table);

            DefaultPager pager = new DefaultPager();
            pager.setDisplay(table);

            panel.setStyleName("fill-layout-width");
            panel.getElement().setAttribute("style", "padding-top:15px;");
            panel.add(table);
            panel.add(pager);


            final SingleSelectionModel<ModelNode> selectionModel = new SingleSelectionModel<ModelNode>();
            table.setSelectionModel(selectionModel);

            selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {

                    // create statement
                    ModelNode selection = selectionModel.getSelectedObject();


                    if(selection!=null) {
                        // create a select statement
                        coordinator.fireEventFromSource(
                                new StatementEvent(
                                        SELECT_ID,
                                        "selected.entity",
                                        selection.get("entity.key").asString()),   // synthetic key (convention), see LoadResourceProcedure
                                this);


                    }
                    else
                    {
                        // clear the select statement
                        coordinator.fireEventFromSource(
                                new StatementEvent(
                                        SELECT_ID,
                                        "selected.entity",
                                        null),
                                this);
                    }
                }
            });


              // handle resets within this scope
            coordinator.addHandler(SystemEvent.TYPE, new SystemEvent.Handler() {
                @Override
                public boolean accepts(SystemEvent event) {

                    return event.getId().equals(RESET_ID);
                }

                @Override
                public void onSystemEvent(SystemEvent event) {
                    dataProvider.getList().clear();
                    dataProvider.refresh();

                    // request loading of data
                    InteractionEvent reset = new InteractionEvent(LOAD_ID);

                    // update interaction units
                    coordinator.fireEventFromSource(
                            reset,
                            interactionUnit.getId()
                    );
                }
            });

            // handle the results of function calls
            coordinator.addHandler(PresentationEvent.TYPE, new PresentationEvent.PresentationHandler()
            {
                @Override
                public boolean accepts(PresentationEvent event) {
                    return (event.getPayload() instanceof List);
                }

                @Override
                public void onPresentationEvent(PresentationEvent event) {
                    List<ModelNode> entities = (List<ModelNode>)event.getPayload();
                    dataProvider.getList().clear();
                    dataProvider.getList().addAll(entities);
                }
            });

            // Register inputs & outputs

            getInteractionUnit().setInputs(
                    new Resource<ResourceType>(RESET_ID, ResourceType.System),
                    new Resource<ResourceType>(getInteractionUnit().getId(), ResourceType.Presentation)
                    );

            getInteractionUnit().setOutputs(
                    new Resource<ResourceType>(LOAD_ID, ResourceType.Event),
                    new Resource<ResourceType>(SELECT_ID, ResourceType.Statement)
            );

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
            return panel;
        }
    }
}
