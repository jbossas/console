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

package org.jboss.as.console.client.widgets.tables;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 */
public class PopupCell extends AbstractCell<String> {

    private final SafeHtml html;
    private PopupPanel popup;

    public PopupCell(String title, Widget popupWidget) {
        super("click", "keydown");

        this.popup = new PopupPanel();
        this.popup.setStyleName("default-popup");
        this.popup.setWidget(popupWidget);
        this.html = new SafeHtmlBuilder().appendHtmlConstant( "<div tabindex=\"-1\" class='cell-popup'>"+title+"</div>").toSafeHtml();
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if ("click".equals(event.getType())) {
            onEnterKeyDown(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        sb.append(html);
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, String value,
                                  NativeEvent event, ValueUpdater<String> valueUpdater) {
        //delegate.execute(String.valueOf(context.getIndex()));
        popup.setPopupPosition(parent.getAbsoluteLeft(), parent.getAbsoluteTop());
        popup.show();
        popup.setAutoHideEnabled(true);
    }

}
