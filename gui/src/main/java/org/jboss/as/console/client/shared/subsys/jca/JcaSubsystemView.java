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
    private JcaBaseEditor baseEditor;

    @Override
    public void setPresenter(JcaPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget createWidget() {


        TabLayoutPanel tabLayoutpanel = new TabLayoutPanel(25, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");


        baseEditor = new JcaBaseEditor();

        tabLayoutpanel.add(baseEditor.asWidget(), "Common JCA Config");
        //tabLayoutpanel.add(xaDataSourceEditor.asWidget(), Console.CONSTANTS.subsys_jca_dataSourcesXA());

        tabLayoutpanel.selectTab(0);

        // ----



        return tabLayoutpanel;
    }
}
