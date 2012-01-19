package org.jboss.as.console.client.shared.runtime.jpa;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.runtime.jpa.model.JPADeployment;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 1/19/12
 */
public class JPAMetricsView extends SuspendableViewImpl implements JPAMetricPresenter.MyView {
    private JPAMetricPresenter presenter;
    private PagedView pages;
    private DeploymentList deploymentList;

    @Override
    public void setPresenter(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        pages = new PagedView();

        this.deploymentList = new DeploymentList(presenter);

        pages.addPage(Console.CONSTANTS.common_label_back(), deploymentList.asWidget());

        // default page
        pages.showPage(0);

        LayoutPanel layout = new LayoutPanel();

        // Top Most Tab
        FakeTabPanel titleBar = new FakeTabPanel("JPA");
        layout.add(titleBar);

        Widget pagesWidget = pages.asWidget();
        layout.add(pagesWidget);

        layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
        layout.setWidgetTopHeight(pagesWidget, 40, Style.Unit.PX, 100, Style.Unit.PCT);

        return layout;

    }

    @Override
    public void setJpaUnits(List<JPADeployment> jpaUnits) {
        deploymentList.setUnits(jpaUnits);
    }
}
