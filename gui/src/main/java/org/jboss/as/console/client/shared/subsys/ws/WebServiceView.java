package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.ws.model.WebServiceProvider;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;

/**
 * @author Heiko Braun
 * @date 6/10/11
 */
public class WebServiceView extends DisposableViewImpl implements WebServicePresenter.MyView{

    private WebServicePresenter presenter;

    private ProviderEditor providerEditor;


    @Override
    public Widget createWidget() {

        providerEditor = new ProviderEditor(presenter);

        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(providerEditor.asWidget(), Console.CONSTANTS.subsys_ws_provider(), true);
        //tabLayoutpanel.add(endpointList.asWidget(), Console.CONSTANTS.subsys_ws_endpoints(), true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(WebServicePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProvider(WebServiceProvider webServiceProvider) {
        providerEditor.setProvider(webServiceProvider);
    }
}
