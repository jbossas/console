package org.jboss.as.console.client.shared.general.validation;


import java.util.LinkedList;
import java.util.List;

/**
 * A simple binary decision tree.
 *
 * @param <T> the data to run decisions on.
 */
public class DecisionTree<T> {

    private T entity;
    private List<String> decisionLog = new LinkedList<String>();

    public class BinTree {

        private int     nodeID;
        private String  questOrAns = null;
        private BinTree yesBranch  = null;
        private BinTree noBranch   = null;
        private Decision decision;

        public BinTree(int newNodeID, String newQuestAns, Decision decision) {
            nodeID     = newNodeID;
            questOrAns = newQuestAns;
            this.decision = decision;
        }

        public String getQuestOrAns() {
            return questOrAns;
        }
    }

    BinTree rootNode = null;

    public DecisionTree(T entity) {
        this.entity = entity;
    }

    public List<String> getDecisionLog() {
        return decisionLog;
    }

    public String dumpDecisionLog() {
        StringBuilder sb = new StringBuilder();
        for(String s : decisionLog)
            sb.append(s).append("\n");
        return sb.toString();

    }

    /* CREATE ROOT NODE */

    public void createRoot(int newNodeID, String newQuestAns, Decision decision) {
        rootNode = new BinTree(newNodeID,newQuestAns, decision);
    }

    /* ADD YES NODE */

    public void yes(int existingNodeID, int newNodeID, String newQuestAns, Decision decision) {
        // If no root node do nothing

        if (rootNode == null) {
            System.out.println("ERROR: No root node!");
            return;
        }

        // Search tree

        if (searchTreeAndAddYesNode(rootNode,existingNodeID,newNodeID,newQuestAns, decision)) {
          //  System.out.println("Added node " + newNodeID +
            //        " onto \"yes\" branch of node " + existingNodeID);
        }
        else System.out.println("Node " + existingNodeID + " not found");
    }

    /* SEARCH TREE AND ADD YES NODE */

    private boolean searchTreeAndAddYesNode(
            BinTree currentNode,
            int existingNodeID, int newNodeID, String newQuestAns,
            Decision decision) {
        if (currentNode.nodeID == existingNodeID) {
            // Found node
            if (currentNode.yesBranch == null) currentNode.yesBranch = new
                    BinTree(newNodeID,newQuestAns, decision);
            else {
                System.out.println("WARNING: Overwriting previous node " +
                        "(id = " + currentNode.yesBranch.nodeID +
                        ") linked to yes branch of node " +
                        existingNodeID);
                currentNode.yesBranch = new BinTree(newNodeID,newQuestAns, decision);
            }
            return(true);
        }
        else {
            // Try yes branch if it exists
            if (currentNode.yesBranch != null) {
                if (searchTreeAndAddYesNode(currentNode.yesBranch,
                        existingNodeID,newNodeID,newQuestAns, decision)) {
                    return(true);
                }
                else {
                    // Try no branch if it exists
                    if (currentNode.noBranch != null) {
                        return(searchTreeAndAddYesNode(currentNode.noBranch,
                                existingNodeID,newNodeID,newQuestAns, decision));
                    }
                    else return(false);	// Not found here
                }
            }
            return(false);		// Not found here
        }
    }

    /* ADD NO NODE */

    public void no(int existingNodeID, int newNodeID, String newQuestAns, Decision decision) {
        // If no root node do nothing

        if (rootNode == null) {
            System.out.println("ERROR: No root node!");
            return;
        }

        // Search tree

        if (searchTreeAndAddNoNode(rootNode,existingNodeID,newNodeID,newQuestAns, decision)) {
            //System.out.println("Added node " + newNodeID +
             //       " onto \"no\" branch of node " + existingNodeID);
        }
        else System.out.println("Node " + existingNodeID + " not found");
    }

    /* SEARCH TREE AND ADD NO NODE */

    private boolean searchTreeAndAddNoNode(
            BinTree currentNode,
            int existingNodeID, int newNodeID, String newQuestAns,
            Decision decision) {
        if (currentNode.nodeID == existingNodeID) {
            // Found node
            if (currentNode.noBranch == null) currentNode.noBranch = new
                    BinTree(newNodeID,newQuestAns, decision);
            else {
                System.out.println("WARNING: Overwriting previous node " +
                        "(id = " + currentNode.noBranch.nodeID +
                        ") linked to yes branch of node " +
                        existingNodeID);
                currentNode.noBranch = new BinTree(newNodeID,newQuestAns, decision);
            }
            return(true);
        }
        else {
            // Try yes branch if it exists
            if (currentNode.yesBranch != null) {
                if (searchTreeAndAddNoNode(currentNode.yesBranch,
                        existingNodeID,newNodeID,newQuestAns, decision)) {
                    return(true);
                }
                else {
                    // Try no branch if it exists
                    if (currentNode.noBranch != null) {
                        return(searchTreeAndAddNoNode(currentNode.noBranch,
                                existingNodeID,newNodeID,newQuestAns, decision));
                    }
                    else return(false);	// Not found here
                }
            }
            else return(false);	// Not found here
        }
    }

    /* --------------------------------------------- */
    /*                                               */
    /*               TREE QUERY METHODS             */
    /*                                               */
    /* --------------------------------------------- */

    private boolean finalOutcome = false;

    public boolean getFinalOutcome() {
        return finalOutcome;
    }

    public void queryBinTree() {
        queryBinTree(rootNode);
        finalOutcome = getLastNode().decision.evaluate(entity);
    }

    private void queryBinTree(BinTree currentNode) {

        // Test for leaf node (answer) and missing branches
        lastNode = currentNode;

        if (currentNode.yesBranch==null) {
            if (currentNode.noBranch==null)
            {
                // reached a leaf
                System.out.println(currentNode.questOrAns);
            }
            else
            {
                // incomplete leaf
                System.out.println("Error: Missing \"Yes\" branch at \"" +
                        currentNode.questOrAns + "\" question");

            }
            return;
        }
        if (currentNode.noBranch==null) {
            // incomplete leaf
            System.out.println("Error: Missing \"No\" branch at \"" +
                    currentNode.questOrAns + "\" question");
            return;
        }

        // proceed with question

        askQuestion(currentNode);

    }


    private BinTree lastNode = null;

    public BinTree getLastNode() {
        return lastNode;
    }

    private void askQuestion(BinTree currentNode) {

        boolean success = currentNode.decision.evaluate(entity);

        decisionLog.add(currentNode.questOrAns + " => " + success);

        if (success)
            queryBinTree(currentNode.yesBranch);
        else
            queryBinTree(currentNode.noBranch);
    }

    /* ----------------------------------------------- */
    /*                                                 */
    /*               TREE OUTPUT METHODS               */
    /*                                                 */
    /* ----------------------------------------------- */

    /* OUTPUT BIN TREE */

    public void outputBinTree() {

        outputBinTree("1",rootNode);
    }

    private void outputBinTree(String tag, BinTree currentNode) {

        // Check for empty node

        if (currentNode == null) return;

        // Output

        //System.out.println("[" + tag + "] nodeID = " + currentNode.nodeID +
        //        ", question/answer = " + currentNode.questOrAns);

        System.out.println("[" + tag + "] "+ currentNode.questOrAns);

        // Go down yes branch

        outputBinTree(tag + ".1", currentNode.yesBranch);

        // Go down no branch

        outputBinTree(tag + ".2",currentNode.noBranch);
    }
}

