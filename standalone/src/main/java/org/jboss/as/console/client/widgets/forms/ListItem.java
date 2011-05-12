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

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * @author Heiko Braun
 * @date 5/12/11
 */
public class ListItem extends FormItem<List> {

    private TextArea textArea;
    private List value;

    public ListItem(String name, String title) {
        super(name, title);
        this.textArea = new TextArea();
    }

    @Override
    public Widget asWidget() {
        return textArea;
    }

    @Override
    public void setEnabled(boolean b) {
        this.textArea.setEnabled(b);
    }

    @Override
    public boolean validate(List value) {
        return true;
    }

    @Override
    public List getValue() {

        String[] split = textArea.getText().split("\n");
        value.clear();

        for(String s : split)
            value.add(s);

        return value;
    }

    @Override
    public void setValue(List list) {
        this.value = list;
        this.textArea.setText("");
        this.textArea.setVisibleLines(list.size());
        for(Object item : list)
        {
            textArea.setText(textArea.getText()+item.toString()+"\n");
        }
    }
}
