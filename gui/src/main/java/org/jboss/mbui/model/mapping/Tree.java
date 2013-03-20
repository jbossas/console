package org.jboss.mbui.model.mapping;

import org.jboss.mbui.model.structure.QName;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {

    private Node<T> rootElement;

    /**
     * Return the root Node of the tree.
     * @return the root element.
     */
    public Node<T> getRootElement() {
        return this.rootElement;
    }

    public void setRootElement(Node<T> rootElement) {
        this.rootElement = rootElement;
    }

    public Node<T> findNode(final QName id) {
        List<Node<T>> results = new ArrayList<Node<T>>();
        walk(getRootElement(), results, new NodePredicate<T>() {
            @Override
            public boolean appliesTo(Node<T> node) {
                return id.equals(node.getId());
            }
        });
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Returns the Tree<T> as a List of Node<T> objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * @return a List<Node<T>>.
     */
    public List<Node<T>> toList() {
        List<Node<T>> list = new ArrayList<Node<T>>();
        walk(rootElement, list);
        return list;
    }

    /**
     * Returns a String representation of the Tree. The elements are generated
     * from a pre-order traversal of the Tree.
     * @return the String representation of the Tree.
     */
    public String toString() {
        return toList().toString();
    }



    /**
     * Walks the Tree in pre-order style. This is a recursive method, and is
     * called from the toList() method with the root element as the first
     * argument. It appends to the second argument, which is passed by reference     * as it recurses down the tree.
     * @param element the starting element.
     * @param list the output of the walk.
     */
    private void walk(Node<T> element, List<Node<T>> list) {
        walk(element, list, new NodePredicate<T>() {
            @Override
            public boolean appliesTo(Node<T> node) {
                return true;
            }
        });
    }


    private void walk(Node<T> element, List<Node<T>> list, NodePredicate<T> predicate) {
        if (predicate.appliesTo(element)) {
            list.add(element);
        }
        for (Node<T> data : element.getChildren()) {
            walk(data, list, predicate);
        }
    }

}


