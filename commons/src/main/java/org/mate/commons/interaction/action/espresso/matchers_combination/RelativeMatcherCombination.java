package org.mate.commons.interaction.action.espresso.matchers_combination;

import static org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType.WITH_CLASS_NAME;
import static org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType.WITH_CONTENT_DESCRIPTION;
import static org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType.WITH_ID;
import static org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType.WITH_RESOURCE_NAME;
import static org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType.WITH_TEXT;

import androidx.annotation.Nullable;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.AllOfMatcher;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTree;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTreeIterator;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTreeNode;
import org.mate.commons.interaction.action.espresso.view_tree.PathInTree;
import org.mate.commons.interaction.action.espresso.view_tree.PathWithNodes;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents a combination of relative matchers.
 * <p>
 * This class provides methods for:
 * - Building a matcher combination that unequivocally targets a view.
 * - Getting the minimal set of matchers from an existing combination that keeps being unequivocal.
 * - Building an EspressoViewMatcher instance from an existing combination.
 * <p>
 * Basically, it is the core class for building EspressoViewMatchers that unequivocally target
 * views, and therefore do not fail at execution time.
 */
public class RelativeMatcherCombination {

    /**
     * The type of Base matchers to use during construction of a Matcher combination (in order of
     * importance).
     */
    static EspressoViewMatcherType[] BASE_MATCHER_TYPES_FOR_COMBINATION = {
            WITH_RESOURCE_NAME,
            WITH_ID,
            WITH_TEXT,
            WITH_CONTENT_DESCRIPTION,
            WITH_CLASS_NAME,
    };

    /**
     * The ViewTree node targeted by this combination.
     */
    private final EspressoViewTreeNode targetNode;

    /**
     * The ViewTree to which the target node belongs to.
     */
    private final EspressoViewTree viewTree;

    /**
     * The relative matchers in this combination.
     */
    private final List<RelativeMatcher> matchers = new ArrayList<>();

    /**
     * A cache that maps each EspressoView in the ViewTree to a String hash.
     * The hash is built using the information asked by each relative matcher in this combination.
     */
    private final Set<EspressoViewTreeNode> otherNodesWithSameConstraintsAsTarget = new HashSet<>();

    private RelativeMatcherCombination(EspressoViewTreeNode targetNode,
                                       EspressoViewTree viewTree) {
        this.targetNode = targetNode;
        this.viewTree = viewTree;

        // at first, all the other nodes have the same hash as the target node (empty string).
        for (EspressoViewTreeNode aNode : viewTree.getAllNodes()) {
            if (!aNode.equals(targetNode)) {
                this.otherNodesWithSameConstraintsAsTarget.add(aNode);
            }
        }
    }

    private RelativeMatcherCombination(EspressoViewTreeNode targetNode,
                                       EspressoViewTree viewTree,
                                       List<RelativeMatcher> initialMatchers) {
        this(targetNode, viewTree);
        for (RelativeMatcher matcher : initialMatchers) {
            this.addMatcher(matcher);
        }
    }

    /**
     * @return the relative matchers in this combination.
     */
    public List<RelativeMatcher> getMatchers() {
        return matchers;
    }

    /**
     * Adds a relative matcher to this combination.
     *
     * @param matcher to add
     */
    private void addMatcher(RelativeMatcher matcher) {
        matchers.add(matcher);
        String constraintValue = getTargetNodeConstraintValue(matcher);
        updateOtherNodesWithSameConstraintAsTarget(matcher, constraintValue);
    }

    private String getTargetNodeConstraintValue(RelativeMatcher matcher) {
        // First, we get the node to which the relative path in matcher leads us.
        // Since this is for the target node, we use the direction indexes provided in the matcher.
        EspressoViewTreeNode nodeAfterPath = matcher.getPath().walkPathFromNodeUsingDirectionIndexes(targetNode);

        if (nodeAfterPath == null) {
            // This should never happen, since the RelativeMatcher was built specifically for target node.
            throw new IllegalStateException("Unable to get node after path when updating target node hash");
        }

        if (!matcher.getType().isValidForEspressoViewTreeNode(nodeAfterPath)) {
            // Again, this won't happen if the RelativeMatcher built for the target node was assembled correctly.
            throw new IllegalStateException("Found invalid matcher for node after path when updating target node hash");
        }

        // Update the hash based on the type of matcher and the information on the relative node.
        EspressoView espressoViewAfterPath = nodeAfterPath.getEspressoView();

        switch (matcher.getType()) {
            case IS_ROOT:
                return String.valueOf(nodeAfterPath.hasParent());
            case WITH_ID:
                return String.valueOf(espressoViewAfterPath.getId());
            case WITH_CLASS_NAME:
                return String.valueOf(espressoViewAfterPath.getClassName());
            case WITH_RESOURCE_NAME:
                return String.valueOf(espressoViewAfterPath.getResourceEntryName());
            case WITH_TEXT:
                return String.valueOf(espressoViewAfterPath.getText());
            case WITH_CONTENT_DESCRIPTION:
                return String.valueOf(espressoViewAfterPath.getContentDescription());
            default:
                throw new IllegalStateException(String.format("Hash update not implemented for " +
                        "matcher type: %s", matcher.getType()));
        }
    }

