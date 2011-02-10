package org.jboss.as.console.client.server.subsys.threads;

import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import org.jboss.as.console.client.components.sgwt.DescriptionLabel;
import org.jboss.as.console.client.components.sgwt.TitleBar;

/**
 * @author Heiko Braun
 * @date 2/9/11
 */
public class ThreadManagementViewImpl extends ViewImpl implements ThreadManagementPresenter.MyView {

    ThreadManagementPresenter presenter;

    @Override
    public void setPresenter(ThreadManagementPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.setMembersMargin(5);

        TitleBar titleBar = new TitleBar("Thread Management");
        layout.addMember(titleBar);
        // TODO: text should be retrieved from model description
        layout.addMember(new DescriptionLabel("The threading subsystem, used to declare manageable thread pools and resources."));

        final TabSet topTabSet = new TabSet();
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setWidth100();
        topTabSet.setHeight100();

        Tab tTab1 = new Tab("Thread Factories");
        ThreadFactoryList factoryList = new ThreadFactoryList(presenter);
        tTab1.setPane(factoryList);

        // ---------------------------------------
        Tab tTab2 = new Tab("Unbounded Pools");

        Tab tTab3 = new Tab("Bounded Pools");

        Tab tTab4 = new Tab("Queueless Pools");

        Tab tTab5 = new Tab("Scheduled Pools");

        topTabSet.addTab(tTab1);
        topTabSet.addTab(tTab2);
        topTabSet.addTab(tTab3);
        topTabSet.addTab(tTab4);
        topTabSet.addTab(tTab5);

        layout.addMember(topTabSet);
        return layout;
    }
}