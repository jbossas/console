package org.jboss.as.console.client.domain.hosts;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.domain.model.Server;
import org.jboss.as.console.client.domain.model.ServerGroupRecord;
import org.jboss.as.console.client.shared.PropertyTable;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.Feedback;
import org.jboss.as.console.client.widgets.TitleBar;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.ComboBoxItem;
import org.jboss.as.console.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class ServerView extends SuspendableViewImpl implements ServerPresenter.MyView{


    private ServerPresenter presenter;
    private Form<Server> form;
    private ContentHeaderLabel nameLabel;
    private ComboBoxItem groupItem;
    private ComboBoxItem socketItem;
    private ComboBoxItem jvmItem;

    private LayoutPanel layout;

    @Override
    public void setPresenter(ServerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        layout = new LayoutPanel();

        TitleBar titleBar = new TitleBar("Server Configuration");
        layout.add(titleBar);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("fill-layout-width");
        panel.getElement().setAttribute("style", "padding:15px;");

        layout.add(panel);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
        layout.setWidgetTopHeight(panel, 35, Style.Unit.PX, 100, Style.Unit.PCT);


        // --------------------------------------------------------

        final ToolStrip toolStrip = new ToolStrip();
        final ToolButton edit = new ToolButton("Edit");
        edit.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                if(edit.getText().equals("Edit"))
                {

                }
                else
                {

                }
            }
        });

        toolStrip.addToolButton(edit);
        ToolButton delete = new ToolButton("Delete");
        delete.addClickHandler(new ClickHandler(){
            @Override
            public void onClick(ClickEvent clickEvent) {
                Feedback.confirm(
                        "Delete Server Configuration",
                        "Do you want to delete this server configuration?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {

                            }
                        });
            }
        });
        toolStrip.addToolButton(delete);

        nameLabel = new ContentHeaderLabel("Name here ...");
        nameLabel.setIcon("common/server_group.png");

        HorizontalPanel horzPanel = new HorizontalPanel();
        horzPanel.getElement().setAttribute("style", "width:100%;");
        horzPanel.add(new Image(Icons.INSTANCE.server()));
        horzPanel.add(nameLabel);
        horzPanel.add(toolStrip);
        toolStrip.getElement().getParentElement().setAttribute("width", "50%");

        panel.add(horzPanel);

        // ----------------------------------------------------------------------


        panel.add(new ContentGroupLabel("Attributes"));

        form = new Form<Server>(Server.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Server Name");
        CheckBoxItem startedItem = new CheckBoxItem("started", "Start Instances?");
        groupItem = new ComboBoxItem("group", "Server Group");


        // ------------------------------------------------------

        socketItem = new ComboBoxItem("socketBinding", "Socket Binding");

        jvmItem = new ComboBoxItem("jvm", "Virtual Machine");

        form.setFields(nameItem, startedItem, groupItem);
        form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                socketItem, jvmItem
        );

        panel.add(form.asWidget());

        // ------------------------------------------------------

        panel.add(new ContentGroupLabel("System Properties"));

        PropertyTable properties = new PropertyTable();
        panel.add(properties);

        return layout;
    }

    @Override
    public void setSelectedRecord(Server selectedRecord) {
        nameLabel.setText(selectedRecord.getName());
        form.edit(selectedRecord);
    }

    @Override
    public void updateServerGroups(List<ServerGroupRecord> serverGroupRecords) {

        String[] names = new String[serverGroupRecords.size()];
        int i=0;
        for(ServerGroupRecord group : serverGroupRecords)
        {
            names[i] = group.getGroupName();
            i++;
        }
        groupItem.setValueMap(names);
    }

    @Override
    public void updateSocketBindings(List<String> result) {
        socketItem.setValueMap(result);
    }

    @Override
    public void updateVirtualMachines(List<String> result) {
        jvmItem.setValueMap(result);
    }
}
