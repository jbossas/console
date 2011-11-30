package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaConnectionManager;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaWorkmanager;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/29/11
 */
public class JcaSubsystemView extends SuspendableViewImpl implements JcaPresenter.MyView {

    private JcaPresenter presenter;
    private JcaBootstrapEditor boostrapEditor;
    private JcaBaseEditor baseEditor;
    private WorkmanagerListView workmanagerEditor;

    @Override
    public void setPresenter(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        workmanagerEditor = new WorkmanagerListView(presenter);
        boostrapEditor = new JcaBootstrapEditor();
        baseEditor = new JcaBaseEditor();

        tabLayoutpanel.add(baseEditor.asWidget(), "Common Config");
        tabLayoutpanel.add(boostrapEditor.asWidget(), "Boostrap Contexts");
        tabLayoutpanel.add(workmanagerEditor.asWidget(), "WorkManager");

        tabLayoutpanel.selectTab(0);

        // ----



        return tabLayoutpanel;
    }

    @Override
    public void setWorkManagers(List<JcaWorkmanager> managers) {
        workmanagerEditor.setManagers(managers);
    }

    @Override
    public void setBeanSettings(JcaBeanValidation jcaBeanValidation) {
        baseEditor.setBeanSettings(jcaBeanValidation);
    }

    @Override
    public void setArchiveSettings(JcaArchiveValidation jcaArchiveValidation) {
        baseEditor.setArchiveSettings(jcaArchiveValidation);
    }

    @Override
    public void setCCMSettings(JcaConnectionManager jcaConnectionManager) {
        baseEditor.setCCMSettings(jcaConnectionManager);
    }
}
