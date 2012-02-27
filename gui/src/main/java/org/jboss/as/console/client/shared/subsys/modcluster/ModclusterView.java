package org.jboss.as.console.client.shared.subsys.modcluster;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.modcluster.model.Modcluster;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

/**
 * @author Pavel Slegr
 * @date 02/16/12
 */
public class ModclusterView extends DisposableViewImpl implements ModclusterPresenter.MyView{

    private ModclusterPresenter presenter;
    private Form<Modcluster> form;
    private ListDataProvider<Modcluster> dataProvider;
    private DefaultCellTable<Modcluster> table ;

    @Override
    public Widget createWidget() {


        table = new DefaultCellTable<Modcluster>(10);
        dataProvider = new ListDataProvider<Modcluster>();
        dataProvider.addDataDisplay(table);

        TextColumn<Modcluster> name = new TextColumn<Modcluster>() {
            @Override
            public String getValue(Modcluster record) {
                return "configuration";
            }
        };

        table.addColumn(name, "Modcluster");


        ToolStrip toolstrip = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewSessionWizard();
            }
        });
        addBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_add_modclusterView());
        toolstrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm("Remove modcluster", "Really remove this modecluster?",
                        new Feedback.ConfirmationHandler(){
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.onDelete(form.getEditedEntity());
                            }
                        });
            }
        });
        removeBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_remove_modclusterView());
        toolstrip.addToolButtonRight(removeBtn);

        // ------
        form = new Form<Modcluster>(Modcluster.class);
        form.setNumColumns(2);
        
        CheckBoxItem advertise = new CheckBoxItem("advertise", "Advertise");
        TextBoxItem advertiseSocket = new TextBoxItem("advertiseSocket", "Advertise Socket");
        TextBoxItem excludedContexts = new TextBoxItem("excludedContexts", "Excluded Contexts");
        CheckBoxItem autoEnableContexts = new CheckBoxItem("autoEnableContexts", "Auto Enable Contexts");
        TextBoxItem balancer = new TextBoxItem("balancer", "Balancer");
        NumberBoxItem maxAttemps = new NumberBoxItem("maxAttemps", "Max Attemps");
        CheckBoxItem flushPackets = new CheckBoxItem("flushPackets", "Flush Packets");
        NumberBoxItem flushWait = new NumberBoxItem("flushWait", "Flush Wait");
        NumberBoxItem nodeTimeout = new NumberBoxItem("nodeTimeout", "Node Timeout");
        NumberBoxItem ping = new NumberBoxItem("ping", "Ping");
        TextBoxItem proxyList = new TextBoxItem("proxyList", "Proxy List");
        TextBoxItem proxyUrl = new TextBoxItem("proxyUrl", "Proxy Url");
        NumberBoxItem socketTimeout = new NumberBoxItem("socketTimeout", "Socket Timeout");
        NumberBoxItem stopContextTimeout = new NumberBoxItem("stopContextTimeout", "Stop Context Timeout");
        CheckBoxItem stickySession = new CheckBoxItem("stickySession", "Sticky Session");
        CheckBoxItem stickySessionForce = new CheckBoxItem("stickySessionForce", "Sticky Session Force");
        CheckBoxItem stickySessionRemove = new CheckBoxItem("stickySessionRemove", "Sticky Session Remove");
        NumberBoxItem workerTimeout = new NumberBoxItem("workerTimeout", "Worker Timeout");
        NumberBoxItem ttl = new NumberBoxItem("ttl", "TTL");

        form.setFields(
        		advertise, autoEnableContexts, advertiseSocket, excludedContexts,
        		balancer,maxAttemps,flushPackets,flushWait,
        		nodeTimeout,ping,proxyList,proxyUrl,
        		socketTimeout,stopContextTimeout,
        		stickySession,stickySessionForce,stickySessionRemove,workerTimeout,
        		ttl);
        form.setEnabled(false);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "modcluster");
                address.add("mod-cluster-config", "configuration");
                return address;
            }
        }, form);

        Widget detail = new FormLayout()
                .setForm(form)
                .setHelp(helpPanel).build();


        FormToolStrip<Modcluster> formToolStrip = new FormToolStrip<Modcluster>(
                form, new FormToolStrip.FormCallback<Modcluster>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(Modcluster entity) {

            }
        });
        formToolStrip.providesDeleteOp(false);

        Widget panel = new MultipleToOneLayout()
                .setTitle("Modcluster")
                .setHeadline("Modcluster")
                .setDescription("The modcluster configuration.")
                .setMaster("Configured modcluster", table)
                .setTopLevelTools(toolstrip.asWidget())
                .setDetailTools(formToolStrip.asWidget())
                .setDetail("Modcluster", detail).build();

        form.bind(table);



        return panel;
    }

    @Override
    public void setPresenter(ModclusterPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(List<Modcluster> list) {
        dataProvider.setList(list);

        if(!list.isEmpty())
            table.getSelectionModel().setSelected(list.get(0), true);
    }
}
