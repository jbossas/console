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

package org.jboss.as.console.client.shared.viewframework;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.EnumSet;
import java.util.List;

/**
 * The editor that allows CRUD operations on an Entity.  This includes an Entity table, add button, and
 * EntityDetails editor.
 *
 * @author Stan Silvert
 * @author Heiko Braun
 */
public class EntityEditor<T> implements EntityListView<T> {
    private String entitiesName;
    private EntityPopupWindow<T> window;
    private ListDataProvider<T> dataProvider;
    private DefaultCellTable<T> table;
    private EntityDetails<T> details;
    private boolean doneInitialSelection = false;
    private DefaultPager pager;
    private EnumSet<FrameworkButton> hideButtons;
    private ToolStrip toolStrip;
    private String description = null;

    private boolean includeTools = true;
    private FrameworkPresenter presenter;
    private ToolStrip customTools = null;

    /**
     * Create a new Entity.
     *
     * @param entitiesName The display name (plural) of the entities.
     * @param window The window used for creating a new entity.
     * @param table The table that holds the entities.
     * @param details  The EntityDetails that manages CRUD for the selected entity.
     */
    public EntityEditor(FrameworkPresenter presenter, String entitiesName, EntityPopupWindow<T> window, DefaultCellTable<T> table, EntityDetails<T> details) {
        this(presenter, entitiesName, window, table, details, EnumSet.noneOf(FrameworkButton.class));
    }

    /**
     * Create a new Entity.
     *
     * @param entitiesName The display name (plural) of the entities.
     * @param window The window used for creating a new entity.
     * @param table The table that holds the entities.
     * @param entityDetails  The EntityDetails that manages CRUD for the selected entity.
     */
    public EntityEditor(
            FrameworkPresenter presenter,
            String entitiesName,
            EntityPopupWindow<T> window,
            DefaultCellTable<T> table,
            EntityDetails<T> entityDetails,
            EnumSet<FrameworkButton> hideButtons) {

        this.presenter = presenter;
        this.entitiesName = entitiesName;
        this.window = window;
        this.table = table;
        this.details = entityDetails;
        this.hideButtons = hideButtons;

          // cleanup before editing
        table.addRowCountChangeHandler(new RowCountChangeEvent.Handler() {
            @Override
            public void onRowCountChange(RowCountChangeEvent event) {
                if(event.getNewRowCount()==0 && event.isNewRowCountExact())
                    details.clearValues();

            }
        });
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ToolStrip getToolStrip() {
        return toolStrip;
    }

    public EntityEditor<T> setIncludeTools(boolean includeTools) {
        this.includeTools = includeTools;
        return this;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout"); // FF hack

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");

        panel.add(new ContentHeaderLabel(entitiesName));
        if(description!=null)
            panel.add(new ContentDescription(description));

        final SingleSelectionModel<T> selectionModel = new SingleSelectionModel<T>();
        table.setSelectionModel(selectionModel);

        // update the detail upon change
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                T selectedEntity = selectionModel.getSelectedObject();
                details.updatedEntity(selectedEntity);
            }
        });

        dataProvider = new ListDataProvider<T>();
        dataProvider.addDataDisplay(table);

        if(customTools!=null)
        {
             panel.add(customTools);
        }
        else if(includeTools)
        {
            toolStrip = createTools();
            if(toolStrip.hasButtons())
                panel.add(toolStrip);
        }

        panel.add(table);

        pager = new DefaultPager();
        pager.setDisplay(table);
        if (table.isVisible())
            panel.add(pager);

        panel.add(new ContentGroupLabel(Console.CONSTANTS.common_label_details()));
        panel.add(details.asWidget());

        layout.add(panel);

        return layout;
    }

    public ToolStrip createTools() {
        final ToolStrip toolStrip = new ToolStrip();

        if (!hideButtons.contains(FrameworkButton.ADD)) {
            ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(),
                    new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            window.setNewBean();
                            window.show();
                        }
                    });
            addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_entityEditor());        
            toolStrip.addToolButtonRight(addBtn);
        }

        if (!hideButtons.contains(FrameworkButton.REMOVE)) {
            ToolButton removeBtn=new ToolButton(Console.CONSTANTS.common_label_remove(),
                    new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {

                            String name = Console.CONSTANTS.common_label_item();
                            Feedback.confirm(
                                    Console.MESSAGES.deleteTitle(name),
                                    Console.MESSAGES.deleteConfirm(name),
                                    new Feedback.ConfirmationHandler() {
                                        @Override
                                        public void onConfirmation(boolean isConfirmed) {
                                            if (isConfirmed) {
                                                presenter.getEntityBridge().onRemove(
                                                        ((SingleSelectionModel)table.getSelectionModel()).getSelectedObject()
                                                );
                                            }
                                        }
                                    });
                        }
                    });
            removeBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_remove_entityEditor());
            toolStrip.addToolButtonRight(removeBtn);
        }

        return toolStrip;
    }

    @Override
    public ListDataProvider<T> getDataProvider() {
        return dataProvider;
    }

    @Override
    public void updateEntityList(List<T> entityList, T lastEdited) {
        // cannot do dataProvider.setList(entityList) as this breaks any sorting
        List<T> list = dataProvider.getList();
        list.clear();
        list.addAll(entityList);
        dataProvider.flush();



        if (table.isEmpty()) return;

        if (!doneInitialSelection) {
            setSelected(entityList.get(0));
            return;
        }

        if(lastEdited == null) {
            setSelected(entityList.get(0));
            return;
        }

        setSelected(lastEdited);
    }

    private void setSelected(T entity) {
        table.getSelectionModel().setSelected(entity, true);
        doneInitialSelection = true;
        List<T> entities = dataProvider.getList();
        int position = entities.indexOf(entity);
        int page = position/table.getPageSize();
        pager.setPage(page);
    }

    public void setEditingEnabled(boolean isEnabled) {
        this.details.setEditingEnabled(isEnabled);
    }

    public void setTools(ToolStrip toolStrip) {
        this.customTools = toolStrip;
    }
}
