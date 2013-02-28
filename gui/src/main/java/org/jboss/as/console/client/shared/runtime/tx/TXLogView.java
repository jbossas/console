package org.jboss.as.console.client.shared.runtime.tx;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/27/13
 */
public class TXLogView extends SuspendableViewImpl implements TXLogPresenter.MyView{

    private TXLogPresenter presenter;

    private DefaultCellTable<TXRecord> table;
    private ListDataProvider<TXRecord> dataProvider;

    private ParticipantsPanel participantsPanel;

    public TXLogView() {

        table = new DefaultCellTable<TXRecord>(
                8,
                new ProvidesKey<TXRecord>() {
                    @Override
                    public Object getKey(TXRecord item) {
                        return item.getId();
                    }
                });

        dataProvider = new ListDataProvider<TXRecord>();
        dataProvider.addDataDisplay(table);

        TextColumn<TXRecord> id = new TextColumn<TXRecord>() {
            @Override
            public String getValue(TXRecord record) {
                return record.getId();
            }
        };

        TextColumn<TXRecord> age = new TextColumn<TXRecord>() {
            @Override
            public String getValue(TXRecord record) {
                return record.getAge();
            }
        };


        table.addColumn(id, "ID");
        table.addColumn(age, "Age");

        participantsPanel = new ParticipantsPanel();
    }

    @Override
    public Widget createWidget() {


        // record details
        final Form<TXRecord> recordForm = new Form<TXRecord>(TXRecord.class);
        TextItem idItem = new TextItem("id", "ID");
        TextItem ageItem = new TextItem("age", "Age");
        TextItem type = new TextItem("type", "Type");

        recordForm.setFields(idItem, ageItem, type);

        FormHelpPanel helpText = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = RuntimeBaseAddress.get();
                address.add("subsystem","transactions");
                address.add("log-store","log-store");

                return address;
            }
        }, recordForm);

        FormLayout formPanel = new FormLayout()
                .setForm(recordForm)
                .setHelp(helpText);

        recordForm.bind(table);


        // top level tools

        ToolStrip tools = new ToolStrip();
        final ToolButton removeButton = new ToolButton(Console.CONSTANTS.common_label_delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final TXRecord record = getSelectedRecord();

                if (record != null) {
                    Feedback.confirm(
                            Console.MESSAGES.deleteTitle("TX Record"),
                            Console.MESSAGES.deleteConfirm(record.getId()),
                            new Feedback.ConfirmationHandler() {
                                @Override
                                public void onConfirmation(boolean confirmed) {
                                    if (confirmed)
                                        presenter.onDeleteRecord(record);
                                }
                            }
                    );
                }
            }
        });
        tools.addToolButtonRight(removeButton);

        // lazy load the participant details
        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                TXRecord selection = getSelectedRecord();
                if(selection!=null)
                {
                    presenter.onLoadParticipants(selection);
                }
            }
        }) ;


        // handle deselection
        table.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                TXRecord selection = getSelectedRecord();
                if(null==selection)
                {
                    participantsPanel.clear();
                    recordForm.clearValues();
                }
            }
        });


        ToolStrip probe = new ToolStrip();
        probe.addToolButtonRight(new ToolButton("Probe", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.onProbe(true);
            }
        }));

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("Transaction Manager")
                .setHeadline("Transaction Recovery Logs")
                .setDescription("The persistent information that the transaction manager stores for the purpose of recovering a transaction in the event of failure. The probe operation will add and remove transactions from the model as the corresponding real transactions start and finish the prepare and commit phases. A stuck transaction will remain in the model until either it is completed or explicitly removed by the delete operation.")
                .setTopLevelTools(probe.asWidget())
                .setMaster("Transactions", table)
                .setMasterTools(tools)
                .addDetail("Log Entry", formPanel.build())
                .addDetail("Participants", participantsPanel.asWidget());

        return layout.build();
    }

    private TXRecord getSelectedRecord() {
        SingleSelectionModel<TXRecord> selectionModel = (SingleSelectionModel<TXRecord>)table.getSelectionModel();
        return selectionModel.getSelectedObject();
    }

    @Override
    public void setPresenter(TXLogPresenter presenter) {
        this.presenter = presenter;
        participantsPanel.setPresenter(presenter);
    }

    @Override
    public void clear() {
        dataProvider.getList().clear();
        dataProvider.flush();
        participantsPanel.clear();
    }

    @Override
    public void updateFrom(List<TXRecord> records) {
        dataProvider.setList(records);

        table.selectDefaultEntity();
    }

    @Override
    public void updateParticpantsFrom(List<TXParticipant> records) {
        participantsPanel.updateParticpantsFrom(records);
    }
}
