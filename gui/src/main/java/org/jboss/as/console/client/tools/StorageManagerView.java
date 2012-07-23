package org.jboss.as.console.client.tools;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.widgets.lists.DefaultCellList;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/23/12
 */
public class StorageManagerView {

    private StoragePresenter presenter;
    private DefaultCellTable<FXTemplate> table;
    private ListDataProvider<FXTemplate> dataProvider;

    public StorageManagerView() {

    }

    public void setPresenter(StoragePresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");
        layout.getElement().setAttribute("style", "padding:15px");

        table = new DefaultCellTable<FXTemplate>(8,
                new ProvidesKey<FXTemplate>() {
                    @Override
                    public Object getKey(FXTemplate fxTemplate) {
                        return fxTemplate.getId();
                    }
                });

        dataProvider = new ListDataProvider<FXTemplate>();
        dataProvider.addDataDisplay(table);

        final SingleSelectionModel<FXTemplate> selectionModel = new SingleSelectionModel<FXTemplate>();
        table.setSelectionModel(selectionModel);

        TextColumn<FXTemplate> idCol = new TextColumn<FXTemplate>() {
            @Override
            public String getValue(FXTemplate fxTemplate) {
                return fxTemplate.getId();
            }

        };

        TextColumn<FXTemplate> nameCol = new TextColumn<FXTemplate>() {
            @Override
            public String getValue(FXTemplate fxTemplate) {
                return fxTemplate.getName();
            }
        };

        table.addColumn(idCol, "ID");
        table.addColumn(nameCol, "Name");


        ToolStrip toolstrip = new ToolStrip();
        ToolButton addBtn = new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.launchNewTemplateWizard();
            }
        });

        ToolButton removeBtn = new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final FXTemplate selectedObject = selectionModel.getSelectedObject();
                presenter.onRemoveTemplate(selectedObject.getId());

            }
        });

        toolstrip.addToolButtonRight(addBtn);
        toolstrip.addToolButtonRight(removeBtn);

        layout.add(toolstrip);
        layout.add(table);

        return layout;
    }

    public void setTemplates(Set<FXTemplate> fxTemplates) {
        dataProvider.getList().clear();
        dataProvider.getList().addAll(fxTemplates);
    }
}