package org.jboss.as.console.client.widgets.forms;

/**
 * @author Heiko Braun
 * @date 3/29/11
 */
public interface InputElement<T> {
    T getValue();

    void setValue(T value);

    void setErroneous(boolean b);

    void setRequired(boolean required);

    boolean isErroneous();

    boolean isRequired();

    String getErrMessage();

    void setErrMessage(String errMessage);
}
