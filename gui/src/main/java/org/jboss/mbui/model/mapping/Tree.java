package org.jboss.mbui.model.mapping;

import org.jboss.mbui.model.structure.QName;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {

    private Node<T> rootElement;

    public Tree(T root) {
        this.rootElement = new Node<T>(root);
    }

    /**
     * Return the root Node of the tree.
     * @return the root element.
     */
    public Node<T> getRootElement() {
        return this.rootElement;
    }

    public Node<T> findNode(final QName id) {
        List<Node<T>> results = new ArrayList<Node<T>>();
        walk(getRootElement(), results, new Predicate<T>() {
            @Override
            public boolean appliesTo(Node<T> node) {
                return id.equals(node.id);
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
        walk(element, list, new Predicate<T>() {
            @Override
            public boolean appliesTo(Node<T> node) {
                return true;
            }
        });
    }


    private void walk(Node<T> element, List<Node<T>> list, Predicate<T> predicate) {
        if (predicate.appliesTo(element)) {
            list.add(element);
        }
        for (Node<T> data : element.getChildren()) {
            walk(data, list, predicate);
        }
    }

    public Node<T> findParent(Node<T> from, Predicate predicate)
    {
        /*T mapping = getMapping(type);
        if (mapping != null)
        {
            // check predicate
            if (predicate != null)
            {
                mapping = (predicate.appliesTo(mapping)) ? mapping : null;
            }

            // complement the mapping (i.e. resource address at a higher level)
            if(mapping!=null && parent!=null)
            {
                Mapping parentMapping = parent.findMapping(type);
                if(parentMapping!=null)
                    mapping.complementFrom(parentMapping);
            }

        }
        if (mapping == null && parent != null)
        {
            mapping = (T) parent.findMapping(type);
        }

        return mapping;     */

        return null;
    }

    public interface Predicate<T> {
        boolean appliesTo(Node<T> node);
    }

    /**
     * Represents a node of the Tree<T> class. The Node<T> is also a container, and
     * can be thought of as instrumentation to determine the location of the type T
     * in the Tree<T>.
     */
    public class Node<T> {

        private final T data;
        private QName id;
        private Node<T> parent;
        private List<Node<T>> children;

        /**
         * Convenience ctor to create a Node<T> with an instance of T.
         * @param data an instance of T.
         */
        public Node(final T data) {
            this.data = data;
        }

        public Node(QName id, T data) {
            this.id = id;
            this.data = data;
        }

        /**
         * Finds the parent which satisfies the predicate. Starts with the parent of this node not the node itself.
         * @param predicate
         * @return
         */
        public Node<T> findParent(Predicate<T> predicate) {
            return findInternal(this.parent, predicate);
        }

        private Node<T> findInternal(Node<T> parent, Predicate<T> predicate) {
            if (parent != null) {
                if (predicate.appliesTo(parent)) {
                    return parent;
                }
                else {
                    return findInternal(parent.parent, predicate);
                }
            }
            return null;
        }


        /**
         * Return the children of Node<T>. The Tree<T> is represented by a single
         * root Node<T> whose children are represented by a List<Node<T>>. Each of
         * these Node<T> elements in the List can have children. The getChildren()
         * method will return the children of a Node<T>.
         * @return the children of Node<T>
         */
        public List<Node<T>> getChildren() {
            if (this.children == null) {
                return new ArrayList<Node<T>>();
            }
            return this.children;
        }



        /**
         * Returns the number of immediate children of this Node<T>.
         * @return the number of immediate children.
         */
        public int getNumberOfChildren() {
            if (children == null) {
                return 0;
            }
            return children.size();
        }

        public void addChild(QName id, T data) {
            addChild(new Node<T>(id, data));
        }


        /**
         * Adds a child to the list of children for this Node<T>. The addition of
         * the first child will create a new List<Node<T>>.
         * @param child a Node<T> object to set.
         */
        public void addChild(Node<T> child) {
            if (children == null) {
                children = new ArrayList<Node<T>>();
            }
            child.parent = this;
            children.add(child);
        }

        /**
         * Inserts a Node<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
         * @param index the position to insert at.
         * @param child the Node<T> object to insert.
         * @throws IndexOutOfBoundsException if thrown.
         */
        public void insertChildAt(int index, Node<T> child) throws IndexOutOfBoundsException {
            if (index == getNumberOfChildren()) {
                // this is really an append
                addChild(child);
                return;
            } else {
                children.get(index); //just to throw the exception, and stop here
                children.add(index, child);
            }
        }

        public boolean removeChild(Node<T> child) {
            return children.remove(child);
        }


        /**
         * Remove the Node<T> element at index index of the List<Node<T>>.
         * @param index the index of the element to delete.
         * @throws IndexOutOfBoundsException if thrown.
         */
        public void removeChildAt(int index) throws IndexOutOfBoundsException {
            Node<T> child = children.get(index);
            child.parent = null;
            children.remove(index);
        }

        public T getData() {
            return this.data;
        }



        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{").append(getData().toString()).append(",[");
            int i = 0;
            for (Node<T> e : getChildren()) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(e.getData().toString());
                i++;
            }
            sb.append("]").append("}");
            return sb.toString();
        }
    }
}


