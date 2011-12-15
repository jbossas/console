package org.jboss.as.console.client.shared.viewframework.builder;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple row based layout for RHS content sections
 *
 * @author Heiko Braun
 * @date 11/28/11
 */
public class SimpleLayout {

    private LayoutPanel layout = null;

    private String title = "TITLE";
    private String headline = "HEADLINE";
    private String description = "DESCRIPTION";

    private Widget toolStrip = null;

    private List<NamedWidget> details = new ArrayList<NamedWidget>();
    private boolean isPlain = false;
    private Widget headlineWidget;

    public SimpleLayout setPlain(boolean isPlain)
    {
        this.isPlain = isPlain;
        return this;
    }

    public SimpleLayout setTitle(String title)
    {
        this.title = title;
        return this;
    }

    public SimpleLayout setTopLevelTools(Widget toolstrip)
    {
        this.toolStrip = toolstrip;
        return this;
    }

    public SimpleLayout addContent(String title, Widget detail)
    {
        details.add(new NamedWidget(title, detail));
        return this;
    }

    public SimpleLayout setDescription(String description) {
        this.description = description;
        return this;
    }

    public SimpleLayout setHeadline(String headline) {
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

        panel.add(new HTML(description));




        for(NamedWidget item : details)
        {
            panel.add(item.widget);
            item.widget.getElement().addClassName("fill-layout-width");
        }
        return layout;
    }


    public SimpleLayout setHeadlineWidget(Widget header) {
        this.headlineWidget = header;
        return this;
    }
}
