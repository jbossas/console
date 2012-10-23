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
        outerElement.setAttribute("cssText", "width:100%!important");


        Element innerElement = panel.getElementById(innerId);
        innerElement.addClassName("stacked-bar-actual");
        innerElement.setInnerText(label);

        return panel;
    }

    public void setRatio(double total, double actual)
    {
        Element inner = panel.getElementById(innerId);
        double percentage = percentage(total, actual);
        if(percentage>0)
        {
            inner.setAttribute("style", "width:" + percentage + "%");
            inner.setAttribute("cssText", "width:" + percentage + "%");
        }
        else
        {
            inner.setAttribute("style", "background:none");
            inner.setAttribute("cssText", "background:none");
        }
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<span style='padding-right:5px;'>").appendEscaped(percentage + "%").appendHtmlConstant("</span>");
        inner.setInnerHTML(html.toSafeHtml().asString());
    }


    static double percentage(double total, double actual)
    {
        if(total==0 || actual==0)
            return 0;

        return Math.round((actual/total)*100);
    }

}
