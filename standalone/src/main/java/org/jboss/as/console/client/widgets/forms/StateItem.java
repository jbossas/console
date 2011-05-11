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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 5/11/11
 */
public class StateItem extends FormItem<Boolean> {

    private Label widget;

    public StateItem(String name, String title) {
        super(name, title);
        this.widget = new Label();
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public boolean validate(Boolean value) {
        return true;
    }

    @Override
    public Boolean getValue() {
        return Boolean.valueOf(widget.getText());
    }

    @Override
    public void setValue(Boolean b) {
        widget.setText(String.valueOf(b));
    }
}
