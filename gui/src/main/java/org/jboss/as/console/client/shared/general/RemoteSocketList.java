package org.jboss.as.console.client.shared.general;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.general.forms.RemoteSocketForm;
import org.jboss.as.console.client.shared.general.model.RemoteSocketBinding;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/19/12
 */
public class RemoteSocketList {
    
    private SocketBindingPresenter presenter;

    private DefaultCellTable<RemoteSocketBinding> factoryTable;
    private ListDataProvider<RemoteSocketBinding> factoryProvider;
    private RemoteSocketForm defaultAttributes;
    private ContentHeaderLabel headline;

    public RemoteSocketList(SocketBindingPresenter presenter) {
        this.presenter = presenter;        
    }    

    Widget asWidget() {

        factoryTable = new DefaultCellTable<RemoteSocketBinding>(10, new ProvidesKey<RemoteSocketBinding>() {
            @Override
            public Object getKey(RemoteSocketBinding RemoteSocketBinding) {
                return RemoteSocketBinding.getName();
            }
        });

        factoryProvider = new ListDataProvider<RemoteSocketBinding>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<RemoteSocketBinding, String> nameColumn = new Column<RemoteSocketBinding, String>(new TextCell()) {
            @Override
            public String getValue(RemoteSocketBinding object) {
                return object.getName();
            }
        };


        factoryTable.addColumn(nameColumn, "Name");

        // defaultAttributes
        defaultAttributes = new RemoteSocketForm(new FormToolStrip.FormCallback<RemoteSocketBinding>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.saveRemoteSocketBinding(getSelectedEntity().getName(), changeset);
            }
            @Override
            public void onDelete(RemoteSocketBinding entity) {

            }
        });

        ToolStrip tools = new ToolStrip();

        //  ----

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewRemoteSocketBindingWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Remote Socket Binding"),
                                Console.MESSAGES.deleteConfirm("Remote Socket Binding " + getSelectedEntity().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteRemoteSocketBinding(getSelectedEntity().getName());
                                        }
                                    }
                                });

                    }

                }));


        headline = new ContentHeaderLabel();

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setDescription("Configuration information for a, remote destination, outbound socket binding.")
                .setMaster("Remote Socket Bindings", factoryTable)
                .setMasterTools(tools)
                .setDetail("Details", defaultAttributes.asWidget());

        defaultAttributes.getForm().bind(factoryTable);
        defaultAttributes.getForm().setEnabled(false);

        return layout.build();
    }

    public void setRemoteSocketBindings(String groupName, List<RemoteSocketBinding> RemoteSocketBindings) {

        headline.setText("Remote Socket Bindings: Group "+groupName);
        factoryProvider.setList(RemoteSocketBindings);

        factoryTable.selectDefaultEntity();
    }

    public RemoteSocketBinding getSelectedEntity() {
        SingleSelectionModel<RemoteSocketBinding> selectionModel = (SingleSelectionModel<RemoteSocketBinding>)factoryTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }

}
