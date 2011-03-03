package org.jboss.as.console.client.widgets.forms;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public abstract class FormItem<T> {

    private T value;
    protected String name;
    protected String title;

    private boolean isKey;

    public FormItem(String name, String title) {
        this.name = name;
        this.title = title;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

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

    public abstract Widget asWidget();

    public abstract void setEnabled(boolean b);

}
