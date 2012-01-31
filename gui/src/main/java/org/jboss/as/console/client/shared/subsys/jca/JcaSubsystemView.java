package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.dom.client.Style;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaArchiveValidation;
import org.jboss.as.console.client.shared.subsys.jca.model.JcaBootstrapContext;
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
    private WorkmanagerEditor workmanagerEditor;

    @Override
    public void setPresenter(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {

        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");

        baseEditor = new JcaBaseEditor(presenter);
        boostrapEditor = new JcaBootstrapEditor(presenter);
        workmanagerEditor = new WorkmanagerEditor(presenter);

        tabLayoutpanel.add(baseEditor.asWidget(), "Common Config", true);
        tabLayoutpanel.add(boostrapEditor.asWidget(), "Bootstrap Contexts", true);
        tabLayoutpanel.add(workmanagerEditor.asWidget(), "Work Manager", true);

        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
    }

    @Override
    public void setWorkManagers(List<JcaWorkmanager> managers) {
        workmanagerEditor.setManagers(managers);
        boostrapEditor.setManagers(managers);
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

    @Override
    public void setBootstrapContexts(List<JcaBootstrapContext> contexts) {
        boostrapEditor.setContexts(contexts);
    }

    @Override
    public void setSelectedWorkmanager(String selectedWorkmanager) {
        workmanagerEditor.setSelection(selectedWorkmanager);
    }
}
