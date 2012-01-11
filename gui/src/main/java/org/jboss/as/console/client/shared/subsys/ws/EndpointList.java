package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/11/12
 */
public class EndpointList {


    private DefaultCellTable<WebServiceEndpoint> table;
    private Form<WebServiceEndpoint> form;

    Widget asWidget() {

        table = new DefaultCellTable<WebServiceEndpoint>(6);

        TextColumn<WebServiceEndpoint> nameCol = new TextColumn<WebServiceEndpoint>() {
            @Override
            public String getValue(WebServiceEndpoint object) {
                return object.getName();
            }
        };

        TextColumn<WebServiceEndpoint> contextCol = new TextColumn<WebServiceEndpoint>() {
            @Override
            public String getValue(WebServiceEndpoint object) {
                return object.getContext();
            }
        };

        table.addColumn(nameCol, "Name");
        table.addColumn(contextCol, "Context");


        // -----


        form = new Form<WebServiceEndpoint>(WebServiceEndpoint.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextItem contextItem = new TextItem("context", "Context");
        TextItem classItem = new TextItem("className", "Class");
        TextItem typeItem = new TextItem("type", "Type");
        TextItem wsdlItem = new TextItem("wsdl", "WSDL Url");

        form.setFields(nameItem, contextItem, classItem, typeItem, wsdlItem);
        form.bind(table);
        form.setEnabled(false);


        final StaticHelpPanel helpPanel = new StaticHelpPanel(WebServiceDescriptions.getEndpointDescription());


        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Endpoints")
                .setHeadline("Web Service Endpoints")
                .setMaster(Console.MESSAGES.available("Web Service Endpoints"), table)
                .setDescription(Console.CONSTANTS.subsys_ws_endpoint_desc())
                .setDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());

        return layout.build();
    }

    public void updateEndpoints(List<WebServiceEndpoint> endpoints) {
        table.setRowCount(endpoints.size(), true);
        table.setRowData(endpoints);

        if(endpoints.size()>0)
            table.getSelectionModel().setSelected(endpoints.get(0), true);
    }
}
