package org.jboss.mbui.gui.behaviour;

import java.util.LinkedList;

/**
 *
 * TODO: Semantics of resolve() versus get(). IMO the later would be correct and resolve should go ...
 * It might better to leave the resolution semantic to a specific implementation (parent delegation, namespace based, etc)
 *
 * @author Heiko Braun
 * @date 1/22/13
 */
public interface StatementContext {


    /**
     * Get a value matching the key.
     * Scoped to current context.
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * Get a tuple matching the key.
     * Scoped to current context.
     *
     * @param key
     * @return
     */
    String[] getTuple(String key);

    /**
     * Resolves a value matching the key.
     * In a hierarchy of contexts this will match the first occurrence.
     *
     * @param key
     * @return
     */
    String resolve(String key);

    /**
     * Resolves a tuple matching the key.
     * In a hierarchy of contexts this will match the first occurrence.
     *
     * @param key
     * @return
     */
    String[] resolveTuple(String key);

    /**
     * Collects all values matching a key.
     * In a hierarchy of contexts the list will be sorted from child (n) to parent (n+1).
     * n being the list index.
     *
     * @param key
     * @return
     */
    LinkedList<String> collect(String key);

    /**
     * Collects all tuples matching a key.
     * In a hierarchy of contexts the list will be sorted from child (n) to parent (n+1).
     * n being the list index.
     *
     * @param key
     * @return
     */
    LinkedList<String[]> collectTuples(String key);
}
