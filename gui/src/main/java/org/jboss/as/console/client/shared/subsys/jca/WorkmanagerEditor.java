package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;
import org.jboss.as.console.client.widgets.pages.PagedView;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/30/11
 */
public class WorkmanagerEditor {

    private JcaPresenter presenter;
    private WorkmanagerList managerList;
    private ThreadPoolEditor threadPools;
    PagedView panel;
    private List<JcaWorkmanager> managers;

    public WorkmanagerEditor(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        panel = new PagedView();

        this.managerList = new WorkmanagerList(presenter);
        this.threadPools = new ThreadPoolEditor(presenter);

        panel.addPage("&larr; Back", managerList.asWidget());
        panel.addPage("Thread Pools", threadPools.asWidget());

        // default page
        panel.showPage(0);


        return panel.asWidget();
    }

    public void setManagers(List<JcaWorkmanager> managers) {
        this.managers = managers;
        managerList.setManagers(managers);
    }

    public void setSelection(String selectedWorkmanager) {
        if(null==selectedWorkmanager)
            panel.showPage(0);
        else
        {
            threadPools.setContextName(selectedWorkmanager);

            if(managers!=null)
            {
                for(JcaWorkmanager mangager : managers)
                {
                    if(mangager.getName().equals(selectedWorkmanager))
                    {
                        threadPools.setWorkManager(mangager);
                        break;
                    }
                }
            }

            panel.showPage(1);
        }
    }
}
