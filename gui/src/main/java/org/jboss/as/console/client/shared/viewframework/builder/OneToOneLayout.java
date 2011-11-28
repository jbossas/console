package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.ContentGroupLabel;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Heiko Braun
 * @date 11/28/11
 */
public class OneToOneLayout<T> {

    private LayoutPanel layout = null;

    private String title = "TITLE";
    private String headline = "HEADLINE";
    private String description = "DESCRIPTION";

    private Widget toolStrip = null;

    private NamedWidget master;
    private Widget masterTools = null;

    private NamedWidget detail;

    private List<NamedWidget> details = new ArrayList<NamedWidget>();

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
        if(!details.isEmpty())
            throw new IllegalStateException("Can either have single OR multiple details, but not both");
        this.detail = new NamedWidget(title, detail);
        return this;
    }

    public OneToOneLayout addDetail(String title, Widget detail)
    {
        if(detail!=null)
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

        if(null==master)
            throw new IllegalStateException("no master set");

        layout  = new LayoutPanel();

        FakeTabPanel titleBar = new FakeTabPanel(title);
        layout.add(titleBar);

        if(this.toolStrip !=null)
        {
            layout.add(toolStrip);
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setStyleName("rhs-content-panel");

        ScrollPanel scroll = new ScrollPanel(panel);
        layout.add(scroll);

        if(toolStrip!=null)
        {
            layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
            layout.setWidgetTopHeight(toolStrip, 28, Style.Unit.PX, 30, Style.Unit.PX);
            layout.setWidgetTopHeight(scroll, 58, Style.Unit.PX, 100, Style.Unit.PCT);
        }
        else
        {
            layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 28, Style.Unit.PX);
            layout.setWidgetTopHeight(scroll, 28, Style.Unit.PX, 100, Style.Unit.PCT);
        }


        panel.add(new ContentHeaderLabel(headline));
        panel.add(new HTML(description));

        if(master!=null)
        {
            panel.add(new ContentGroupLabel(master.title));
            if(masterTools!=null) panel.add(masterTools);
            panel.add(master.widget);
        }

        // -----

        if(detail!=null)
        {
            panel.add(new ContentGroupLabel(detail.title));
            panel.add(detail.widget);
        }
        else if(details.size()>0)
        {
            TabPanel tabs = new TabPanel();
            tabs.setStyleName("default-tabpanel");

            for(NamedWidget item : details)
            {
                tabs.add(item.widget, item.title);
            }

            panel.add(tabs);

            if(!details.isEmpty())
                tabs.selectTab(0);

        }

        return layout;
    }


}
