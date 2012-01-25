package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class PersistenceUnitList {


    private DefaultCellTable<JPADeployment> table;
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
        table.addColumn(statusColumn, "Metrics Enabled?");
        table.addColumn(option, "Option");

        table.setSelectionModel(
                new SingleSelectionModel<JPADeployment>(

                        // TODO: https://issues.jboss.org/browse/AS7-3441
                        /*new ProvidesKey<JPADeployment>() {
                            @Override
                            public Object getKey(JPADeployment item) {
                                return item.getDeploymentName()+"#"+item.getPersistenceUnit();
                            }
                        } */
                )
        );

        dataProvider = new ListDataProvider<JPADeployment>();
        dataProvider.addDataDisplay(table);


        // ---


        final Form<JPADeployment> form = new Form<JPADeployment>(JPADeployment.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        TextItem deployment = new TextItem("deploymentName", "Deployment");
        TextItem persistenceUnit = new TextItem("persistenceUnit", "Unit");
        CheckBoxItem enabledField = new CheckBoxItem("metricEnabled", "Metrics Enabled?");

        form.setFields(deployment, persistenceUnit, enabledField);


         final StaticHelpPanel helpPanel = new StaticHelpPanel(
                 Console.CONSTANTS.subsys_jpa_deployment_desc()
         );

        form.bind(table);



        FormToolStrip<JPADeployment> formTools = new FormToolStrip<JPADeployment>(
                form, new FormToolStrip.FormCallback<JPADeployment>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveJPADeployment(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JPADeployment entity) {
                // not provided
            }
        }
        );
        formTools.providesDeleteOp(false);

        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(formTools.asWidget());
        formPanel.add(helpPanel.asWidget());
        formPanel.add(form.asWidget());

        // ---

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("JPA Metrics")
                .setHeadline("Persistence Units")
                .setDescription(Console.CONSTANTS.subsys_jpa_puList_desc())
                .setMaster(Console.MESSAGES.available("Persistence Units"), table)
                .addDetail("Persistence Unit", formPanel);


        return layout.build();

    }

    public void setUnits(List<JPADeployment> jpaUnits) {
        dataProvider.setList(jpaUnits);

        //table.defaultSelectEntity();
        if(!jpaUnits.isEmpty())
            table.getSelectionModel().setSelected(jpaUnits.get(0), true);
    }
}
