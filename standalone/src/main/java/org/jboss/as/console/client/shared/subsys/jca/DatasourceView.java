package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 3/24/11
 */
public class DatasourceView extends DisposableViewImpl implements DataSourcePresenter.MyView {

    private DataSourcePresenter presenter;

    LayoutPanel layout = null;

    private DataSourceEditor dataSourceEditor;

    @Override
    public Widget createWidget() {

        this.dataSourceEditor = new DataSourceEditor(presenter);

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        tabLayoutpanel.add(dataSourceEditor.asWidget(), "Data Sources");
        tabLayoutpanel.add(new HTML("Not implemented yet"), "XA Data Sources");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setPresenter(DataSourcePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateDataSources(List<DataSource> datasources) {
        dataSourceEditor.updateDataSources(datasources);
    }
}
