/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
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

package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 3/3/11
 */
public class FieldsetRenderer implements GroupRenderer {

    String id;

    public FieldsetRenderer() {
        this.id = "formGroup_"+HTMLPanel.createUniqueId();
    }

    @Override
    public Widget render(RenderMetaData metaData, String groupName, Map<String, FormItem> groupItems) {

        SafeHtmlBuilder builder = new SafeHtmlBuilder();

        builder.appendHtmlConstant("<fieldset id='"+id+"' class='default-fieldset'>");
        builder.appendHtmlConstant("<legend class='default-legend'>").appendEscaped(groupName).appendHtmlConstant("</legend>");
        builder.appendHtmlConstant("</fieldset>");

        HTMLPanel html = new HTMLPanel(builder.toSafeHtml());

        DefaultGroupRenderer defaultGroupRenderer = new DefaultGroupRenderer();
        Widget defaultGroupWidget = defaultGroupRenderer.render(metaData, "", groupItems);
        html.add(defaultGroupWidget,id);

        return html;
    }
}
