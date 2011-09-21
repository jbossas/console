package org.jboss.as.console.client.shared.subsys.messaging.model;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * workaround for non existing resource documentation
 * @author Heiko Braun
 * @date 6/9/11
 */
@Deprecated
public class MessagingDescription {

    private static SafeHtml addressingDescription;

    public static SafeHtml getProviderDescription()
    {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<ul>");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("acceptor: An acceptor defines a way in which connections can be made to the HornetQ server.");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("connector: A connector can be used by a client to define how it connects to a server.");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("persistence-enabled: Is message persistence enabled.");
        builder.appendHtmlConstant("</ul>");

        return builder.toSafeHtml();
    }

    public static SafeHtml getSecurityDescription()
    {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<ul>");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("A security setting allows sets of permissions to be defined against queues based on their address.");
        builder.appendHtmlConstant("</ul>");

        return builder.toSafeHtml();
    }

    public static SafeHtml getAddressingDescription() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<ul>");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("An address setting defines some attributes that are defined against an address wildcard rather than a specific queue.");
        builder.appendHtmlConstant("</ul>");

        return builder.toSafeHtml();
    }

    public static SafeHtml getFactoryDescription() {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendHtmlConstant("<ul>");
        builder.appendHtmlConstant("<li>");
        builder.appendEscaped("The JMS connection factories.");
        builder.appendHtmlConstant("</ul>");

        return builder.toSafeHtml();
    }
}
