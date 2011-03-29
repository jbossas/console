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

    private boolean isKey;

    private boolean isErroneous =   false;
    private boolean isRequired =    true;
    private String errMessage = "Invalid input";

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

    public abstract Widget asWidget();

    public abstract void setEnabled(boolean b);

    public abstract boolean validate(T value);
}
