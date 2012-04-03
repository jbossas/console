package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DivertForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.Divert;
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
 * @date 4/2/12
 */
public class DivertList {

    private ContentHeaderLabel serverName;
    private DefaultCellTable<Divert> table;
    private ListDataProvider<Divert> provider;
    private MsgDestinationsPresenter presenter;

    public DivertList(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        table = new DefaultCellTable<Divert>(10, new ProvidesKey<Divert>() {
            @Override
            public Object getKey(Divert Divert) {
                return Divert.getRoutingName();
            }
        });

        provider = new ListDataProvider<Divert>();
        provider.addDataDisplay(table);

        Column<Divert, String> name = new Column<Divert, String>(new TextCell()) {
            @Override
            public String getValue(Divert object) {
                return object.getRoutingName();
            }
        };

        Column<Divert, String> from = new Column<Divert, String>(new TextCell()) {
            @Override
            public String getValue(Divert object) {
                return object.getDivertAddress();
            }
        };

        Column<Divert, String> to = new Column<Divert, String>(new TextCell()) {
            @Override
            public String getValue(Divert object) {
                return object.getForwardingAddress();
            }
        };

        table.addColumn(name, "Name");
        table.addColumn(from, "From");
        table.addColumn(to, "To");

        ToolStrip tools = new ToolStrip();
        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        presenter.launchNewDivertWizard();
                    }
                }));

        tools.addToolButtonRight(
                new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent clickEvent) {

                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle("Divert"),
                                Console.MESSAGES.deleteConfirm("Divert " + getSelectedEntity().getRoutingName()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onDeleteDivert(getSelectedEntity().getRoutingName());
                                        }
                                    }
                                });

                    }

                }));

        // ----

        DivertForm form = new DivertForm(new FormToolStrip.FormCallback<Divert>()
        {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSaveDivert(getSelectedEntity().getRoutingName(), changeset);
            }

            @Override
            public void onDelete(Divert entity) {

            }
        });

        // ----

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("A messaging resource that allows you to transparently divert messages routed to one address to some other address, without making any changes to any client application logic.")
                .setMaster("Diverts", table)
                .setMasterTools(tools)
                .setDetail("Details", form.asWidget());


        form.getForm().bind(table);

        return layout.build();
    }

    public void setDiverts(List<Divert> diverts) {
        provider.setList(diverts);
        serverName.setText("Diverts: Provider "+presenter.getCurrentServer());

        table.selectDefaultEntity();
    }

    public Divert getSelectedEntity() {
        SingleSelectionModel<Divert> selectionModel = (SingleSelectionModel<Divert>) table.getSelectionModel();
        return selectionModel.getSelectedObject();
    }


}
