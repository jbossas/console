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

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public class PasswordBoxItem extends FormItem<String> {

    private PasswordTextBox textBox;
    private InputElementWrapper wrapper;

    public PasswordBoxItem(String name, String title) {
        super(name, title);

        textBox = new PasswordTextBox();
        textBox.setName(name);
        textBox.setTitle(title);

        wrapper = new InputElementWrapper(textBox, this);
    }

    @Override
    public Widget asWidget() {
        return wrapper;
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public void resetMetaData() {
        super.resetMetaData();
        textBox.setValue("");
    }

    @Override
    public void setValue(String value) {
        if(value!=null && !value.equals(""))
            isModified = true;
        textBox.setValue(value);
    }

    @Override
    public void setEnabled(boolean b) {
        textBox.setEnabled(b);
    }

    @Override
    public void setErroneous(boolean b) {
        super.setErroneous(b);
        wrapper.setErroneous(b);

    }

    @Override
    public boolean validate(String value) {
        if(isRequired() && value.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
