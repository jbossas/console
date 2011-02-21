package org.jboss.as.console.client.forms;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Heiko Braun
 * @date 2/21/11
 */
public abstract class FormItem<T> {

    private T value;
    private String name;
    private String title;
    private boolean enabled;

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

    public abstract Widget asWidget();

    public void setEnabled(boolean b)
    {
        this.enabled = b;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
