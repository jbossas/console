/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.core.message;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class MessageCell extends AbstractCell<Message> {

    /*interface Template extends SafeHtmlTemplates {
        @Template("<div class=\"{0}\">{1}</div>")
        SafeHtml message(String cssClass, String title);
    } */

    //private static final Template TEMPLATE = GWT.create(Template.class);


    @Override
    public void render(
            Context context,
            Message message,
            SafeHtmlBuilder safeHtmlBuilder)
    {


        ImageResource icon = MessageCenterView.getSeverityIcon(message.getSeverity());
        AbstractImagePrototype prototype = AbstractImagePrototype.create(icon);

        String styles = (context.getIndex() %2 > 0) ? "message-list-item message-list-item-odd" : "message-list-item";
        String rowStyle= message.isNew()  ? "" : "class='message-list-item-old'";

        safeHtmlBuilder.appendHtmlConstant("<table width='100%' cellpadding=4 cellspacing=0>");
        safeHtmlBuilder.appendHtmlConstant("<tr valign='middle' "+rowStyle+">");
        safeHtmlBuilder.appendHtmlConstant("<td width=16>");
        safeHtmlBuilder.appendHtmlConstant(prototype.getHTML());
        safeHtmlBuilder.appendHtmlConstant("</td><td width='100%'>");
        safeHtmlBuilder.appendHtmlConstant("<div class='"+rowStyle+"'>");
        String actualMessage = message.getConciseMessage().length()>30 ? message.getConciseMessage().substring(0, 30)+" ..." : message.getConciseMessage();

        //safeHtmlBuilder.appendHtmlConstant(TEMPLATE.message(styles, actualMessage));
        safeHtmlBuilder.appendHtmlConstant(actualMessage);
        safeHtmlBuilder.appendHtmlConstant("</div>");


        safeHtmlBuilder.appendHtmlConstant("</td></tr></table>");

    }

}

