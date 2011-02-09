package org.jboss.as.console.client.server.subsys;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.RowEditorExitEvent;
import com.smartgwt.client.widgets.grid.events.RowEditorExitHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.jboss.as.console.client.components.DescriptionLabel;
import org.jboss.as.console.client.components.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementViewImpl extends ViewImpl implements ThreadManagementPresenter.MyView {

    ThreadManagementPresenter presenter;

    @Override
    public void setPresenter(ThreadManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setMembersMargin(5);

        TitleBar titleBar = new TitleBar("Thread Management");
        layout.addMember(titleBar);
        // TODO: text should be retrieved from model description
        layout.addMember(new DescriptionLabel("The threading subsystem, used to declare manageable thread pools and resources."));

        final TabSet topTabSet = new TabSet();
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setWidth100();
        topTabSet.setHeight100();

        Tab tTab1 = new Tab("Thread Factories");
        VLayout t1Layout = new VLayout();

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

        t1Layout.addMember(toolStrip);
        toolStrip.addSeparator();

        ToolStripButton helpButton = new ToolStripButton();
        helpButton.setIcon("[SKIN]/actions/help.png");
        //helpButton.setTitle("Help");
        toolStrip.addButton(helpButton);
        helpButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window help = new Window();
                help.setTitle("Help");
                help.setAutoSize(true);
                help.setAutoCenter(true);

                // TODO: text should be retrieved from model description
                HTMLFlow htmlFlow = new HTMLFlow("<pre>A thread factory (implementing java.util.concurrent.ThreadFactory).  The \"name\" attribute is\n" +
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
                        "   %G - emit the thread group name</pre>");

                htmlFlow.setMargin(10);
                help.addItem(htmlFlow);

                help.centerInPage();
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
        t1Layout.addMember(factoriesGrid);
        tTab1.setPane(t1Layout);

        // ---------------------------------------
        Tab tTab2 = new Tab("Unbounded Pools");
        //tTab2.setPane(tImg2);

        Tab tTab3 = new Tab("Bounded Pools");

        Tab tTab4 = new Tab("Queueless Pools");

        Tab tTab5 = new Tab("Scheduled Pools");

        topTabSet.addTab(tTab1);
        topTabSet.addTab(tTab2);
        topTabSet.addTab(tTab3);
        topTabSet.addTab(tTab4);
        topTabSet.addTab(tTab5);

        layout.addMember(topTabSet);
        return layout;
    }
}