package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.ButtonCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class WorkmanagerListView {

    private JcaPresenter presenter;
    private ListDataProvider<JcaWorkmanager> dataProvider;

    public WorkmanagerListView(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        DefaultCellTable<JcaWorkmanager> table = new DefaultCellTable<JcaWorkmanager>(10);
        dataProvider = new ListDataProvider<JcaWorkmanager>();
        dataProvider.addDataDisplay(table);
        table.setSelectionModel(new SingleSelectionModel<JcaWorkmanager>());

        TextColumn<JcaWorkmanager> name = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return record.getName();
            }
        };

        TextColumn<JcaWorkmanager> numShort = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return String.valueOf(record.getShortRunning().size());
            }
        };

        TextColumn<JcaWorkmanager> numLong = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return String.valueOf(record.getLongRunning().size());
            }
        };

        Column<JcaWorkmanager, JcaWorkmanager> option = new Column<JcaWorkmanager, JcaWorkmanager>(
                new ButtonCell<JcaWorkmanager>("Edit >", new ActionCell.Delegate<JcaWorkmanager>() {
                    @Override
                    public void execute(JcaWorkmanager selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JcaWorkManager).with("name", selection.getName())
                        );
                    }
                })
        ) {
            @Override
            public JcaWorkmanager getValue(JcaWorkmanager manager) {
                return manager;
            }
        };

        table.addColumn(name, "Name");
        table.addColumn(numShort, "Num Short Running");
        table.addColumn(numLong, "Num Long Running");
        table.addColumn(option, "Option");

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton("Remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO
            }
        }));

        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Workmanager")
                .setHeadline("Workmanager Configuration")
                .setDescription("Work manager for resource adapters.")
                .setMaster("Configured Workmanager", table)
                .setTopLevelTools(topLevelTools.asWidget())
                .build();

        return panel;
    }

    public void setManagers(List<JcaWorkmanager> managers) {
        dataProvider.setList(managers);
    }
}
