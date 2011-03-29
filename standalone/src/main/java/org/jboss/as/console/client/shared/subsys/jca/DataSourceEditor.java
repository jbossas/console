package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.forms.CheckBoxItem;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.PasswordBoxItem;
import org.jboss.as.console.client.widgets.forms.TextBoxItem;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.icons.Icons;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;
import org.jboss.as.console.client.widgets.tools.ToolButton;
import org.jboss.as.console.client.widgets.tools.ToolStrip;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public class DataSourceEditor {

    private DataSourcePresenter presenter;
    private DefaultCellTable<DataSource> dataSourceTable;
    private ListDataProvider<DataSource> dataSourceProvider;

    public DataSourceEditor(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    public Widget asWidget() {

        ScrollPanel scroll = new ScrollPanel();

        VerticalPanel layout = new VerticalPanel();
        layout.getElement().setAttribute("style", "margin:15px; width:95%");

        scroll.add(layout);

        ToolStrip toolstrip = new ToolStrip();
        toolstrip.addToolButton(new ToolButton("Add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Console.MODULES.getMessageCenter().notify(
                        new Message("Adding datasources not implemented",Message.Severity.Warning)
                );
            }
        }));

        layout.add(toolstrip);

        // ---

        layout.add(new ContentHeaderLabel("JDBC Data Source Configurations"));

        dataSourceTable = new DefaultCellTable<DataSource>(20);
        dataSourceProvider = new ListDataProvider<DataSource>();
        dataSourceProvider.addDataDisplay(dataSourceTable);


        TextColumn<DataSource> nameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getName();
            }
        };

        TextColumn<DataSource> jndiNameColumn = new TextColumn<DataSource>() {
            @Override
            public String getValue(DataSource record) {
                return record.getJndiName();
            }
        };

        Column<DataSource, ImageResource> statusColumn =
                new Column<DataSource, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(DataSource dataSource) {

                        ImageResource res = null;

                        if(dataSource.isEnabled())
                            res = Icons.INSTANCE.statusGreen_small();
                        else
                            res = Icons.INSTANCE.statusRed_small();

                        return res;
                    }
                };


        dataSourceTable.addColumn(nameColumn, "Name");
        dataSourceTable.addColumn(jndiNameColumn, "JNDI");
        dataSourceTable.addColumn(statusColumn, "Enabled?");

        layout.add(dataSourceTable);


        // -----------

        Form<DataSource> form = new Form(DataSource.class);

        TextItem nameItem = new TextItem("name", "Name");
        CheckBoxItem enabledItem = new CheckBoxItem("enabled", "Is enabled?");
        TextBoxItem jndiItem = new TextBoxItem("jndiName", "JNDI");
        TextBoxItem driverItem = new TextBoxItem("driverClass", "Driver");

        TextBoxItem userItem = new TextBoxItem("username", "Username");
        PasswordBoxItem passwordItem = new PasswordBoxItem("password", "Password");

        form.setFields(nameItem, enabledItem, jndiItem, driverItem, userItem, passwordItem);
        form.bind(dataSourceTable);
        form.setEnabled(false); // currently not editable

        Widget formWidget = form.asWidget();

        layout.add(new ContentGroupLabel("Details"));
        layout.add(formWidget);
        return scroll;
    }

    public void updateDataSources(List<DataSource> datasources) {
        dataSourceProvider.setList(datasources);

         if(!datasources.isEmpty())
            dataSourceTable.getSelectionModel().setSelected(datasources.get(0), true);

    }
}
