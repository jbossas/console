package org.jboss.as.console.client.shared.runtime.ws;

import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.tables.ColumnSortHandler;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;

import java.util.Comparator;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/23/12
 */
public class WebServiceRuntimeView extends SuspendableViewImpl implements WebServiceRuntimePresenter.MyView {

    private WebServiceRuntimePresenter presenter;
    private ListDataProvider<WebServiceEndpoint> dataProvider;
    private ColumnSortHandler<WebServiceEndpoint> sortHandler;

    @Override
    public void setPresenter(WebServiceRuntimePresenter presenter) {
        this.presenter = presenter;
    }

    private DefaultCellTable<WebServiceEndpoint> table;
    private Form<WebServiceEndpoint> form;

    public Widget createWidget() {

        table = new DefaultCellTable<WebServiceEndpoint>(10);
        sortHandler = new ColumnSortHandler<WebServiceEndpoint>();

        dataProvider = new ListDataProvider<WebServiceEndpoint>();
        dataProvider.addDataDisplay(table);

        TextColumn<WebServiceEndpoint> nameCol = new TextColumn<WebServiceEndpoint>() {
            @Override
            public String getValue(WebServiceEndpoint object) {
                return object.getName();
            }
        };
        nameCol.setSortable(true);
        sortHandler.setComparator(nameCol, new Comparator<WebServiceEndpoint>() {
            @Override
            public int compare(WebServiceEndpoint o1, WebServiceEndpoint o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        TextColumn<WebServiceEndpoint> contextCol = new TextColumn<WebServiceEndpoint>() {
            @Override
            public String getValue(WebServiceEndpoint object) {
                return object.getContext();
            }
        };
        contextCol.setSortable(true);
        sortHandler.setComparator(contextCol, new Comparator<WebServiceEndpoint>() {
            @Override
            public int compare(WebServiceEndpoint o1, WebServiceEndpoint o2) {
                return o1.getContext().compareTo(o2.getContext());
            }
        });
        TextColumn<WebServiceEndpoint> deploymentCol = new TextColumn<WebServiceEndpoint>() {
            @Override
            public String getValue(WebServiceEndpoint object) {
                return object.getDeployment();
            }
        };
        deploymentCol.setSortable(true);
        sortHandler.setComparator(deploymentCol, new Comparator<WebServiceEndpoint>() {
            @Override
            public int compare(WebServiceEndpoint o1, WebServiceEndpoint o2) {
                return o1.getDeployment().compareTo(o2.getDeployment());
            }
        });

        table.addColumn(nameCol, "Name");
        table.addColumn(contextCol, "Context");
        table.addColumn(deploymentCol, "Deployment");

        table.addColumnSortHandler(sortHandler);
        table.getColumnSortList().push(nameCol); // initial sort is on name

        // -----


        form = new Form<WebServiceEndpoint>(WebServiceEndpoint.class);
        form.setNumColumns(2);

        TextItem nameItem = new TextItem("name", "Name");
        TextItem contextItem = new TextItem("context", "Context");
        TextItem classItem = new TextItem("className", "Class");
        TextItem typeItem = new TextItem("type", "Type");
        TextItem wsdlItem = new TextItem("wsdl", "WSDL Url");
        TextItem dplItem = new TextItem("deployment", "Deployment");

        form.setFields(nameItem, contextItem, classItem, typeItem, wsdlItem, dplItem);
        form.bind(table);
        form.setEnabled(false);


        //final StaticHelpPanel helpPanel = new StaticHelpPanel(WebServiceDescriptions.getEndpointDescription());

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setTitle("Webservices")
                .setHeadline("Web Service Endpoints")
                .setMaster(Console.MESSAGES.available("Web Service Endpoints"), table)
                .setDescription(Console.CONSTANTS.subsys_ws_endpoint_desc())
                .setDetail(Console.CONSTANTS.common_label_selection(), form.asWidget());

        return layout.build();
    }

    public void updateEndpoints(List<WebServiceEndpoint> endpoints) {
        dataProvider.setList(endpoints);
        sortHandler.setList(dataProvider.getList());

        // Make sure the new values are properly sorted
        ColumnSortEvent.fire(table, table.getColumnSortList());

        if (endpoints.size() > 0)
            table.getSelectionModel().setSelected(endpoints.get(0), true);
    }
}
