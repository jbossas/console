package org.jboss.as.console.client.shared.subsys.mail;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.PasswordBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 2/14/12
 */
public class ServerConfigView {

    private HTML headline;
    private String description;
    private FormToolStrip.FormCallback callback;
    private Form<MailServerDefinition> form;
    private MailPresenter presenter;
    private ListDataProvider<MailServerDefinition> dataProvider;
    private String title;
    private DefaultCellTable<MailServerDefinition> table;


    public ServerConfigView(
            String title, String description,
            FormToolStrip.FormCallback<MailServerDefinition> callback,
            MailPresenter presenter) {
        this.title= title;
        this.description = description;
        this.callback = callback;
        this.presenter = presenter;
    }

    Widget asWidget() {


        table = new DefaultCellTable<MailServerDefinition>(1);

        dataProvider = new ListDataProvider<MailServerDefinition>();
        dataProvider.addDataDisplay(table);

        TextColumn<MailServerDefinition> nameColumn = new TextColumn<MailServerDefinition>() {
            @Override
            public String getValue(MailServerDefinition record) {
                return record.getType().name().toUpperCase();

            }
        };

        table.addColumn(nameColumn, "Type");


        // ----

        form = new Form<MailServerDefinition>(MailServerDefinition.class);

        TextBoxItem socket = new TextBoxItem("socketBinding", "Socket Binding");
        TextBoxItem user = new TextBoxItem("username", "Username");
        PasswordBoxItem pass = new PasswordBoxItem("password", "Password");
        CheckBoxItem ssl = new CheckBoxItem("ssl", "Use SSL?");

        form.setFields(socket, ssl, user, pass);
        form.setEnabled(false);
        form.setNumColumns(2);

        FormToolStrip formTools = new FormToolStrip(form, callback);

        headline = new HTML();
        headline.setStyleName("content-header-label");

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(headline)
                .setDescription(description)
                .setMaster(Console.MESSAGES.available("Mail Server"), table)
                .setDetailTools(formTools.asWidget())
                .setDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());


        form.bind(table);

        return layout.build();
    }

    public void setServerConfig(String parent, MailServerDefinition server) {

        headline.setText("Mail Server: " +parent);

        if(server!=null)
            form.edit(server);
        else
            form.clearValues();

        // it's a single instance but we still use a table

        List<MailServerDefinition> values = new ArrayList<MailServerDefinition>(1);
        values.add(server);

        dataProvider.setList(values);

        table.selectDefaultEntity();
    }
}
