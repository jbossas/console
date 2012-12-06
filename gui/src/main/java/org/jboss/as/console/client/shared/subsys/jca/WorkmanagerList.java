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
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.as.console.client.widgets.tables.ViewLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 12/6/11
 */
public class WorkmanagerList {

    private JcaPresenter presenter;
    private ListDataProvider<JcaWorkmanager> dataProvider;

    public WorkmanagerList(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        final DefaultCellTable<JcaWorkmanager> table = new DefaultCellTable<JcaWorkmanager>(10);
        dataProvider = new ListDataProvider<JcaWorkmanager>();
        dataProvider.addDataDisplay(table);
        table.setSelectionModel(new SingleSelectionModel<JcaWorkmanager>());

        TextColumn<JcaWorkmanager> name = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return record.getName();
            }
        };

        Column<JcaWorkmanager, JcaWorkmanager> option = new Column<JcaWorkmanager, JcaWorkmanager>(
                new ViewLinkCell<JcaWorkmanager>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<JcaWorkmanager>() {
                    @Override
                    public void execute(JcaWorkmanager selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JcaPresenter).with("name", selection.getName())
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
        table.addColumn(option, "Option");

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewManagerDialogue();
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Work Manager"),
                        Console.MESSAGES.deleteConfirm("Work Manager"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    SingleSelectionModel<JcaWorkmanager> selectionModel = (SingleSelectionModel<JcaWorkmanager>) table.getSelectionModel();
                                    presenter.onDeleteManager(selectionModel.getSelectedObject());
                                }
                            }
                        });

            }
        }));

        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Work Manager")
                .setHeadline("JCA Workmanager")
                .setDescription(Console.CONSTANTS.subsys_jca_workmanager_config_desc())
                .setMaster(Console.MESSAGES.available("Work Manager"), table)
                .setMasterTools(topLevelTools.asWidget())
                .build();

        return panel;

    }

    public void setManagers(List<JcaWorkmanager> managers) {
        this.dataProvider.setList(managers);
    }
}
