package org.mate.commons.interaction.action.espresso.view_tree;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.test.espresso.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing the UI hierarchy of a specific screen.
 * <p>
 * The special thing about this class is that it provides an iterator that starts from a certain
 * view and iterates over all the views in the tree expanding from the starting view.
 * That is, it will visit first the views in the tree closer to the target (e.g., children or
 * parent), and afterwards the more distant ones (e.g., children's children, parent's parent, etc.).
 */
public class EspressoViewTree {

    /**
     * The root of the Window that hosted this ViewTree.
     */
    @Nullable
    private final Root windowRoot;

    /**
     * The root node in the tree.
     */
    @Nullable
    private final EspressoViewTreeNode rootNode;

    /**
     * A map of nodes by View ID.
     * This is useful for quickly finding out if a View's ID is unique or not.
     */
    private final Map<Integer, List<EspressoViewTreeNode>> nodesByViewId = new HashMap<>();

    public EspressoViewTree(Root windowRoot, String activityName) {
        this.windowRoot = windowRoot;
        this.rootNode = new EspressoViewTreeNode(windowRoot.getDecorView(), activityName);

        setUniqueIdForViews();
    }

    @Nullable
    public EspressoViewTreeNode getRootNode() {
        return rootNode;
    }

    @Nullable
    public Root getWindowRoot() {
        return windowRoot;
    }

    /**
     * Set a unique ID for each node in the tree at construction.
     */
    private void setUniqueIdForViews() {
        // Save the nodes associated with each View ID.
        for (EspressoViewTreeNode node : getAllNodes()) {
            Integer viewId = node.getEspressoView().getId();

            if (!nodesByViewId.containsKey(viewId)) {
                nodesByViewId.put(viewId, new ArrayList<>());
            }

            nodesByViewId.get(viewId).add(node);
        }

        // Generate the unique IDs for all nodes
        for (EspressoViewTreeNode node : getAllNodes()) {
            node.getEspressoView().generateUniqueId(this);
        }
    }

    /**
     * @return all nodes in the tree.
     */
    public List<EspressoViewTreeNode> getAllNodes() {
        if (rootNode == null) {
            return new ArrayList<>();
        }

        return rootNode.getAllNodesInSubtree();
    }

    /**
     * Returns an iterator that start at a certain node and then traverses the whole ViewTree.
     * It will visit first the views closer to the starting node.
     *
     * @param startingNode the node from which to start the iterator
     * @return an iterator
     */
    public EspressoViewTreeIterator getTreeIteratorForTargetNode(EspressoViewTreeNode startingNode) {
        if (startingNode == null) {
            return new EspressoViewTreeIterator();
        }

        return new EspressoViewTreeIterator(startingNode);
    }

    /**
     * Find the node in the tree corresponding to a view.
     *
     * @param view to find.
     * @return the found node, null otherwise.
     */
    public @Nullable
    EspressoViewTreeNode findNodeForView(View view) {
        for (EspressoViewTreeNode node : this.getAllNodes()) {
            if (node.getEspressoView().getView().equals(view)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Find the nodes in the tree corresponding to a view ID.
     *
     * @param viewId to find.
     * @return a list of nodes.
     */
    public List<EspressoViewTreeNode> getNodesById(Integer viewId) {
        return nodesByViewId.get(viewId);
    }
}
