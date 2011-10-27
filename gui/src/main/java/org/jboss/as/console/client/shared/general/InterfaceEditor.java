package org.jboss.as.console.client.shared.general;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.general.model.Interface;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 10/24/11
 */
public class InterfaceEditor {

    private CellTable<Interface> table;
    private String title;
    private String description = null;
    public InterfaceEditor(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Widget asWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(title);
        layout.add(titleBar);

        final Form<Interface> form = new Form<Interface>(Interface.class);

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                //presenter.launchNewInterfaceDialogue();
            }
        }));

        topLevelTools.addToolButtonRight(new ToolButton("Remove", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                /*final Interface editedEntity = form.getEditedEntity();
                Feedback.confirm("Remove Interface Declaration",
                        "Really remove interface " + editedEntity.getName() + "?",
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                    presenter.onRemoveInterface(editedEntity);
                            }
                        });

                presenter.onRemoveInterface(form.getEditedEntity());*/
            }
        }));


        layout.add(topLevelTools);

        // -----------
        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        panel.add(new ContentHeaderLabel("Interface Declarations"));

        if(description!=null)
            panel.add(new ContentDescription(description));

        table = new DefaultCellTable<Interface>(10);

        TextColumn<Interface> nameColumn = new TextColumn<Interface>() {
            @Override
            public String getValue(Interface record) {
                return record.getName();
            }
        };

        table.addColumn(nameColumn, "Name");

        panel.add(table);


        panel.add(new ContentGroupLabel("Interface"));

        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem inetAddress = new TextBoxItem("inetAddress", "InetAddress");
        TextBoxItem nic = new NonRequiredTextBoxItem("nic", "Nic");
        TextBoxItem nicMatch = new NonRequiredTextBoxItem("nicMatch", "Nic Match");

        CheckBoxItem publicAddress = new CheckBoxItem("publicAddress", "Public Address");
        CheckBoxItem siteLocalAddress = new CheckBoxItem("siteLocal", "Site Local Address");

        CheckBoxItem anyAddress = new CheckBoxItem("anyAddress", "Any Address");
        CheckBoxItem anyIP4Address = new CheckBoxItem("anyIP4Address", "Any IP4 Address");
        CheckBoxItem anyIP6Address = new CheckBoxItem("anyIP6Address", "Any IP4 Address");

        CheckBoxItem up = new CheckBoxItem("up", "Up");
        CheckBoxItem virtual = new CheckBoxItem("virtual", "Virtual");

        CheckBoxItem p2p = new CheckBoxItem("P2P", "Point to Point");
        CheckBoxItem multicast = new CheckBoxItem("multicast", "Multicast");
        CheckBoxItem loopback = new CheckBoxItem("loopback", "Loopback");
        TextBoxItem loopbackAddress = new NonRequiredTextBoxItem("loopbackAddress", "Loopback Address");

        form.setFields(nameItem, inetAddress, nic, nicMatch, publicAddress, siteLocalAddress, up, virtual);

        form.setFieldsInGroup(
                "Advanced",
                new DisclosureGroupRenderer(),
                anyAddress, anyIP4Address, anyIP6Address,
                p2p, multicast, loopback, loopbackAddress
        );


        FormToolStrip<Interface> toolstrip = new FormToolStrip<Interface>(
                form,
                new FormToolStrip.FormCallback<Interface>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                    }

                    @Override
                    public void onDelete(Interface entity) {

                    }
                });

        toolstrip.providesDeleteOp(false);

        form.bind(table);
        form.setEnabled(false);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = new ModelNode();
                address.add("interface", "*");
                return address;
            }
        }, form);

        panel.add(toolstrip.asWidget());
        panel.add(helpPanel.asWidget());
        panel.add(form.asWidget());


        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 26, Style.Unit.PX);
        layout.setWidgetTopHeight(topLevelTools, 26, Style.Unit.PX, 30, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 56, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;
    }

    public void setInterfaces(List<Interface> interfaces) {
        table.setRowCount(interfaces.size(), true);
        table.setRowData(interfaces);

        if(!interfaces.isEmpty())
            table.getSelectionModel().setSelected(interfaces.get(0), true);
    }
}
