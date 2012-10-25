package com.google.gwt.debugpanel.client;

import com.google.gwt.debugpanel.common.GwtStatisticsEventSystem;
import com.google.gwt.debugpanel.models.GwtDebugStatisticsModel;
import com.google.gwt.debugpanel.models.GwtExceptionModel;
import com.google.gwt.debugpanel.widgets.DebugPanelListener;
import com.google.gwt.debugpanel.widgets.DebugPanelWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 10/25/12
 */
public class DebugPanel implements IsWidget, DebugPanelListener {

    private GwtStatisticsEventSystem sys;
    private DefaultDebugStatisticsDebugPanelComponent panelComponent;
    //private DelayedDebugPanelComponent xmlComponent;
    private DefaultRawLogDebugPanelComponent logComponent;

    private GwtDebugStatisticsModel sm;
    private GwtExceptionModel em;
    private DebugPanelWidget widget;

    public DebugPanel() {
        sys = new GwtStatisticsEventSystem();
        panelComponent = new DefaultDebugStatisticsDebugPanelComponent(null);
        //xmlComponent = panelComponent.xmlComponent();
        logComponent = new DefaultRawLogDebugPanelComponent(sys);
        em = new GwtExceptionModel();
    }

    @Override
    public Widget asWidget() {

        widget = new DebugPanelWidget(
                this,
                true,
                new DebugPanelWidget.Component[] {
                        panelComponent,
                        new DefaultExceptionDebugPanelComponent(em),
                        logComponent
                });
        widget.show(true);
        return widget;
    }

    //@Override
    public void onShow() {
        panelComponent.reset(
                sm = new GwtDebugStatisticsModel(
                        new DefaultStatisticsModelStartupEventHandler(),
                        new DefaultStatisticsModelRpcEventHandler()));
        //xmlComponent.reset();
        logComponent.reset();
        logComponent.setVisible(true);
        sys.addListener(sm, false);
        sys.addListener(em, false);
        sys.enable(true);
    }

    //@Override
    public void onReset() {
        sys.removeListener(sm);
        sys.removeListener(em);

        panelComponent.reset(null);
        sys.clearEventHistory();

        widget.show(true);
    }
}
