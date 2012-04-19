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
import org.jboss.as.console.client.shared.general.forms.LocalSocketForm;
import org.jboss.as.console.client.shared.general.model.LocalSocketBinding;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
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
public class LocalSocketList {
    
    private SocketBindingPresenter presenter;

    private DefaultCellTable<LocalSocketBinding> factoryTable;
    private ListDataProvider<LocalSocketBinding> factoryProvider;
    private LocalSocketForm defaultAttributes;

    
    public LocalSocketList(SocketBindingPresenter presenter) {
        this.presenter = presenter;        
    }    

    Widget asWidget() {

        factoryTable = new DefaultCellTable<LocalSocketBinding>(10, new ProvidesKey<LocalSocketBinding>() {
            @Override
            public Object getKey(LocalSocketBinding LocalSocketBinding) {
                return LocalSocketBinding.getName();
            }
        });

        factoryProvider = new ListDataProvider<LocalSocketBinding>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<LocalSocketBinding, String> nameColumn = new Column<LocalSocketBinding, String>(new TextCell()) {
            @Override
            public String getValue(LocalSocketBinding object) {
                return object.getName();
            }
        };


        factoryTable.addColumn(nameColumn, "Name");

        // defaultAttributes
        defaultAttributes = new LocalSocketForm(new FormToolStrip.FormCallback<LocalSocketBinding>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.saveLocalSocketBinding(getSelectedEntity().getName(), changeset);
            }
            @Override
            public void onDelete(LocalSocketBinding entity) {

            }
        });

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewLocalSocketBindingWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Local Socket Binding"),
                                Console.MESSAGES.deleteConfirm("Local Socket Binding " + getSelectedEntity().getName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteLocalSocketBinding(getSelectedEntity().getName());
                                        }
                                    }
                                });

                    }

                }));

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadline("Local Socket Bindings")
                .setDescription("Configuration information for a, local destination, outbound socket binding.")
                .setMaster("Local Socket Bindings", factoryTable)
                .setMasterTools(tools)
                .setDetail("Details", defaultAttributes.asWidget());

        defaultAttributes.getForm().bind(factoryTable);
        defaultAttributes.getForm().setEnabled(false);

        return layout.build();
    }

    public void setLocalSocketBindings(List<LocalSocketBinding> LocalSocketBindings) {
        factoryProvider.setList(LocalSocketBindings);

        factoryTable.selectDefaultEntity();
    }

    public LocalSocketBinding getSelectedEntity() {
        SingleSelectionModel<LocalSocketBinding> selectionModel = (SingleSelectionModel<LocalSocketBinding>)factoryTable.getSelectionModel();
        return selectionModel.getSelectedObject();
    }
}
