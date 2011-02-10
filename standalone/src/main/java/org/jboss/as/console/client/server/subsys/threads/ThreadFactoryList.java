package org.jboss.as.console.client.server.subsys.threads;

import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.RowEditorExitEvent;
import com.smartgwt.client.widgets.grid.events.RowEditorExitHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.jboss.as.console.client.components.sgwt.HelpWindow;

/**
 * @author Heiko Braun
 * @date 2/10/11
 */
class ThreadFactoryList extends VLayout {

    public ThreadFactoryList(final ThreadManagementPresenter presenter) {

        ToolStrip toolStrip = new ToolStrip();
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



        factoriesGrid.setWidth100();
        factoriesGrid.setHeight100();
        factoriesGrid.setShowAllRecords(true);

        ListGridField nameField = new ListGridField("name", "Name");
        ListGridField groupField = new ListGridField("group", "Group");
        ListGridField prioField = new ListGridField("prio", "Priority");

        factoriesGrid.setFields(nameField, groupField, prioField);
        factoriesGrid.setCanResizeFields(true);
        factoriesGrid.setCanEdit(true);
        factoriesGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);

        factoriesGrid.setData(presenter.getFactoryRecords());

        factoriesGrid.addRowEditorExitHandler(new RowEditorExitHandler()
        {
            @Override
            public void onRowEditorExit(RowEditorExitEvent event) {

                ThreadFactoryRecord record = null;
                if(event.getRecord() instanceof ThreadFactoryRecord)
                    record = (ThreadFactoryRecord)event.getRecord();
                else
                {
                    record = new ThreadFactoryRecord();
                    record.fromValues(event.getNewValues());
                }

                presenter.onUpdateRecord(record);

            }
        });

        this.addMember(factoriesGrid);
    }
}
