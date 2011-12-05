package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class WorkmanagerView extends SuspendableViewImpl implements WorkmanagerPresenter.MyView {

    private WorkmanagerPresenter presenter;
    private ThreadPoolEditor poolEditor;

    @Override
    public void setPresenter(WorkmanagerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        poolEditor = new ThreadPoolEditor(presenter);

        tabLayoutpanel.add(poolEditor.asWidget(), "Thread Pools");

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setWorkManagerName(String workManagerName) {
        poolEditor.setContextName(workManagerName);
    }

    @Override
    public void setWorkManager(JcaWorkmanager manager) {
        poolEditor.setWorkManager(manager);
    }
}
