package org.jboss.mbui.gui.behaviour;

import org.jboss.mbui.gui.behaviour.Constants;
import org.jboss.mbui.gui.behaviour.StatementContext;

import java.lang.Override;
import java.lang.String;
import java.util.LinkedList;

/**
 * @author Heiko Braun
 * @date 3/21/13
 */
public abstract class DelegatingStatementContext implements StatementContext {
    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String[] getTuple(String key) {
        return null;
    }

    @Override
    public LinkedList<String> collect(String key) {
        return Constants.EMPTY_LIST;
    }

    @Override
    public LinkedList<String[]> collectTuples(String key) {
        return Constants.EMPTY_TUPLE_LIST;
    }
}
