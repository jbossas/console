package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.RHSContentPanel;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.widgets.ContentGroupLabel;
import org.jboss.as.console.client.widgets.ContentHeaderLabel;
import org.jboss.as.console.client.widgets.tables.DefaultPager;
import org.jboss.as.console.client.widgets.forms.Form;
import org.jboss.as.console.client.widgets.forms.TextItem;
import org.jboss.as.console.client.widgets.tables.DefaultCellTable;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class WebServiceView extends DisposableViewImpl implements WebServicePresenter.MyView{

    private WebServicePresenter presenter;
    private CellTable<WebServiceEndpoint> table ;
    private Form<WebServiceEndpoint> form;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new RHSContentPanel("Web Services");

        layout.add(new ContentHeaderLabel("Registered Web Service Endpoints"));

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

        DefaultPager pager = new DefaultPager();
        pager.setDisplay(table);

        layout.add(table);
        layout.add(pager);

        // -----

        layout.add(new ContentGroupLabel("Web Service"));

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
        layout.add(helpPanel.asWidget());

        layout.add(form.asWidget());

        return layout;
    }

    @Override
    public void setPresenter(WebServicePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateEndpoints(List<WebServiceEndpoint> endpoints) {
        table.setRowCount(endpoints.size(), true);
        table.setRowData(endpoints);

        if(endpoints.size()>0)
            table.getSelectionModel().setSelected(endpoints.get(0), true);
    }
}
