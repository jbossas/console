package org.jboss.as.console.client.shared.subsys.ws;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Heiko Braun
 * @date 6/13/11
 */
public class WebServiceDescriptions {

    public static SafeHtml getEndpointDescription()
    {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<ul>");
        html.appendHtmlConstant("<li>");
        html.appendEscaped("context: Webservice endpoint context.") ;
        html.appendHtmlConstant("<li>");
        html.appendEscaped("class: Webservice implementation class.") ;
        html.appendHtmlConstant("<li>");
        html.appendEscaped("type: Webservice endpoint type.") ;
        html.appendHtmlConstant("<li>");
        html.appendEscaped("wsdl-url:Webservice endpoint WSDL URL.") ;
        html.appendHtmlConstant("</ul>");
        return html.toSafeHtml();
    }
}