    /**
     * Updates the hash of all nodes that currently have the same hash as the target node.
     * Afterwards, we evaluate if some of those nodes now have a different hash.
     *
     * @param matcher to use for update
     */
    private void updateOtherNodesWithSameConstraintAsTarget(RelativeMatcher matcher, String targetNodeConstraint) {
        Iterator<EspressoViewTreeNode> it = otherNodesWithSameConstraintsAsTarget.iterator();

        while (it.hasNext()) {
            EspressoViewTreeNode node = it.next();

            List<EspressoViewTreeNode> nodesFrontierAfterPath = matcher.getPath()
                    .walkPathFromNodeWithoutDirectionIndexes(node);

            boolean foundNodeInFrontierWithSameConstraintAsTarget = false;

            for (EspressoViewTreeNode nodeInFrontier : nodesFrontierAfterPath) {
                if (!matcher.getType().isValidForEspressoViewTreeNode(nodeInFrontier)) {
                    // the matcher is not valid for the relative node.
                    // e.g., WithText matcher for a relative node that has null text.
                    continue;
                }

                EspressoView espressoViewAfterPath = nodeInFrontier.getEspressoView();

                String otherConstraint;
                switch (matcher.getType()) {
                    case IS_ROOT:
                        otherConstraint = String.valueOf(nodeInFrontier.hasParent());
                        break;
                    case WITH_ID:
                        otherConstraint = String.valueOf(espressoViewAfterPath.getId());
                        break;
                    case WITH_CLASS_NAME:
                        otherConstraint = String.valueOf(espressoViewAfterPath.getClassName());
                        break;
                    case WITH_RESOURCE_NAME:
                        otherConstraint = String.valueOf(espressoViewAfterPath.getResourceEntryName());
                        break;
                    case WITH_TEXT:
                        otherConstraint = String.valueOf(espressoViewAfterPath.getText());
                        break;
                    case WITH_CONTENT_DESCRIPTION:
                        otherConstraint = String.valueOf(espressoViewAfterPath.getContentDescription());
                        break;
                    default:
                        throw new IllegalStateException(String.format("Hash update not implemented for " +
                                "matcher type: %s", matcher.getType()));
                }

                if (otherConstraint.equals(targetNodeConstraint)) {
                    foundNodeInFrontierWithSameConstraintAsTarget = true;
                    break;
                }
            }

            if (!foundNodeInFrontierWithSameConstraintAsTarget) {
                it.remove();
            }
        }
    }

    /**
     * Indicates whether this relative matcher combination matches the target node unequivocally
     * or not. This happens when there is only one node with the same hash as the target node:
     * that very same node.
     *
     * @return a boolean
     */
    private boolean isUnequivocal() {
        return otherNodesWithSameConstraintsAsTarget.size() == 0;
    }

