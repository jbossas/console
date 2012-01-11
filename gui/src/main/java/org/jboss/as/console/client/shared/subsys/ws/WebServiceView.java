package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceEndpoint;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceProvider;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.layout.RHSContentPanel;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class WebServiceView extends DisposableViewImpl implements WebServicePresenter.MyView{

    private WebServicePresenter presenter;
    private CellTable<WebServiceEndpoint> table ;
    private Form<WebServiceEndpoint> form;
    private Form<WebServiceProvider> providerForm;

    @Override
    public Widget createWidget() {
        LayoutPanel layout = new RHSContentPanel("Web Services");

        layout.add(new ContentHeaderLabel("Web Services Provider"));
        layout.add(new ContentDescription(Console.CONSTANTS.subsys_ws_desc()));

        // ---
        providerForm = new Form<WebServiceProvider>(WebServiceProvider.class);
        providerForm .setNumColumns(2);

        FormToolStrip<WebServiceProvider> formToolStrip = new FormToolStrip<WebServiceProvider>(
                providerForm,
                new FormToolStrip.FormCallback<WebServiceProvider>(){
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveProvider(providerForm.getUpdatedEntity());
                    }

                    @Override
                    public void onDelete(WebServiceProvider entity) {

                    }
                });
        formToolStrip.providesDeleteOp(false);

        layout.add(formToolStrip.asWidget());

        CheckBoxItem modify = new CheckBoxItem("modifyAddress", "Modify SOAP Address");
        TextBoxItem wsdlHost = new TextBoxItem("wsdlHost", "WSDL Host");
        NumberBoxItem wsdlPort = new NumberBoxItem("wsdlPort", "WSDL Port");
        NumberBoxItem wsdlSecurePort = new NumberBoxItem("wsdlSecurePort", "WSDL Secure Port");

        providerForm.setFields(modify, wsdlHost, wsdlPort, wsdlSecurePort);

        layout.add(providerForm.asWidget());
        providerForm.setEnabled(false);

        // ---

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

        layout.add(new ContentGroupLabel(Console.MESSAGES.available("Web Service Endpoints")));

        layout.add(table);
        layout.add(pager);

        // -----

        layout.add(new ContentGroupLabel(Console.CONSTANTS.common_label_selection()));

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

    @Override
    public void setProvider(WebServiceProvider webServiceProvider) {
        providerForm.edit(webServiceProvider);
    }
}
