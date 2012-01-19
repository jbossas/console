package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.widgets.pages.PagedView;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class JPAMetricsView extends SuspendableViewImpl implements JPAMetricPresenter.MyView {
    private JPAMetricPresenter presenter;
    private PagedView panel;
    private DeploymentList deploymentList;

    @Override
    public void setPresenter(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        panel = new PagedView();

        this.deploymentList = new DeploymentList(presenter);

        panel.addPage(Console.CONSTANTS.common_label_back(), deploymentList.asWidget());
       // panel.addPage("Thread Pools", threadPools.asWidget());

        // default page
        panel.showPage(0);


        return panel.asWidget();
    }
}
