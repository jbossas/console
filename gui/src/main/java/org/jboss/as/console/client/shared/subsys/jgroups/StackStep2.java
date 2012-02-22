package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/22/12
 */
public class StackStep2 {

    private NewStackWizard presenter;
    private DefaultCellTable<JGroupsProtocol> table;
    private ListDataProvider<JGroupsProtocol> dataProvider;

    public StackStep2(NewStackWizard presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:15px; vertical-align:center;width:95%");

        layout.add(new HTML("<h3>"+ Console.CONSTANTS.subsys_jgroups_step2()+"</h3>"));

        // available protocols
        List<String> names = new ArrayList<String>();
        for (Protocol element : Protocol.values()) {
            final String name = element.getLocalName();
            if (name!=null && !"TCP".equals(name) && !"UDP".equals(name))
                names.add(name);
        }

        final Form<JGroupsProtocol> form = new Form<JGroupsProtocol>(JGroupsProtocol.class);

        ComboBoxItem typeField = new ComboBoxItem("type", "Type");


        typeField.setValueMap(names);

        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding", false);

        form.setFields(typeField, socket);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "jgroups");
                address.add("stack", "*");
                address.add("protocol", "*");
                return address;
            }
        }, form);
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        //  ------


        table = new DefaultCellTable<JGroupsProtocol>(6, new ProvidesKey<JGroupsProtocol>() {
            @Override
            public Object getKey(JGroupsProtocol item) {
                return item.getType();
            }
        });
        dataProvider = new ListDataProvider<JGroupsProtocol>();
        dataProvider.addDataDisplay(table);

        TextColumn<JGroupsProtocol> type = new TextColumn<JGroupsProtocol>() {
            @Override
            public String getValue(JGroupsProtocol record) {
                return record.getType();
            }
        };

        table.addColumn(type, "Type");

        final SingleSelectionModel<JGroupsProtocol> selectionModel = new SingleSelectionModel<JGroupsProtocol>();
        table.setSelectionModel(selectionModel);

        ToolStrip toolstrip = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_append(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if(!validation.hasErrors())
                {
                    JGroupsProtocol protocol = form.getUpdatedEntity();
                    dataProvider.getList().add(protocol);
                    table.getSelectionModel().setSelected(protocol, true);
                }
            }
        });
        toolstrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                JGroupsProtocol protocol = selectionModel.getSelectedObject();
                List<JGroupsProtocol> list = dataProvider.getList();
                list.remove(protocol);

                List<JGroupsProtocol> update = new LinkedList<JGroupsProtocol>();
                update.addAll(list);

                dataProvider.setList(update);
            }
        });

        toolstrip.addToolButtonRight(removeBtn);

        layout.add(toolstrip.asWidget());
        layout.add(table.asWidget());

        // ----


        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if(!validation.hasErrors())
                {
                    presenter.onFinishStep2(dataProvider.getList());
                }
            }
        };

        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.cancel();
            }
        };

        DialogueOptions options = new DialogueOptions(
                "Done",submitHandler,
                Console.CONSTANTS.common_label_cancel(),cancelHandler
        );

        return new WindowContentBuilder(layout, options).build();
    }
}
