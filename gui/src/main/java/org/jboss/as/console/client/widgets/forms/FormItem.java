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
 * Form item abstraction.
 *
 * @author Heiko Braun
 * @date 2/21/11
 */
public abstract class FormItem<T> implements InputElement<T> {

    protected String name;
    protected String title;

    protected boolean isErroneous = false;
    protected boolean isRequired = true;
    protected boolean isModified = false;
    protected boolean isUndefined = true;

    protected String errMessage = "Invalid input";

    public FormItem(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
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

    void setModified(boolean modified) {
        isModified = modified;
    }

    public boolean isUndefined() {
        return isUndefined;
    }

    void setUndefined(boolean undefined) {
        isUndefined = undefined;
    }

    protected void resetMetaData() {
        isModified = false;
        isUndefined = true;
        setErroneous(false);
    }

    public abstract Widget asWidget();

    public abstract void setEnabled(boolean b);

    public abstract boolean validate(T value);
}
