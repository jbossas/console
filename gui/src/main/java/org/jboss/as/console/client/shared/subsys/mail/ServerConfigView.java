package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.PasswordBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 2/14/12
 */
public class ServerConfigView {

    private HTML headline;
    private String description;
    private Form<MailServerDefinition> form;
    private MailPresenter presenter;
    private ListDataProvider<MailServerDefinition> dataProvider;
    private String title;
    private DefaultCellTable<MailServerDefinition> table;


    public ServerConfigView(
            String title, String description,
            MailPresenter presenter) {
        this.title= title;
        this.description = description;
        this.presenter = presenter;
    }

    Widget asWidget() {


        table = new DefaultCellTable<MailServerDefinition>(3, new ProvidesKey<MailServerDefinition>() {
            @Override
            public Object getKey(MailServerDefinition item) {
                return item.getType();
            }
        });

        dataProvider = new ListDataProvider<MailServerDefinition>();
        dataProvider.addDataDisplay(table);

        TextColumn<MailServerDefinition> nameColumn = new TextColumn<MailServerDefinition>() {
            @Override
            public String getValue(MailServerDefinition record) {
                return record.getType().name().toUpperCase();

            }
        };

        table.addColumn(nameColumn, "Type");


        ToolStrip tableTools = new ToolStrip();

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.launchNewServerWizard();
                    }
                });

        ToolButton removeBtn = new ToolButton(Console.CONSTANTS.common_label_remove(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Feedback.confirm(
                                Console.MESSAGES.deleteTitle(Console.CONSTANTS.common_label_item()),
                                Console.MESSAGES.deleteConfirm(Console.CONSTANTS.common_label_item()),
                                new Feedback.ConfirmationHandler() {
                                    @Override
                                    public void onConfirmation(boolean isConfirmed) {
                                        if (isConfirmed) {
                                            presenter.onRemoveServer(form.getEditedEntity());
                                        }
                                    }
                                });
                    }
                });

        tableTools.addToolButtonRight(addBtn);
        tableTools.addToolButtonRight(removeBtn);

        // ----

        form = new Form<MailServerDefinition>(MailServerDefinition.class);

        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem user = new TextBoxItem("username", "Username");
        PasswordBoxItem pass = new PasswordBoxItem("password", "Password");
        CheckBoxItem ssl = new CheckBoxItem("ssl", "Use SSL?");

        form.setFields(socket, ssl, user, pass);
        form.setEnabled(false);
        form.setNumColumns(2);

        FormToolStrip formTools = new FormToolStrip(form,
                new FormToolStrip.FormCallback<MailServerDefinition>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {

                        presenter.onSaveServer(form.getEditedEntity().getType(), changeset);
                    }

                    @Override
                    public void onDelete(MailServerDefinition entity) {

                    }
                });

        headline = new HTML();
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setDescription(description)
                .setMaster(Console.MESSAGES.available("Mail Server"), table)
                .setMasterTools(tableTools)
                .setDetailTools(formTools.asWidget())
                .setDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());


        form.bind(table);

        return layout.build();
    }

    public void setServerConfig(MailSession parent) {

        headline.setText("Mail Server: " +parent.getJndiName());

        // it's a single instance but we still use a table

        List<MailServerDefinition> values = new ArrayList<MailServerDefinition>(3);

        if(parent.getSmtpServer()!=null)
            values.add(parent.getSmtpServer());

        if(parent.getImapServer()!=null)
            values.add(parent.getImapServer());

        if(parent.getPopServer()!=null)
            values.add(parent.getPopServer());

        dataProvider.setList(values);

        table.selectDefaultEntity();
    }


}
