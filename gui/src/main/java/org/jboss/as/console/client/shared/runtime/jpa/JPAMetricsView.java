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
    private PersistenceUnitList deploymentList;
    private BasicMetrics basicMetrics;
    private List<JPADeployment> units;
    private String[] currentToken;

    @Override
    public void setPresenter(JPAMetricPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        pages = new PagedView();

        this.deploymentList = new PersistenceUnitList(presenter);
        this.basicMetrics = new BasicMetrics(this.presenter);

        pages.addPage(Console.CONSTANTS.common_label_back(), deploymentList.asWidget());
        pages.addPage("Basic Metrics", basicMetrics.asWidget());

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

        this.units = jpaUnits;
        deploymentList.setUnits(jpaUnits);
    }

    @Override
    public void setSelectedUnit(String[] tokens) {

        currentToken = tokens;

        if(null==tokens)
            pages.showPage(0);
        else
        {
            basicMetrics.setContextName(tokens);

            if(units!=null)
            {
                for(JPADeployment unit : units)
                {
                    if(unit.getPersistenceUnit().equals(tokens[1]))
                    {
                        basicMetrics.setUnit(unit);
                        break;
                    }
                }
            }


            // load the actual metric data
            presenter.loadMetrics(tokens);

            pages.showPage(1);
        }
    }

    @Override
    public void updateMetric(UnitMetric unitMetric) {

        if(unitMetric.isEnabled())
        {
            basicMetrics.updateMetric(unitMetric);
        }
        else
        {
            Console.error(Console.MESSAGES.subsys_jpa_err_mericDisabled(currentToken[0]));
        }
    }

    @Override
    public void clearValues() {
        basicMetrics.clearValues();
    }
}
