package org.jboss.mbui.gui.behaviour;

/**
 * @author Heiko Braun
 * @date 11/20/12
 */
public interface DataDrivenCommand<T> {

    void execute(T data);
}