    /**
     * Builds a RelativeMatcherCombination that unequivocally targets the provided target node,
     * in the context of the other views also available in the screen. If it is unable to find a
     * unique View Matcher, it returns null.
     * <p>
     * The algorithm proceeds as follows:
     * - It iterates over all nodes in the tree, starting from the target node.
     * - For each of these nodes, we get the relative path that needs to be traversed from the
     * target node to reach it.
     * - Then, for each relative path, we add all possible matcher types (as defined in
     * BASE_MATCHER_TYPES_FOR_COMBINATION) until we find a unequivocal View Matcher. Each Matcher
     * Type defines a type of constraint that can be added to the final View Matcher (e.g.,
     * search for a view with a certain resource id).
     * <p>
     * The algorithm stops once it finds a unequivocal matcher combination.
     * This combination is NOT guaranteed to be minimal, just unequivocal.
     *
     * @param targetNode to match against
     * @param viewTree   of the UI hierarchy.
     * @return a unequivocally targeting matcher combination, null otherwise.
     */
    public static @Nullable
    RelativeMatcherCombination buildUnequivocalCombination(EspressoViewTreeNode targetNode,
                                                           EspressoViewTree viewTree) {
        if (!targetNode.hasParent()) {
            // A node without parent in a tree is, by definition, the root node.
            // Use a special matcher for that case
            return new RelativeMatcherCombination(targetNode, viewTree, Collections.singletonList(
                    new RelativeMatcher(new PathInTree(),
                            EspressoViewMatcherType.IS_ROOT)));
        }

        long startTime = System.nanoTime();

        RelativeMatcherCombination matcherCombination =
                new RelativeMatcherCombination(targetNode, viewTree);

        boolean uniqueMatcherFound = false;

        // traverse all nodes in the tree starting from the target node
        EspressoViewTreeIterator treeIteratorForTargetNode = viewTree.getTreeIteratorForTargetNode(targetNode);
        for (PathWithNodes pathWithNodes : treeIteratorForTargetNode) {
            EspressoViewTreeNode nodeAfterPath = pathWithNodes.getEndingNode();
            PathInTree pathFromTarget = pathWithNodes.getPath();

            for (EspressoViewMatcherType type : BASE_MATCHER_TYPES_FOR_COMBINATION) {
                if (type.isValidForEspressoViewTreeNode(nodeAfterPath)) {
                    matcherCombination.addMatcher(new RelativeMatcher(pathFromTarget, type));
                    if (matcherCombination.isUnequivocal()) {
                        uniqueMatcherFound = true;
                        break;
                    }
                }
            }

            if (uniqueMatcherFound) {
                break;
            }
        }

        if (matcherCombination.isUnequivocal()) {
            matcherCombination = matcherCombination.getMinimalCombination();
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

        String debugMsg = String.format(
                "Matcher combination generation took %d ms (for %d other views). ",
                duration,
                viewTree.getAllNodes().size());
        debugMsg += String.format("Result has %d matchers ", matcherCombination.getMatchers().size());
        if (matcherCombination.isUnequivocal()) {
            debugMsg += "and is unequivocal.";
        } else {
            debugMsg += "and is not unequivocal.";
        }
        MATELog.log_debug(debugMsg);

        if (matcherCombination.isUnequivocal()) {
            return matcherCombination;
        }

        return null;
    }

    /**
     * Returns a minimal variation of the current relative matcher combination represented in
     * this instance. I.e., a unequivocal combination (targeting the same node) that stops being
     * unequivocal after removing any matcher.
     * This method uses Delta Debugging for such task.
     *
     * @return the minimal matcher combination
     */
    public RelativeMatcherCombination getMinimalCombination() {
        if (!isUnequivocal()) {
            throw new IllegalStateException("Can not reduce a combination that is not unequivocal");
        }

        if (matchers.size() == 1) {
            // nothing to do, combination is composed of only one matcher.
            return this;
        }

        List<RelativeMatcher> newMatchers = new ArrayList<>(matchers);

        // N will be the partition size for the Delta Debugging. Initial value is 2.
        int n = 2;

        while (newMatchers.size() >= n) {
            // divide matchers into deltas and their complements
            List<List<RelativeMatcher>> deltas = new ArrayList<>();
            List<List<RelativeMatcher>> complements = new ArrayList<>();
            int partitionSize = (int) Math.floor((float) newMatchers.size() / (float) n);

            for (int start = 0; start < newMatchers.size(); start += partitionSize) {
                int end = Math.min(start + partitionSize, newMatchers.size());
                List<RelativeMatcher> delta = new ArrayList<>(newMatchers.subList(start, end));
                deltas.add(delta);

                List<RelativeMatcher> complement = new ArrayList<>(newMatchers.subList(0, start));
                complement.addAll(newMatchers.subList(end, newMatchers.size()));
                complements.add(complement);
            }

            // remove duplicate entry from complements list that is in deltas list
            complements.removeAll(deltas);

            // test matchers in deltas
            int uniqueSublistIndex = -1;
            for (int i = 0; i < deltas.size(); i++) {
                RelativeMatcherCombination m = new RelativeMatcherCombination(targetNode,
                        viewTree,
                        deltas.get(i));
                if (m.isUnequivocal()) {
                    uniqueSublistIndex = i;
                    break;
                }
            }

            if (uniqueSublistIndex != -1) {
                // reduce to the failing delta in next iteration
                n = 2;
                newMatchers = deltas.get(uniqueSublistIndex);
                continue;
            }

            // test matchers in complements
            int uniqueComplementIndex = -1;
            for (int i = 0; i < complements.size(); i++) {
                RelativeMatcherCombination m = new RelativeMatcherCombination(targetNode,
                        viewTree,
                        complements.get(i));
                if (m.isUnequivocal()) {
                    uniqueComplementIndex = i;
                    break;
                }
            }

            if (uniqueComplementIndex != -1) {
                // reduce to the failing complement in next iteration
                newMatchers = complements.get(uniqueComplementIndex);
                n = n - 1;
            } else {
                // increase granularity, search in a finer space
                n = 2 * n;
                if (n > newMatchers.size()) {
                    break;
                }
            }
        }


        return new RelativeMatcherCombination(targetNode, viewTree, newMatchers);
    }

    /**
     * @return an EspressoViewMatcher with all the matchers in this combination.
     */
    public EspressoViewMatcher getEspressoViewMatcher() {
        AllOfMatcher viewMatcher = new AllOfMatcher();

        for (RelativeMatcher matcher : getMatchers()) {
            viewMatcher.addMatcherForPathInTree(matcher, targetNode);
        }

        return viewMatcher;
    }
}
