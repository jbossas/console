package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.widgets.pages.PagedView;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class WorkmanagerDetail {

    private JcaPresenter presenter;
    private WorkmanagerList managerList;
    private ThreadPoolEditor threadPools;
    PagedView panel;

    public WorkmanagerDetail(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        panel = new PagedView();

        this.managerList = new WorkmanagerList(presenter);
        this.threadPools = new ThreadPoolEditor(presenter);

        panel.addPage("Overview", managerList.asWidget());
        panel.addPage("Thread Pools", threadPools.asWidget());

        // default page
        panel.showPage(0);

        return panel.asWidget();
    }

    public void setManagers(List<JcaWorkmanager> managers) {
        managerList.setManagers(managers);
    }

    public void setSelection(String selectedWorkmanager) {
        if(null==selectedWorkmanager)
            panel.showPage(0);
        else
        {
            threadPools.setContextName(selectedWorkmanager);
            panel.showPage(1);
        }
    }
}
