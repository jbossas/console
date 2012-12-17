package org.jboss.as.console.client.shared.subsys.tx;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.tx.model.TransactionManager;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.ChextBoxItem;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class TransactionView extends SuspendableViewImpl implements TransactionPresenter.MyView{

    private TransactionPresenter presenter = null;
    private TXModelForm defaultForm;
    private TXModelForm pathForm;
    private TXModelForm processIDForm;
    private TXModelForm recoveryForm;


    @Override
    public void setPresenter(TransactionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel("Transactions");
        layout.add(titleBar);

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(scroll, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        panel.add(new ContentHeaderLabel("Transaction Manager"));
        panel.add(new ContentDescription(Console.CONSTANTS.subys_tx_desc()));

        // -----

        NumberBoxItem defaultTimeout = new NumberBoxItem("defaultTimeout", "Default Timeout");
        ChextBoxItem enableStatistics = new ChextBoxItem("enableStatistics", "Enable Statistics");
        ChextBoxItem enableTsm = new ChextBoxItem("enableTsmStatus", "Enable TSM Status");

        ChextBoxItem jts = new ChextBoxItem("jts", "Enable JTS");
        TextBoxItem nodeId = new TextBoxItem("nodeIdentifier", "Node Identifier");

        TextBoxItem processIdSocket = new TextBoxItem("processIdSocketBinding", "Process ID Socket");
        NumberBoxItem processIdPortMax = new NumberBoxItem("processIdMaxPorts", "Max Ports");
        ChextBoxItem processIdUUID = new ChextBoxItem("processIdUUID", "Process ID UUID?");

        ChextBoxItem useHornetq = new ChextBoxItem("hornetqStore", "Use HornetQ Store?");

        TextBoxItem path = new TextBoxItem("path", "Path");
        TextBoxItem relativeTo = new TextBoxItem("relativeTo", "Relative To");
        TextBoxItem objectStorePath = new TextBoxItem("objectStorePath", "Object Store Path");
        TextBoxItem objectStorePathRelativeTo = new TextBoxItem("objectStoreRelativeTo", "Object Store Relative To");

        ChextBoxItem recoveryListener = new ChextBoxItem("recoveryListener", "Recovery Listener");
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem statusSocketBinding = new TextBoxItem("statusSocketBinding", "Status Socket Binding");


        //  ---

        defaultForm = new TXModelForm(presenter, enableStatistics, enableTsm, jts, useHornetq, defaultTimeout, nodeId);
        pathForm = new TXModelForm(presenter, path, relativeTo, objectStorePath, objectStorePathRelativeTo);
        processIDForm = new TXModelForm(presenter, processIdUUID, processIdSocket, processIdPortMax);
        recoveryForm = new TXModelForm(presenter, socketBinding, statusSocketBinding, recoveryListener);

        panel.add(defaultForm.asWidget());

        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.getElement().setAttribute("style", "margin-top:15px;");

        tabs.add(processIDForm.asWidget(), "Process ID");
        tabs.add(recoveryForm.asWidget(), "Recovery");
        tabs.add(pathForm.asWidget(), "Path");

        tabs.selectTab(0);

        panel.add(tabs);

        return layout;
    }


    @Override
    public void setTransactionManager(TransactionManager tm) {
        defaultForm.edit(tm);
        pathForm.edit(tm);
        processIDForm.edit(tm);
        recoveryForm.edit(tm);
    }
}
