package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaSubsystemView extends SuspendableViewImpl implements JcaPresenter.MyView {

    private JcaPresenter presenter;
    private JcaBootstrapEditor boostrapEditor;
    private JcaBaseEditor baseEditor;
    private WorkmanagerEditor workmanagerEditor;

    @Override
    public void setPresenter(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        workmanagerEditor = new WorkmanagerEditor();
        boostrapEditor = new JcaBootstrapEditor();
        baseEditor = new JcaBaseEditor();

        tabLayoutpanel.add(baseEditor.asWidget(), "Common Config");
        tabLayoutpanel.add(boostrapEditor.asWidget(), "Boostrap Contexts");
        tabLayoutpanel.add(workmanagerEditor.asWidget(), "WorkManager");

        tabLayoutpanel.selectTab(0);

        // ----



        return tabLayoutpanel;
    }
}
