package org.jboss.mbui.gui.behaviour;

/**
 * @author Heiko Braun
 * @date 1/22/13
 */
public interface StatementContext {
    String resolve(String key);
    String[] resolveTuple(String key);
}
