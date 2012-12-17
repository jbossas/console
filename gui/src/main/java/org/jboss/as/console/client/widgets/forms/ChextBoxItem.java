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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.shared.expr.ExpressionAdapter;

/**
 * @author Heiko Braun
 * @date 3/2/11
 */
public class ChextBoxItem extends org.jboss.ballroom.client.widgets.forms.FormItem<Boolean> {

    private CheckBox checkBox;
    private final TextBox textBox;
    private final HorizontalPanel panel;

    public ChextBoxItem(String name, String title) {
        super(name, title);
        checkBox = new CheckBox();
        checkBox.setTabIndex(0);
        checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                setModified(true);
            }
        });
        setUndefined(false);

        checkBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                textBox.setText("");
            }
        });

        textBox = new TextBox();
        textBox.setStyleName("chextbox");
        textBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent blurEvent) {
                textBox.removeStyleName("chextbox-active");
            }
        });

        textBox.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent focusEvent) {
                textBox.addStyleName("chextbox-active");
            }
        });

        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                checkBox.setValue(Boolean.valueOf(textBox.getText()));
            }
        });

        panel = new HorizontalPanel();
        panel.add(checkBox);
        panel.add(textBox);


    }

    public Element getInputElement() {
        return checkBox.getElement().getFirstChildElement();
    }

    @Override
    public void resetMetaData() {
        super.resetMetaData();
        setUndefined(false); // implicitly defined
        checkBox.setValue(false);
    }

    @Override
    public Boolean getValue() {
        return checkBox.getValue();
    }

    @Override
    public void setExpressionValue(String expr) {
        super.setExpressionValue(expr);
        textBox.setText(expr);
    }

    @Override
    public void setValue(Boolean value) {
        checkBox.setValue(value);
        textBox.setText("");
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public void setEnabled(boolean b) {
        checkBox.setEnabled(b);
    }

    @Override
    public boolean validate(Boolean value) {
        return true;
    }

    @Override
    public void clearValue() {
        setValue(false);
    }
}
