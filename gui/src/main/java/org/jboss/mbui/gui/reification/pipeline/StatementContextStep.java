package org.jboss.mbui.gui.reification.pipeline;

import org.jboss.mbui.gui.reification.Context;
import org.jboss.mbui.gui.reification.ReificationException;
import org.jboss.mbui.model.Dialog;
import org.jboss.mbui.model.mapping.Node;
import org.jboss.mbui.model.mapping.Tree;
import org.jboss.mbui.model.structure.Container;
import org.jboss.mbui.model.structure.InteractionUnit;
import org.jboss.mbui.model.structure.impl.InteractionUnitVisitor;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static org.jboss.mbui.model.structure.TemporalOperator.Choice;
import static org.jboss.mbui.model.structure.TemporalOperator.Deactivation;

/**
 * @author Heiko Braun
 * @date 3/19/13
 */
public class StatementContextStep<S extends Enum<S>> extends ReificationStep {


    public StatementContextStep() {
        super("statement context");

    }

    @Override
    public void execute(Dialog dialog, Context context) throws ReificationException {

        final Tree<Integer> statementContextTree = new Tree<Integer>();

        StatementContextVisitor visitor = new StatementContextVisitor(statementContextTree);
        dialog.getInterfaceModel().accept(visitor);

        dialog.setStatementContextShim(statementContextTree);

        System.out.println("Created "+visitor.getContextIds().size() +" context IDs");
    }

    abstract class Scope {

        Node<Integer> node;
        Integer previousContext = null;

        protected Scope(Node<Integer> container) {
            this.node = container;
        }

        public Integer getPreviousContext() {
            return previousContext;
        }

        protected Scope(Node<Integer> container, Integer previousContext) {
            this.node = container;
            this.previousContext = previousContext;
        }

        public Node<Integer> getNode() {
            return node;
        }

        abstract Integer getContextId();
    }

    class StatementContextVisitor implements InteractionUnitVisitor<S> {

        private final Tree<Integer> tree;
        private final Set<Integer> contextIds = new HashSet<Integer>();
        private Stack<Scope> stack = new Stack<Scope>();

        int scopeIdx = 0;

        public StatementContextVisitor(final Tree<Integer> tree) {
            this.tree = tree;
        }

        private Integer createContextId() {
            int contextId = ++scopeIdx;
            contextIds.add(contextId);
            return contextId;
        }

        public Set<Integer> getContextIds() {
            return contextIds;
        }

        @Override
        public void startVisit(Container container) {

            Node<Integer> containerNode = null;

            if(stack.isEmpty())
            {
                // top level: create new root node
                final Node<Integer> rootNode = new Node<Integer>(container.getId());
                rootNode.setData(createContextId());
                tree.setRootElement(rootNode);
                stack.push(new Scope(rootNode) {
                    @Override
                    Integer getContextId() {
                        return rootNode.getData();
                    }
                });

                containerNode = rootNode;
            }
            else
            {
                // child level: add new child & re-assign current container
                containerNode = stack.peek().getNode().addChild(container.getId());
            }

            boolean demarcationType = (Deactivation == container.getTemporalOperator() || Choice == container.getTemporalOperator());

            if(demarcationType)
            {
                // distinct context, new UUID
                stack.push(new Scope(containerNode, stack.peek().getContextId()) {
                    @Override
                    Integer getContextId() {
                        return createContextId();
                    }
                });

            }
            else
            {
                // re-use parent context id
                final Integer sharedContextId = stack.peek().getContextId();
                stack.push(new Scope(containerNode) {

                    @Override
                    Integer getContextId() {
                        return sharedContextId;
                    }
                });

            }

        }

        @Override
        public void visit(InteractionUnit<S> interactionUnit) {
            Scope scope = stack.peek();
            Node<Integer> node = scope.getNode().addChild(interactionUnit.getId());
            node.setData(stack.peek().getContextId());
        }

        @Override
        public void endVisit(Container container) {

            Scope scope = stack.pop();

            if(scope.getPreviousContext()!=null)
                scope.getNode().setData(scope.getPreviousContext());
            else
                scope.getNode().setData(scope.getContextId());

        }
    }
}
