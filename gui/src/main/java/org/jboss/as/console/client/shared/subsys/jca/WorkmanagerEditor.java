package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class WorkmanagerEditor {

    private WorkmanagerPresenter presenter;
    private ListDataProvider<JcaWorkmanager> dataProvider;

    public void setPresenter(WorkmanagerPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        DefaultCellTable<JcaWorkmanager> table = new DefaultCellTable<JcaWorkmanager>(10);
        dataProvider = new ListDataProvider<JcaWorkmanager>();
        dataProvider.addDataDisplay(table);

        TextColumn<JcaWorkmanager> name = new TextColumn<JcaWorkmanager>() {
            @Override
            public String getValue(JcaWorkmanager record) {
                return record.getName();
            }
        };

        table.addColumn(name, "Name");
        table.addColumn(name, "Num Short Running");
        table.addColumn(name, "Num Long Running");
        table.addColumn(name, "Option");

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
}
