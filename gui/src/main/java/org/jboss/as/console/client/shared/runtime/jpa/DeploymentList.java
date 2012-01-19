package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.model.DeploymentRecord;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class DeploymentList {


    private CellTable<DeploymentRecord> table;
    private JPAMetricPresenter presenter;

    public DeploymentList(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        table = new DefaultCellTable<DeploymentRecord>(8);

        TextColumn<DeploymentRecord> name = new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        };

        TextColumn<DeploymentRecord> runtimeName = new TextColumn<DeploymentRecord>() {

            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        };

        Column<DeploymentRecord, DeploymentRecord> option = new Column<DeploymentRecord, DeploymentRecord>(
                new TextLinkCell<DeploymentRecord>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<DeploymentRecord>() {
                    @Override
                    public void execute(DeploymentRecord selection) {
                        /*presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JcaPresenter).with("name", selection.getName())
                        );*/
                    }
                })
        ) {
            @Override
            public DeploymentRecord getValue(DeploymentRecord manager) {
                return manager;
            }
        };

        table.addColumn(name, "Name");
        table.addColumn(runtimeName, "Runtime Name");
        table.addColumn(option, "Option");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("JPA Metrics")
                .setHeadline("JPA Deployments")
                .setDescription(Console.MESSAGES.available("JPA Deployments"))
                .setMaster("Deploment List", table);


        return layout.build();

    }
}
