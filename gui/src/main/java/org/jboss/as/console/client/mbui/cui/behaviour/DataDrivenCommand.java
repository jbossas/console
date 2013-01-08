package org.jboss.as.console.client.mbui.cui.behaviour;

/**
 * @author Heiko Braun
 * @date 11/20/12
 */
public interface DataDrivenCommand<T> {

    void execute(T data);
}
