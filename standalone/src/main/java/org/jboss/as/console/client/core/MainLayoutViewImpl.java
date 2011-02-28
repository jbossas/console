package org.jboss.as.console.client.core;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.message.Message;

/**
 * The main console layout that builds on GWT 2.1 layout panels.
 *
 * @author Heiko Braun
 */
public class MainLayoutViewImpl extends ViewImpl
        implements MainLayoutPresenter.MainLayoutView {

    private DockLayoutPanel panel;

    private LayoutPanel headerPanel;
    private LayoutPanel mainContentPanel;
    private LayoutPanel footerPanel;

    private Header header;

    @Inject
    public MainLayoutViewImpl() {

        mainContentPanel = new LayoutPanel();
        mainContentPanel.setStyleName("main-content-panel");

        headerPanel = new LayoutPanel();
        headerPanel.setStyleName("header-panel");

        footerPanel = new LayoutPanel();
        footerPanel.setStyleName("footer-panel");

        panel = new DockLayoutPanel(Style.Unit.PX);
        panel.addNorth(headerPanel, 64);
        panel.addSouth(footerPanel, 30);
        panel.add(mainContentPanel);

        header = Console.MODULES.getHeader();
        getHeaderPanel().add(header.asWidget());

        getFooterPanel().add(Console.MODULES.getFooter().asWidget());
    }

    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {

        if (slot == MainLayoutPresenter.TYPE_SetMainContent) {
            if(content!=null)
                setMainContent(content);
        }
        else {
            Console.MODULES.getMessageCenter().notify(
                    new Message("Unknown slot requested:" + slot)
            );
        }
    }

    public void setMainContent(Widget content) {
        mainContentPanel.clear();

        if (content != null) {
            mainContentPanel.add(content);
        }
    }

    public LayoutPanel getHeaderPanel() {
        return headerPanel;
    }

    public LayoutPanel getFooterPanel() {
        return footerPanel;
    }

}