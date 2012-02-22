package org.jboss.as.console.client.shared.subsys.jgroups;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.TextLinkCell;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/16/12
 */
public class StackOverview {

    private JGroupsPresenter presenter;
    private Form<JGroupsStack> form;
    private ListDataProvider<JGroupsStack> dataProvider;
    private DefaultCellTable<JGroupsStack> table ;

    public StackOverview(JGroupsPresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {
        table = new DefaultCellTable<JGroupsStack>(8, new ProvidesKey<JGroupsStack>() {
            @Override
            public Object getKey(JGroupsStack item) {
                return item.getName();
            }
        });
        dataProvider = new ListDataProvider<JGroupsStack>();
        dataProvider.addDataDisplay(table);

        TextColumn<JGroupsStack> jndiName = new TextColumn<JGroupsStack>() {
            @Override
            public String getValue(JGroupsStack record) {
                return record.getName();
            }
        };

        Column<JGroupsStack, JGroupsStack> option = new Column<JGroupsStack, JGroupsStack>(
                new TextLinkCell<JGroupsStack>(Console.CONSTANTS.common_label_view(), new ActionCell.Delegate<JGroupsStack>() {
                    @Override
                    public void execute(JGroupsStack selection) {
                        presenter.getPlaceManager().revealPlace(
                                new PlaceRequest(NameTokens.JGroupsPresenter).with("name", selection.getName())
                        );
                    }
                })
        ) {
            @Override
            public JGroupsStack getValue(JGroupsStack manager) {
                return manager;
            }
        };


        table.addColumn(jndiName, "Name");
        table.addColumn(option, "Option");

        table.setSelectionModel(new SingleSelectionModel<JGroupsStack>());

        ToolStrip toolstrip = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewStackWizard();
            }
        });
        toolstrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Protocol Stack"),
                        Console.MESSAGES.deleteConfirm("Protocol Stack"),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed)
                                {
                                    SingleSelectionModel<JGroupsStack> selectionModel = (SingleSelectionModel<JGroupsStack>) table.getSelectionModel();
                                    presenter.onDeleteStack(selectionModel.getSelectedObject());
                                }
                            }
                        });
            }
        });

        toolstrip.addToolButtonRight(removeBtn);

        // ------


       /* form = new Form<JGroupsStack>(JGroupsStack.class);
        form.setNumColumns(2);

        TextItem jndi = new TextItem("jndiName", "JNDI Name");
        CheckBoxItem debug = new CheckBoxItem("debug", "Debug Enabled?");
        TextBoxItem from = new TextBoxItem("from", "Default From");

        form.setFields(jndi, debug, from);
        form.setEnabled(false);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "mail");
                address.add("mail-session", "*");
                return address;
            }
        }, form);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();


        FormToolStrip<JGroupsStack> formToolStrip = new FormToolStrip<JGroupsStack>(
                form, new FormToolStrip.FormCallback<JGroupsStack>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(JGroupsStack entity) {

            }
        });
        formToolStrip.providesDeleteOp(false); */

        Widget panel = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("JGroups")
                .setHeadline("Protocol Stacks")
                .setDescription(Console.CONSTANTS.subsys_jgroups_session_desc())
                .setMaster(Console.MESSAGES.available("Protocol Stacks"), table)
                .setMasterTools(toolstrip.asWidget())
                .build();

        //form.bind(table);



        return panel;

    }

    public void updateStacks(List<JGroupsStack> stacks) {
        dataProvider.setList(stacks);
        table.selectDefaultEntity();
    }
}
