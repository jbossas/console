package org.jboss.as.console.client.shared.runtime.charts;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 12/8/11
 */
public class StackedBar {

    String outerId = HTMLPanel.createUniqueId();
    String innerId = HTMLPanel.createUniqueId();
    HTMLPanel panel;
    String label = "";

    public StackedBar(String label) {
        this.label = label;
    }

    public StackedBar() {
    }

    public Widget asWidget() {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        builder.appendHtmlConstant("<div id='"+ outerId +"'><div id='"+innerId+"'/></div>");

        panel = new HTMLPanel(builder.toSafeHtml());

        Element outerElement = panel.getElementById(outerId);
        outerElement.addClassName("stacked-bar-total");
        outerElement.setAttribute("style", "width:100%");


        Element innerElement = panel.getElementById(innerId);
        innerElement.addClassName("stacked-bar-actual");
        innerElement.setInnerText(label);
        //innerElement.setAttribute("style", "width:0%");

        return panel;
    }

    public void setRatio(long total, long actual)
    {
        Element inner = panel.getElementById(innerId);
        long percentage = percentage(total, actual);
        if(percentage>0)
            inner.setAttribute("style", "width:" + percentage + "%");
        else
            inner.setAttribute("style", "background:none");
        inner.setInnerHTML(percentage+"%");
    }


    static long percentage(long total, long actual)
    {
        if(total==0 || actual==0)
            return 0;

        return Long.valueOf(Math.abs((actual/total)*100)).longValue();
    }

}
