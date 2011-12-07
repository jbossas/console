package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class MailSessionView extends DisposableViewImpl implements MailPresenter.MyView{

    private MailPresenter presenter;
    private Form<MailSession> form;
    private ListDataProvider<MailSession> dataProvider;
    private DefaultCellTable<MailSession> table ;

    @Override
    public Widget createWidget() {


        table = new DefaultCellTable<MailSession>(10);
        dataProvider = new ListDataProvider<MailSession>();
        dataProvider.addDataDisplay(table);

        TextColumn<MailSession> jndiName = new TextColumn<MailSession>() {
            @Override
            public String getValue(MailSession record) {
                return record.getJndiName();
            }
        };

        table.addColumn(jndiName, "JNDI Name");


        ToolStrip toolstrip = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewSessionWizard();
            }
        });
        addBtn.ensureDebugId(Console.CONSTANTS.debug_label_add_mailSessionView());
        toolstrip.addToolButtonRight(addBtn);

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Feedback.confirm("Remove Mail Session", "Really remove this mail session?",
                        new Feedback.ConfirmationHandler(){
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if(isConfirmed)
                                    presenter.onDelete(form.getEditedEntity());
                            }
                        });
            }
        });
        removeBtn.ensureDebugId(Console.CONSTANTS.debug_label_remove_mailSessionView());
        toolstrip.addToolButtonRight(removeBtn);

        // ------


        form = new Form<MailSession>(MailSession.class);
        form.setNumColumns(2);

        TextItem jndi = new TextItem("jndiName", "JNDI Name");
        TextBoxItem smtp = new TextBoxItem("smtpServer", "SMTP Server");
        TextBoxItem imap = new TextBoxItem("imapServer", "IMAP Server");
        TextBoxItem pop = new TextBoxItem("popServer", "Pop3 Server");
        CheckBoxItem debug = new CheckBoxItem("debug", "Debug Enabled?");

        form.setFields(jndi, smtp, imap, pop, debug);
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


        FormToolStrip<MailSession> formToolStrip = new FormToolStrip<MailSession>(
                form, new FormToolStrip.FormCallback<MailSession>() {
            @Override
            public void onSave(Map<String, Object> changeset) {
                presenter.onSave(form.getEditedEntity(), changeset);
            }

            @Override
            public void onDelete(MailSession entity) {

            }
        });
        formToolStrip.providesDeleteOp(false);

        Widget panel = new MultipleToOneLayout()
                .setTitle("Mail")
                .setHeadline("Mail Sessions")
                .setDescription("The mail session configuration.")
                .setMaster("Configured mail sessions", table)
                .setTopLevelTools(toolstrip.asWidget())
                .setDetailTools(formToolStrip.asWidget())
                .setDetail("Mail Session", detail).build();

        form.bind(table);



        return panel;
    }

    @Override
    public void setPresenter(MailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateFrom(List<MailSession> list) {
        dataProvider.setList(list);

        if(!list.isEmpty())
            table.getSelectionModel().setSelected(list.get(0), true);
    }
}
