package org.jboss.as.console.client.domain.groups.deployment;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.deployment.DeploymentFilter;
import org.jboss.as.console.client.shared.deployment.model.DeploymentRecord;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @date 7/31/12
 */
public class AssignToGroupWizard {

    private DeploymentsPresenter presenter;
    private ListDataProvider<DeploymentRecord> dataProvider;
    private List<DeploymentRecord> availableDeployments;
    private ServerGroupRecord serverGroup;

    public AssignToGroupWizard(
            DeploymentsPresenter presenter,
            List<DeploymentRecord> availableDeployments,
            ServerGroupRecord serverGroup) {
        this.presenter = presenter;
        this.availableDeployments = availableDeployments;
        this.serverGroup = serverGroup;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.addStyleName("window-content");

        ProvidesKey<DeploymentRecord> key = new ProvidesKey<DeploymentRecord>() {
            @Override
            public Object getKey(DeploymentRecord o) {
                return o.getName();
            }
        };

        final DefaultCellTable<DeploymentRecord> table = new DefaultCellTable<DeploymentRecord>(8, key);
        dataProvider = new ListDataProvider<DeploymentRecord>();
        dataProvider.addDataDisplay(table);
        dataProvider.setList(availableDeployments);

        final MultiSelectionModel<DeploymentRecord> selectionModel =
                new MultiSelectionModel<DeploymentRecord>(key);
        table.setSelectionModel(selectionModel);

        table.setSelectionModel(selectionModel);
        TextColumn<DeploymentRecord> titleColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                String title = null;
                if(record.getRuntimeName().length()>37)
                    title = record.getRuntimeName().substring(0,36)+"...";
                else
                    title = record.getRuntimeName();
                return title;
            }
        };

        Column<DeploymentRecord, Boolean> checkBoxColumn =
                new Column<DeploymentRecord, Boolean>(new CheckboxCell()) {

                    @Override
                    public Boolean getValue(DeploymentRecord object) {
                        return selectionModel.isSelected(object);
                    }

                };

        table.setWidth("100%", true);
        table.addColumn(checkBoxColumn, "Assign");
        table.addColumn(titleColumn, "Name");

        table.setColumnWidth(checkBoxColumn, 10, Style.Unit.PCT);
        table.setColumnWidth(titleColumn, 90, Style.Unit.PCT);


        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);

        ToolStrip toolStrip = new ToolStrip();
        DeploymentFilter filter = new DeploymentFilter(dataProvider);
        toolStrip.addToolWidget(filter.asWidget());

        layout.add(new ContentHeaderLabel("Server Group: "+serverGroup.getName()));
        layout.add(new ContentGroupLabel("Available Deployment Content"));
        layout.add(toolStrip.asWidget());
        layout.add(table.asWidget());
        layout.add(pager);

        final HTML errorMessages = new HTML("Please select a deployment!");
        errorMessages.setStyleName("error-panel");
        errorMessages.setVisible(false);
        layout.add(errorMessages);

        DialogueOptions options = new DialogueOptions(
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        errorMessages.setVisible(false);

                        Set<DeploymentRecord> selectedSet = selectionModel.getSelectedSet();
                        if(selectedSet.isEmpty())
                        {
                            errorMessages.setVisible(true);
                        }
                        else
                        {
                            presenter.onAssignDeployments(serverGroup, selectedSet);
                        }
                    }
                },
                new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        errorMessages.setVisible(false);
                        presenter.closeDialogue();
                    }
                }
        );



        return new WindowContentBuilder(layout, options).build();

    }
}
