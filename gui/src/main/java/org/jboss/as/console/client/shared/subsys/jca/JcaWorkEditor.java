package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaWorkEditor {

    private ListDataProvider<JcaWorkmanager> dataProvider;

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
                .addDetail("Long Running Threads", new HTML())
                .addDetail("Short Running Threads", new HTML())
                .build();

        return panel;
    }
}
