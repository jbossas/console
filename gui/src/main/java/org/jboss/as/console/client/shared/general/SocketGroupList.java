package org.jboss.as.console.client.shared.general;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/17/12
 */
public class SocketGroupList {
    
    private SocketBindingPresenter presenter;
    private CellTable<String> table;
    private ListDataProvider<String> dataProvider;
    private String token;

    public SocketGroupList(SocketBindingPresenter presenter, String token) {
        this.presenter = presenter;
        this.token = token;
    }

    public Widget asWidget() {

        table = new DefaultCellTable<String>(5);
        dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(table);

        TextColumn<String> nameColumn = new TextColumn<String>() {
            @Override
            public String getValue(String record) {
                return record;
            }
        };

        Column<String, String> option = new Column<String, String>(
                new TextLinkCell<String>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<String>() {
                    @Override
                    public void execute(String selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(token).with("name", selection)
                        );
                    }
                })
        ) {
            @Override
            public String getValue(String manager) {
                return manager;
            }
        };

        table.addColumn(nameColumn, "Name");
        table.addColumn(option, "Option");

        table.setSelectionModel(new SingleSelectionModel<String>());

        // ----
        SimpleLayout layoutBuilder = new SimpleLayout()
                .setPlain(true)
                .setHeadline("Socket Binding Groups")
                .setDescription("Please chose a group from below for specific settings.")
                .addContent(Console.MESSAGES.available("Groups"), table);

        return layoutBuilder.build();
    }


    private String getCurrentSelection() {
        String selection = ((SingleSelectionModel<String>) table.getSelectionModel()).getSelectedObject();
        return selection;
    }

    public void setGroups(List<String> adapters) {
        dataProvider.setList(adapters);

        if(!adapters.isEmpty())
            table.getSelectionModel().setSelected(adapters.get(0), true);

    }
}
