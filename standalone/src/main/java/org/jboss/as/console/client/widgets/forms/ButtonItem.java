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

package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.widgets.DefaultButton;

/**
 * @author Heiko Braun
 * @date 3/9/11
 */
public class ButtonItem extends FormItem<Boolean> {

    protected DefaultButton button;

    public ButtonItem(String name, String title) {
        super(name, title);
        this.button = new DefaultButton(title);
    }

    @Override
    public Boolean getValue() {
        return true;
    }

    @Override
    public void setValue(Boolean value) {

    }

    @Override
    public Widget asWidget() {
        return button;
    }

    @Override
    public void setEnabled(boolean b) {
        button.setEnabled(b);
    }

    public void addClickHandler(ClickHandler handler)
    {
        this.button.addClickHandler(handler);
    }

    @Override
    public boolean validate(Boolean value) {
        return true;
    }
}
