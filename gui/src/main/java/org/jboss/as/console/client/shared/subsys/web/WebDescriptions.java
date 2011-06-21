package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author Heiko Braun
 * @date 6/9/11
 */
public class WebDescriptions {

    public static SafeHtml getJSPConfigDescription() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<ul>");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("development:If true, the frequency at which JSPs are checked for modification may be specified via the checkInterval parameter. true or false, default true.");

        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("display source: should a source fragment be included in exception messages? true or false, default true.");

        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("keep generated: Should we keep the generated Java source code for each page instead of deleting it? true or false, default true.");

        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("check interval: If development is false and checkInterval is greater than zero, background compiles are enabled. checkInterval is the time in seconds between checks to see if a JSP page (and its dependent files) needs to be recompiled. Default 0 seconds.");

        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("listings: Display directory listings?.");

        /*builder.appendHtmlConstant("<li>");
        builder.appendEscaped("advertise: Set this attribute to true to cause JBoss Web to advertise support for the Srevlet specification using the header recommended in the specification. The default value is false.");
        */
        builder.appendHtmlConstant("</ul>");

        return builder.toSafeHtml();
    }
}
