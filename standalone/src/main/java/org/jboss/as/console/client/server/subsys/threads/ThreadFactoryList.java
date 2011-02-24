package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import org.jboss.as.console.client.shared.tables.DefaultCellTable;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
class ThreadFactoryList extends LayoutPanel {

    public ThreadFactoryList(final ThreadManagementPresenter presenter) {

        DefaultCellTable factoryTable = new DefaultCellTable(10);

        TextColumn<ThreadFactoryRecord> nameColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getName();
            }
        };

        TextColumn<ThreadFactoryRecord> groupColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return record.getGroup();
            }
        };

        TextColumn<ThreadFactoryRecord> prioColumn = new TextColumn<ThreadFactoryRecord>() {
            @Override
            public String getValue(ThreadFactoryRecord record) {
                return String.valueOf(record.getPriority());
            }
        };

        factoryTable.addColumn(nameColumn, "Factory Name");
        factoryTable.addColumn(groupColumn, "Group");
        factoryTable.addColumn(prioColumn, "Priority");

       /* ToolStrip toolStrip = new ToolStrip();
        toolStrip.setWidth100();

        final ListGrid factoriesGrid = new ListGrid();

        ToolStripButton addButton = new ToolStripButton();
        addButton.setTitle("Add");
        toolStrip.addButton(addButton);
        addButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                factoriesGrid.startEditingNew();
            }
        });

        this.addMember(toolStrip);
        toolStrip.addSeparator();

        ToolStripButton helpButton = new ToolStripButton();
        helpButton.setIcon("[SKIN]/actions/help.png");
        //helpButton.setTitle("Help");
        toolStrip.addButton(helpButton);
        helpButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {

                // TODO: text should come from model description
                String text = "A thread factory (implementing java.util.concurrent.ThreadFactory).  The \"name\" attribute is\n" +
                        "the bean name of the created thread factory.  The optional \"priority\" attribute may be used to specify\n" +
                        "the thread priority of created threads.  The optional \"group-name\" attribute specifies the name of a the\n" +
                        "thread group to create for this thread factory.\n" +
                        "\n" +
                        "The \"thread-name-pattern\" is the template used to create names for threads.  The following patterns\n" +
                        "may be used:\n" +
                        "\n" +
                        "   %% - emit a percent sign\n" +
                        "   %t - emit the per-factory thread sequence number\n" +
                        "   %g - emit the global thread sequence number\n" +
                        "   %f - emit the factory sequence number\n" +
                        "   %i - emit the thread ID\n" +
                        "   %G - emit the thread group name";

                Window help = new HelpWindow(text);
                help.show();
            }
        });



        */


        add(factoryTable);
        setWidgetTopHeight(factoryTable, 0, Style.Unit.PX, 100, Style.Unit.PCT);
    }
}
