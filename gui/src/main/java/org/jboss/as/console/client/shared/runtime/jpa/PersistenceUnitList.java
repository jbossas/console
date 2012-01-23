package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class PersistenceUnitList {


    private CellTable<JPADeployment> table;
    private ListDataProvider<JPADeployment> dataProvider;

    private JPAMetricPresenter presenter;

    public PersistenceUnitList(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        table = new DefaultCellTable<JPADeployment>(8);

        TextColumn<JPADeployment> name = new TextColumn<JPADeployment>() {

            @Override
            public String getValue(JPADeployment record) {
                return record.getDeploymentName();
            }
        };

        TextColumn<JPADeployment> unit = new TextColumn<JPADeployment>() {

            @Override
            public String getValue(JPADeployment record) {
                return record.getPersistenceUnit();
            }
        };

        Column<JPADeployment, ImageResource> statusColumn =
                new Column<JPADeployment, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(JPADeployment jpa) {

                        ImageResource res = null;

                        if(jpa.isMetricEnabled())
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };


        Column<JPADeployment, JPADeployment> option = new Column<JPADeployment, JPADeployment>(
                new TextLinkCell<JPADeployment>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<JPADeployment>() {
                    @Override
                    public void execute(JPADeployment selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JPAMetricPresenter)
                                        .with("dpl", selection.getDeploymentName())
                                        .with("unit", selection.getPersistenceUnit())
                        );
                    }
                })
        ) {
            @Override
            public JPADeployment getValue(JPADeployment manager) {
                return manager;
            }
        };


        table.addColumn(unit, "Persistence Unit");
        table.addColumn(name, "Deployment");
        table.addColumn(statusColumn, "Enabled?");
        table.addColumn(option, "Option");

        table.setSelectionModel(new SingleSelectionModel<JPADeployment>());

        dataProvider = new ListDataProvider<JPADeployment>();
        dataProvider.addDataDisplay(table);

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("JPA Metrics")
                .setHeadline("Persistence Units")
                .setDescription("DESCRIPTION")
                .setMaster(Console.MESSAGES.available("Persistence Units"), table);


        return layout.build();

    }

    public void setUnits(List<JPADeployment> jpaUnits) {
        dataProvider.setList(jpaUnits);
    }
}
