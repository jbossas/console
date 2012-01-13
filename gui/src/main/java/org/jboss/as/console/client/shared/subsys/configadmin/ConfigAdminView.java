package org.jboss.as.console.client.shared.subsys.configadmin;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.configadmin.model.ConfigAdminData;

public class ConfigAdminView extends SuspendableViewImpl implements ConfigAdminPresenter.MyView {
    private ConfigAdminEditor configAdminEditor;
    private ConfigAdminPresenter presenter;

    @Override
    public Widget createWidget() {
        configAdminEditor = new ConfigAdminEditor(presenter);

        TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(40, Style.Unit.PX);
        tabLayoutPanel.addStyleName("default-tabpanel");

        tabLayoutPanel.add(configAdminEditor.asWidget(), Console.CONSTANTS.subsys_configadmin());
        tabLayoutPanel.selectTab(0);

        return tabLayoutPanel;
    }

    @Override
    public void setPresenter(ConfigAdminPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateConfigurationAdmin(List<ConfigAdminData> casDataList, String selectPid) {
        configAdminEditor.update(casDataList, selectPid);
    }
}
