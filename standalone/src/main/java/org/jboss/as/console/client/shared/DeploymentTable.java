package org.jboss.as.console.client.shared;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.domain.groups.PropertyRecord;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 2/22/11
 */
public class DeploymentTable extends DefaultCellTable<DeploymentRecord>{

    public DeploymentTable() {
        super(10);

        TextColumn<DeploymentRecord> dplNameColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getName();
            }
        };

        TextColumn<DeploymentRecord> dplRuntimeColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getRuntimeName();
            }
        };

        TextColumn<DeploymentRecord> dplShaColumn = new TextColumn<DeploymentRecord>() {
            @Override
            public String getValue(DeploymentRecord record) {
                return record.getSha();
            }
        };

        addColumn(dplNameColumn, "Name");
        addColumn(dplRuntimeColumn, "Runtime Name");
        addColumn(dplShaColumn, "Sha");

        // just an example
        final SingleSelectionModel<DeploymentRecord> selectionModel = new SingleSelectionModel<DeploymentRecord>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                DeploymentRecord record = selectionModel.getSelectedObject();
                //System.out.println("selected: "+record.getName());
            }
        });

        setSelectionModel(selectionModel);
    }
}
