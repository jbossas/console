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

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public abstract class FormItem<T> implements InputElement<T> {

    private T value;
    protected String name;
    protected String title;

    protected  boolean isKey;

    protected boolean isErroneous = false;
    protected boolean isRequired = true;
    protected String errMessage = "Invalid input";

    protected boolean isModified = false;

    public FormItem(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    @Override
    public void setErroneous(boolean b) {
        this.isErroneous = b;
    }

    @Override
    public void setRequired(boolean required) {
        isRequired = required;
    }

    @Override
    public boolean isErroneous() {
        return isErroneous;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public String getErrMessage() {
        return errMessage;
    }

    @Override
    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public boolean isModified() {
        return isModified;
    }

    protected void resetMetaData() {
        isModified = false;
        setErroneous(false);
    }

    public abstract Widget asWidget();

    public abstract void setEnabled(boolean b);

    public abstract boolean validate(T value);
}
