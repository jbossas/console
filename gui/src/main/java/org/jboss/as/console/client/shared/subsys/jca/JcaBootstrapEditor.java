package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaBootstrapEditor {

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


        Form<JcaBootstrapContext> form = new Form<JcaBootstrapContext>(JcaBootstrapContext.class);

        TextItem contextName = new TextItem("name", "Name");
        ComboBoxItem workmanager = new ComboBoxItem("workmanger", "Workmanager");

        form.setFields(contextName, workmanager);
        form.setNumColumns(2);

        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Boostrap")
                .setHeadline("JCA Boostrap Contexts")
                .setDescription("Bootstrap context for resource adapters.")
                .setMaster("Configured Contexts", table)
                .setTopLevelTools(topLevelTools.asWidget())
                .setDetail("Bootstrap Context", form.asWidget())
                .build();

        return panel;
    }
}
