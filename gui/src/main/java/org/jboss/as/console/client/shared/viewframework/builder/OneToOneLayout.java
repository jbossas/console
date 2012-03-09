package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.ContentDescription;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class OneToOneLayout {

    private LayoutPanel layout = null;

    private String title = "TITLE";
    private String headline = "HEADLINE";
    private String description = "DESCRIPTION";

    private Widget toolStrip = null;

    private NamedWidget master;
    private Widget masterTools = null;

    private NamedWidget detail;

    private List<NamedWidget> details = new ArrayList<NamedWidget>();
    private boolean isPlain = false;
    private Widget headlineWidget;

    public OneToOneLayout setPlain(boolean isPlain)
    {
        this.isPlain = isPlain;
        return this;
    }

    public OneToOneLayout setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public OneToOneLayout setTopLevelTools(Widget toolstrip)
    {
        this.toolStrip = toolstrip;
        return this;
    }

    public OneToOneLayout setMasterTools(Widget toolstrip)
    {
        this.masterTools = toolstrip;
        return this;
    }

    public OneToOneLayout setMaster(String title, Widget master)
    {
        this.master = new NamedWidget(title, master);
        return this;
    }

    public OneToOneLayout setDetail(String title, Widget detail)
    {
        if(!this.details.isEmpty())
            throw new IllegalStateException("Can either have single OR multiple details, but not both");
        this.detail = new NamedWidget(title, detail);
        return this;
    }

    public OneToOneLayout addDetail(String title, Widget detail)
    {
        if(this.detail!=null)
            throw new IllegalStateException("Can either have single OR multiple details, but not both");
        details.add(new NamedWidget(title, detail));
        return this;
    }

    public OneToOneLayout setDescription(String description) {
        this.description = description;
        return this;
    }

    public OneToOneLayout setHeadline(String headline) {
        this.headline = headline;
        return this;
    }

    public Widget build() {

        layout  = new LayoutPanel();
        layout.setStyleName("fill-layout");

        FakeTabPanel titleBar = null;
        if(!isPlain) {
            titleBar = new FakeTabPanel(title);
            layout.add(titleBar);
        }

        if(this.toolStrip !=null)
        {
            layout.add(toolStrip);
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        // titlebar offset, if exists
        int offset = isPlain ? 0 : 40;

        if(toolStrip!=null)
        {
            if(!isPlain) layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
            layout.setWidgetTopHeight(toolStrip, offset, Style.Unit.PX, 30, Style.Unit.PX);
            layout.setWidgetTopHeight(scroll, offset+30, Style.Unit.PX, 100, Style.Unit.PCT);
        }
        else
        {
            if(!isPlain) layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
            layout.setWidgetTopHeight(scroll, offset, Style.Unit.PX, 100, Style.Unit.PCT);
        }


        if(null==headlineWidget)
        {
            panel.add(new ContentHeaderLabel(headline));
        }
        else
        {
            panel.add(headlineWidget);
        }

        panel.add(new ContentDescription(description));

        if(master!=null)
        {
            if(master.title!=null && !master.title.isEmpty())
                panel.add(new ContentGroupLabel(master.title));

            if(masterTools!=null) panel.add(masterTools);

            master.widget.getElement().setAttribute("role", "application");
            panel.add(master.widget);
        }

        // -----

        if(detail!=null)
        {
            if(detail.title!=null && !detail.title.isEmpty())
                panel.add(new ContentGroupLabel(detail.title));
            panel.add(detail.widget);
            detail.widget.getElement().addClassName("fill-layout-width");
            detail.widget.getElement().setAttribute("role", "application");
        }
        else if(details.size()>0)
        {
            TabPanel tabs = new TabPanel();
            tabs.setStyleName("default-tabpanel");
            tabs.getElement().setAttribute("style", "margin-top:15px;");

            for(NamedWidget item : details)
            {
                tabs.add(item.widget, item.title);
                item.widget.getElement().addClassName("fill-layout-width");
                item.widget.getElement().setAttribute("role", "application");
            }

            panel.add(tabs);

            if(!details.isEmpty())
                tabs.selectTab(0);

        }

        return layout;
    }


    public OneToOneLayout setHeadlineWidget(Widget header) {
        this.headlineWidget = header;
        return this;
    }
}
